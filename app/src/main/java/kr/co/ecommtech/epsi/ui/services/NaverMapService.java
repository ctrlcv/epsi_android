package kr.co.ecommtech.epsi.ui.services;

import kr.co.ecommtech.epsi.ui.data.AddressResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface NaverMapService {
    @Headers({"X-NCP-APIGW-API-KEY-ID: krxte7cv3q", "X-NCP-APIGW-API-KEY: ty9rMTgdjiNxie1sTav7D10uJiSb0rrxtgU0Qza2"})
    @GET("/map-geocode/v2/geocode")
    public Call<AddressResponse> searchAddress(@Query("query") String query);
}
