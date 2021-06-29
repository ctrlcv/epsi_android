package kr.co.ecommtech.epsi.ui.services;

import java.util.HashMap;

import kr.co.ecommtech.epsi.ui.data.LogIn;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface QueryService {
    String SERVER_URL = "http://121.137.122.187:3108"; //121.137.122.187:3108"; //192.168.0.26:3108";

    @Headers({"Accept: applcation/json", "Content-Type: application/json; charset=utf-8"})
    @POST("/api/users/signin")
    Call<LogIn> logIn(@Body HashMap<String, String> map);
}
