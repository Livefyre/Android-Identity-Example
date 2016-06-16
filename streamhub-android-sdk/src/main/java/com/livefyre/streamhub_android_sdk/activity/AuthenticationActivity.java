package com.livefyre.streamhub_android_sdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kvana.streamhub_android_sdk.R;
import com.livefyre.streamhub_android_sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationActivity extends BaseActivity {

    private class LoginWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String cookies = CookieManager.getInstance().getCookie(URL);
            if (url.contains("AuthCanceled")) {
                cancelResult();
            } else if (cookies != null && cookies.contains("")) {
                getTokenOut(cookies, url);
            } else {
                webview.loadUrl(url);
            }
            return true;
        }
    }

    private static final String TAG = AuthenticationActivity.class.getName();
    public static final int AUTHENTICATION_REQUEST_CODE = 200;
    public static String KEY_COOKIE = "lfsp-profile";
    public static String TOKEN = "token";
    public static String ENVIRONMENT = "environment";
    public static String NETWORK_ID = "network_id";
    public static String ENCODED_URL = "encoded_url_param_string";
    public static String NEXT = "next";
    private String environment, network, encodedUrlParamString, next;
    private WebView webview;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        environment = getIntent().getStringExtra(ENVIRONMENT);
        network = getIntent().getStringExtra(NETWORK_ID);
        encodedUrlParamString = getIntent().getStringExtra(ENCODED_URL);
        next = getIntent().getStringExtra(NEXT);

        //validating url params
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
        //Preparing Url if all params are ok
        URL = String.format("https://identity.%s/%s/pages/auth/engage/?app=%s&next=%s", environment, network, encodedUrlParamString, next);
        //Configure WebView
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new LoginWebViewClient());
        //load url
        webview.loadUrl(URL);
    }

    /**
     * Sends result to requested activity
     *
     * @param token - Requested token
     */
    private void sendResult(String token) {
        Intent intent = new Intent();
        intent.putExtra(TOKEN, token);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * it sends request canceled info to requested activity
     */
    private void cancelResult() {
        showToast("Authenticate request cancelled..");
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //sending cancel info to reqested activity on back pressed
        cancelResult();
    }

    /**
     * Gets token out from the cookies if found.
     * Then send back to the Requested activity
     *
     * @param cookies - Cookies String
     * @param url     - redirection Url to reload if cookies not found
     */
    public void getTokenOut(String cookies, String url) {
        //If requested cookie key not found not process skip processing the string for token
        if (!cookies.contains(KEY_COOKIE)) {
            webview.loadUrl(url);
            return;
        }
        //Process cookie string
        String token = cookies.split(";")[2];
        token = token.replace("\"", "");
        token = token.substring(token.indexOf("=") + 1, token.length());
        try {
            JSONObject jsonObject = new JSONObject(Util.base64ToString(token));
            //if token not found just load Url with redirection Url
            if (jsonObject.optString(TOKEN) == null || jsonObject.optString(TOKEN).length() == 0) {
                webview.loadUrl(url);
                return;
            }
            //sending result to requested activity
            sendResult(jsonObject.optString(TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
