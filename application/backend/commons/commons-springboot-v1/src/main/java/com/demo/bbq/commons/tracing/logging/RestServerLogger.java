package com.demo.bbq.commons.tracing.logging;

import com.demo.bbq.commons.properties.ConfigurationBaseProperties;
import com.demo.bbq.commons.tracing.logging.constants.LoggingMessage;
import com.demo.bbq.commons.tracing.logging.injector.ThreadContextInjectorUtil;
import com.demo.bbq.commons.tracing.logging.obfuscation.body.BodyObfuscatorUtil;
import com.demo.bbq.commons.tracing.logging.obfuscation.header.HeaderObfuscatorUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
public class RestServerLogger implements HandlerInterceptor {

  private final ConfigurationBaseProperties properties;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    decorateRequest(properties, request);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    decorateResponse(properties, request, response);
  }

  private static void decorateRequest(ConfigurationBaseProperties properties, HttpServletRequest request) {
    String method = request.getMethod();
    String url = getFullRequestURL(request);
    String requestBody = BodyObfuscatorUtil.process(properties.getObfuscation(), extractRequestBody(request));
    Map<String, String> requestHeaders = extractRequestHeaders(request);
    String obfuscatedHeaders = HeaderObfuscatorUtil.process(properties.getObfuscation(), requestHeaders);

    ThreadContextInjectorUtil.populateFromHeaders(requestHeaders);
    ThreadContextInjectorUtil.populateFromRestServerRequest(method, url, obfuscatedHeaders, requestBody);
    log.info(LoggingMessage.REST_SERVER_REQUEST);
  }

  //ToDo: Add responseBody in logs
  private static void decorateResponse(ConfigurationBaseProperties properties,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
    String method = request.getMethod();
    String url = getFullRequestURL(request);
    Map<String, String> responseHeaders = extractResponseHeaders(response);
    String obfuscatedHeaders = HeaderObfuscatorUtil.process(properties.getObfuscation(), responseHeaders);
    String httpCode = String.valueOf(response.getStatus());

    ThreadContextInjectorUtil.populateFromHeaders(responseHeaders);
    ThreadContextInjectorUtil.populateFromRestServerResponse(method, url, obfuscatedHeaders, StringUtils.EMPTY, httpCode);
    log.info(LoggingMessage.REST_SERVER_RESPONSE);
  }

  private static String extractRequestBody(HttpServletRequest request) {
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader bufferedReader = request.getReader()) {
      bufferedReader.lines().forEach(stringBuilder::append);
    } catch (IOException e) {
      log.error("Error reading request body", e);
    }
    return stringBuilder.toString();
  }

  private static Map<String, String> extractRequestHeaders(HttpServletRequest request) {
    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.put(headerName, request.getHeader(headerName));
    }
    return headers;
  }

  private static Map<String, String> extractResponseHeaders(HttpServletResponse response) {
    Map<String, String> headers = new HashMap<>();
    Collection<String> headerNames = response.getHeaderNames();
    headerNames.forEach(headerName -> headers.put(headerName, response.getHeader(headerName)));
    return headers;
  }

  private static String getFullRequestURL(HttpServletRequest request) {
    String url = Optional.of(request)
        .map(HttpServletRequest::getRequestURL)
        .map(StringBuffer::toString)
        .orElse(StringUtils.EMPTY);

    String queryString = request.getQueryString();
    if (StringUtils.isNotBlank(queryString))
      url += "?" + queryString;

    return url;
  }
}
