package com.demo.bbq.infrastructure.apigateway.util.exception;

import com.demo.bbq.support.constant.CharacterConstant;
import com.demo.bbq.support.exception.catalog.ApiExceptionType;
import com.demo.bbq.support.exception.model.ApiException;
import com.demo.bbq.support.exception.model.builder.ApiExceptionBuilder;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Getter
public enum ApiGatewayException {

  ERROR0000(ApiExceptionType.AUTH_RULES, "Missing  Authorization header"),
  ERROR0001(ApiExceptionType.AUTH_RULES, "Bad Authorization structure"),
  ERROR0002(ApiExceptionType.AUTH_RULES, "Roles missing"),
  ERROR0003(ApiExceptionType.GATEWAY_CONNECTION, "Gateway connection error");

  private final String serviceNumber = "in.001";
  private final ApiExceptionType type;
  private final String message;

  private final Supplier<String> generateErrorCode = () ->
      this.getServiceNumber()
          .concat(CharacterConstant.DOT)
          .concat(this.getType().getCode())
          .concat(CharacterConstant.DOT)
          .concat(this.name().toLowerCase());

  public ApiException buildException(Throwable cause) {
    return buildApiException()
        .cause(cause)
        .build();
  }

  public ApiException buildException() {
    return buildApiException()
        .build();
  }

  private ApiExceptionBuilder buildApiException() {
    return ApiException.builder()
        .errorCode(this.generateErrorCode.get())
        .message(this.message)
        .type(this.type.getDescription())
        .status(this.type.getHttpStatus());
  }

}