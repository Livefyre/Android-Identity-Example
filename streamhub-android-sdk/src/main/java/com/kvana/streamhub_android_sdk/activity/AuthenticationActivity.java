package com.kvana.streamhub_android_sdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kvana.streamhub_android_sdk.R;
import com.kvana.streamhub_android_sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationActivity extends BaseActivity {

    private class LoginWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String cookies = CookieManager.getInstance().getCookie(URL);
            if (url.contains("AuthCanceled")) {
                cancelResult();
            } else if (cookies != null && cookies.contains("")) {
                getCookie(cookies, url);
            } else {
                webview.loadUrl(url);
            }
            return true;
        }
    }

    private static final String TAG = AuthenticationActivity.class.getName();
    public static final int AUTHENTICATION_REQUEST_CODE = 200;
    public static String TOKEN = "token";
    public static String ENVIRONMENT = "environment";
    public static String NETWORK = "token";
    public static String ENCODED_URL = "encodedUrlParamString";
    public static String NEXT = "next";
    private String environment, network, encodedUrlParamString, next;
    private WebView webview;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        environment = getIntent().getStringExtra(ENVIRONMENT);
        network = getIntent().getStringExtra(NETWORK);
        encodedUrlParamString = getIntent().getStringExtra(ENCODED_URL);
        next = getIntent().getStringExtra(NEXT);

        if (environment == null || environment.length() == 0) {
            showToast("Environment is empty.");
            finish();
        }

        if (network == null || network.length() == 0) {
            showToast("Network is empty.");
            finish();
        }

        if (encodedUrlParamString == null || encodedUrlParamString.length() == 0) {
            showToast("Encoded Url is empty.");
            finish();
        }

        if (next == null || next.length() == 0) {
            showToast("Next is empty.");
            finish();
        }

        URL = String.format("https://identity.%s/%s/pages/auth/engage/?app=%s&next=%s", environment, network, encodedUrlParamString, next);
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new LoginWebViewClient());
        webview.loadUrl(URL);
    }

    private void sendResult(String token) {
        Intent intent = new Intent();
        intent.putExtra(TOKEN, token);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void cancelResult() {
        showToast("Failed to authenticate user..");
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelResult();
    }

    public void getCookie(String cookies, String url) {
        if (!cookies.contains("lfsp-profile")) {
            webview.loadUrl(url);
            return;
        }
        String token = cookies.split(";")[2];
        token = token.substring(token.indexOf("=") + 2, token.length());
        try {
            JSONObject jsonObject = new JSONObject(Util.base64ToString(token));
            if (jsonObject.optString("token") == null || jsonObject.optString("token").length() == 0) {
                webview.loadUrl(url);
                return;
            }
            sendResult(jsonObject.optString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
