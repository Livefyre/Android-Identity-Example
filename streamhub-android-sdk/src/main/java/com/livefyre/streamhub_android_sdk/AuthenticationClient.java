package com.livefyre.streamhub_android_sdk;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.livefyre.streamhub_android_sdk.HttpClient;
import com.livefyre.streamhub_android_sdk.LivefyreConfig;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Created by kvanamac3 on 7/22/16.
 */
public class AuthenticationClient {

    public static void authenticate(Context context, String environment,
                                    String origin,
                                    String referer,
                                    String cookie,
                                    JsonHttpResponseHandler handler) throws UnsupportedEncodingException {

        final String authEndpoint = authEndpoint(environment);

        Header[] headers = new Header[3];
        headers[0] = new BasicHeader("origin", origin);
        headers[1] = new BasicHeader("referer", referer);
        headers[2] = new BasicHeader("cookie", cookie);

        HttpClient.client.get(context, authEndpoint, headers, null, handler);
    }

    public static String authEndpoint(String environment) throws UnsupportedEncodingException {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(LivefyreConfig.scheme)
                .authority(LivefyreConfig.identityDomain + "." + environment)
                .appendPath(LivefyreConfig.getConfiguredNetworkID())
                .appendPath("api")
                .appendPath("v1.0")
                .appendPath("public")
                .appendPath("profile");

        Log.d("Auth URL", "" + uriBuilder.toString());

        return uriBuilder.toString();
    }
}