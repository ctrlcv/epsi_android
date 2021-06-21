package kr.co.ecommtech.epsi.ui.network;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import kr.co.ecommtech.epsi.ui.services.QueryService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClientToken {
    private static final  String TAG = "HttpClientToken";

    private static Retrofit retrofit;
    private static String currentAccessToken;

    public static Retrofit getRetrofit() {
//        if (LoginManager.getInstance().getLoginInfo() != null) {
//            if (LoginManager.getInstance().getLoginInfo().getAccessToken() != null) {
//                return getRetrofit(LoginManager.getInstance().getLoginInfo().getAccessToken());
//            }
//        }

        return HttpClient.getRetrofit();
    }

    public static Retrofit getRetrofit(String authToken) {
//        Log.d(TAG, "getRetrofit() authToken:" + authToken);
//        Log.d(TAG, "getRetrofit() getCurrentAccessToken():" + getCurrentAccessToken());

        if (retrofit != null &&
                !TextUtils.isEmpty(getCurrentAccessToken()) &&
                getCurrentAccessToken().equals(authToken)) {
            Log.d(TAG, "getRetrofit() return retrofit");
            return retrofit;
        } else {
            retrofit = null;
            setCurrentAccessToken(authToken);
        }

        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("x-access-token", authToken)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(httpClient);
        builder.baseUrl(QueryService.SERVER_URL);
        builder.addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();
        Log.d(TAG, "getRetrofit() return new retrofit");

        return retrofit;
    }

    public static String getCurrentAccessToken() {
        return currentAccessToken;
    }

    public static void setCurrentAccessToken(String currentAccessToken) {
        HttpClientToken.currentAccessToken = currentAccessToken;
    }
}
