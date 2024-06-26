package com.demo.bbq.commons.interceptor;

import com.demo.bbq.commons.errors.serializer.ErrorSerializerUtil;
import com.demo.bbq.commons.properties.ConfigurationBaseProperties;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;

@Slf4j
public class ExchangeInterceptorUtil {

  private ExchangeInterceptorUtil() {}

  public static Response handleError(Interceptor.Chain chain,
                                     ConfigurationBaseProperties properties) {
    Response response = null;
    try {
      response = chain.proceed(chain.request());
      if(!response.isSuccessful() && response.body() != null) {}
    } catch (IOException exception) {
      ErrorSerializerUtil.propagate(exception);
    }
    return response;
  }
}