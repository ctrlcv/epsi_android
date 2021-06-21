package kr.co.ecommtech.epsi.ui.network;

import kr.co.ecommtech.epsi.ui.services.QueryService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(QueryService.SERVER_URL);
            builder.addConverterFactory(GsonConverterFactory.create());

            retrofit = builder.build();
        }

        return retrofit;
    }
}
