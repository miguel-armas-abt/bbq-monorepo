package com.demo.bbq.business.orderhub.infrastructure.repository.restclient.menu.menuv1.retrofit;

import com.demo.bbq.business.orderhub.infrastructure.repository.restclient.menu.dto.MenuOptionDto;
import com.demo.bbq.business.orderhub.infrastructure.repository.restclient.menu.dto.MenuOptionRequestDto;
import io.reactivex.Single;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.*;

public interface MenuV1Api {

  @Headers({"Accept: application/json"})
  @GET("menu-options/{id}")
  Single<MenuOptionDto> findById(@Path(value = "id") Long id);

  @Headers({"Accept: application/json"})
  @GET("menu-options")
  Single<List<MenuOptionDto>> findByCategory(@Query("category") String category);

  @POST("menu-options")
  Single<ResponseBody> save(@Body MenuOptionRequestDto menuOptionRequest);

  @PUT("menu-options/{id}")
  Single<ResponseBody> update(@Path(value = "id") Long id, @Body MenuOptionRequestDto menuOptionRequest);

  @DELETE("menu-options/{id}")
  Single<Response<Void>> delete(@Path(value = "id") Long id);
}