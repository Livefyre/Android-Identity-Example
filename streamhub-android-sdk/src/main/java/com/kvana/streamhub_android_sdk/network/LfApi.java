package com.kvana.streamhub_android_sdk.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Hari on 02/06/16.
 */
public interface LfApi {
    @GET("/api/v3.0/auth")
    Call<String> adminClient(@Query("articleId") String articleId, @Query("lftoken") String lfToken, @Query("siteId") String siteId);
}
