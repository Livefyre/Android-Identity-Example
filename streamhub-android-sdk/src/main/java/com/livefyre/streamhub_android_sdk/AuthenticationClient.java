package com.livefyre.streamhub_android_sdk;

import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;

/**
 * Created by Habi on 15/07/16.
 */
public class AuthenticationClient {

    public static void authenticate(String environment,
                                    String origin,
                                    String referer,
                                    String cookie,
                                    JsonHttpResponseHandler handler)
            throws UnsupportedEncodingException {
        final String authEndpoint =
                authEndpoint(environment, origin, referer, cookie);
        HttpClient.client.get(authEndpoint, handler);
    }

    public static String authEndpoint(String environment,
                                      String origin,
                                      String referer,
                                      String cookie)
            throws UnsupportedEncodingException {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(LivefyreConfig.scheme)
                .authority(LivefyreConfig.identityDomain + "." + environment)
                .authority(LivefyreConfig.getConfiguredNetworkID())
                .appendPath("api")
                .appendPath("v1.0")
                .appendPath("public")
                .appendPath("profile");
        uriBuilder
                .appendQueryParameter("origin", origin)
                .appendQueryParameter("referer", referer)
                .appendQueryParameter("cookie", cookie);
        Log.d("Auth URL", "" + uriBuilder.toString());
        return uriBuilder.toString();
    }
}
