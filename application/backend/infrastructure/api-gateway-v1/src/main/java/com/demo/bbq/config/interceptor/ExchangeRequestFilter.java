package com.demo.bbq.config.interceptor;

import com.demo.bbq.utils.restclient.webclient.logging.ExchangeRequestFilterUtil;
import com.demo.bbq.utils.restclient.webclient.obfuscation.header.strategy.HeaderObfuscationStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ExchangeRequestFilter implements ExchangeFilterFunction {

  private final LoggingProperties properties;
  private final List<HeaderObfuscationStrategy> headerObfuscationStrategies;

  @Override
  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    if (!properties.isEnabled()) {
      return next.exchange(request);
    }
    return next.exchange(ExchangeRequestFilterUtil.buildClientRequestDecorator(properties, headerObfuscationStrategies, request));
  }
}