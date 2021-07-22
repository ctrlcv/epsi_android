package kr.co.ecommtech.epsi.ui.services;

import java.util.HashMap;

import kr.co.ecommtech.epsi.ui.data.GroupCodeList;
import kr.co.ecommtech.epsi.ui.data.LogIn;
import kr.co.ecommtech.epsi.ui.data.MaterialCodeList;
import kr.co.ecommtech.epsi.ui.data.PipeList;
import kr.co.ecommtech.epsi.ui.data.TypeCodeList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface QueryService {
    String SERVER_URL = "http://139.150.83.28:3108";// "http://139.150.83.28:3108"; //http://192.168.0.26:3108";

    @Headers({"Accept: applcation/json", "Content-Type: application/json; charset=utf-8"})
    @POST("/api/users/signin")
    Call<LogIn> logIn(@Body HashMap<String, String> map);

    @Headers({"Accept: applcation/json", "Content-Type: application/json; charset=utf-8"})
    @POST("/api/epsi/groupcd")
    Call<GroupCodeList> getPipeGroupCodes();

    @Headers({"Accept: applcation/json", "Content-Type: application/json; charset=utf-8"})
    @POST("/api/epsi/typecd")
    Call<TypeCodeList> getPipeTypeCodes();

    @Headers({"Accept: applcation/json", "Content-Type: application/json; charset=utf-8"})
    @POST("/api/epsi/materialcd")
    Call<MaterialCodeList> getPipeMaterialCodes();

    @Headers({"Accept: applcation/json", "Content-Type: application/json; charset=utf-8"})
    @POST("/api/epsi/epsi")
    Call<PipeList> getPipeList(@Body HashMap<String, Double> map);

}
