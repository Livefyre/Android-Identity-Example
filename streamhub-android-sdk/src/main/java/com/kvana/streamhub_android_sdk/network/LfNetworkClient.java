package com.kvana.streamhub_android_sdk.network;

import com.kvana.streamhub_android_sdk.util.Constant;
import com.kvana.streamhub_android_sdk.util.StringConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hari on 02/06/16.
 */
public class LfNetworkClient {
    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.MINUTES)
            .connectTimeout(3, TimeUnit.MINUTES)
            .build();
    private static LfNetworkClient ourInstance = new LfNetworkClient();

    public static LfNetworkClient getInstance() {
        return ourInstance;
    }

    private LfNetworkClient() {
    }

    private Retrofit lfRetrofit = new Retrofit.Builder()
            .baseUrl(Constant.url.BASE_URL)
            .addConverterFactory(StringConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();
    private LfApi lfApi = lfRetrofit.create(LfApi.class);

    public Call<String> adminClient(String articleId, String lfToken, String siteId) {
        return lfApi.adminClient(articleId, lfToken, siteId);
    }
}
