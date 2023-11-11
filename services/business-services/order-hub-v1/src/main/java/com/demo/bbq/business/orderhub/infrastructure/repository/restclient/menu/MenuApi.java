package com.demo.bbq.business.orderhub.infrastructure.repository.restclient.menu;

import com.demo.bbq.business.orderhub.infrastructure.repository.restclient.menu.dto.MenuOptionDto;
import com.demo.bbq.business.orderhub.infrastructure.repository.restclient.menu.dto.MenuOptionRequestDto;
import com.demo.bbq.business.orderhub.infrastructure.repository.restclient.menu.properties.MenuOptionSelectorClassProperties;
import com.demo.bbq.support.exception.model.ApiException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MenuApi {

  private static final String API_CLIENT = "menu-option";
  private final List<MenuApiConnector> menuApiConnectorList;
  private final MenuOptionSelectorClassProperties properties;

  private MenuApiConnector getService(Class<?> selectorClass) {
    return menuApiConnectorList.stream()
        .filter(service -> service.supports(selectorClass))
        .findFirst()
        .orElseThrow(NullPointerException::new);
  }

  @CircuitBreaker(name = API_CLIENT, fallbackMethod = "findByIdFallback")
  public Mono<MenuOptionDto> findById(Long id) {
    return getService(properties.getSelectorClass()).findById(id);
  }

  @CircuitBreaker(name = API_CLIENT, fallbackMethod = "findByCategoryFallback")
  public Flux<MenuOptionDto> findByCategory(String category) {
    return getService(properties.getSelectorClass()).findByCategory(category);
  }

  @CircuitBreaker(name = API_CLIENT, fallbackMethod = "saveFallback")
  public Mono<Void> save(MenuOptionRequestDto menuOptionRequest) {
    return getService(properties.getSelectorClass()).save(menuOptionRequest);
  }

  @CircuitBreaker(name = API_CLIENT, fallbackMethod = "updateFallback")
  public Mono<Void> update(Long id, MenuOptionRequestDto menuOptionRequest) {
    return getService(properties.getSelectorClassFallback()).update(id, menuOptionRequest);
  }

  @CircuitBreaker(name = API_CLIENT, fallbackMethod = "deleteFallback")
  public Mono<Void> delete(Long id) {
    return getService(properties.getSelectorClassFallback()).delete(id);
  }

  public Mono<MenuOptionDto> findByIdFallback(Long id, Throwable throwable) {
    validateException(throwable);
    return getService(properties.getSelectorClassFallback()).findById(id);
  }

  public Flux<MenuOptionDto> findByCategoryFallback(String category, Throwable throwable) {
    validateException(throwable);
    return getService(properties.getSelectorClassFallback()).findByCategory(category);
  }

  public Mono<Void> saveFallback(MenuOptionRequestDto menuOptionRequest, Throwable throwable) {
    validateException(throwable);
    return getService(properties.getSelectorClassFallback()).save(menuOptionRequest);
  }

  public Mono<Void> updateFallback(Long id, MenuOptionRequestDto menuOptionRequest, Throwable throwable) {
    validateException(throwable);
    return getService(properties.getSelectorClassFallback()).update(id, menuOptionRequest);
  }

  public Mono<Void> deleteFallback(Long id, Throwable throwable) {
    validateException(throwable);
    return getService(properties.getSelectorClassFallback()).delete(id);
  }

  private static void validateException(Throwable throwable) {
    if (throwable instanceof ApiException) {
      ApiException apiException = (ApiException) throwable;
      if(isFunctionalError.test(apiException)) {
        throw apiException;
      }
    }
  }

  private static final Predicate<ApiException> isFunctionalError = apiException ->
      apiException.getHttpStatus().equals(HttpStatus.BAD_REQUEST) || apiException.getHttpStatus().equals(HttpStatus.NOT_FOUND);
}