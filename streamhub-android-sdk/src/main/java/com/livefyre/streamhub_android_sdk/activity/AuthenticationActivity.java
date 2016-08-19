package com.livefyre.streamhub_android_sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kvana.streamhub_android_sdk.R;
import com.livefyre.streamhub_android_sdk.network.AuthenticationClient;
import com.livefyre.streamhub_android_sdk.util.LivefyreConfig;
import com.livefyre.streamhub_android_sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class AuthenticationActivity extends BaseActivity {
    private class AuthCallback implements AuthenticationClient.ResponseHandler {
        @Override
        public void success(String res, String error) {
            dismissProgressDialog();
            Log.d(TAG, "onSuccess: " + res.toString());
            try {
                JSONObject resJsonObj = new JSONObject(res);

                JSONObject jsonObject = resJsonObj.optJSONObject("data");
                String email = jsonObject.optString("email");
                if (email == null || email.equals("") || email.equals("null")) {
                    webview.setVisibility(View.VISIBLE);
                    authCallCompleted = true;
                    webview.loadUrl(String.format("https://identity.%s/%s/pages/profile/complete/?next=%s", environment, network, next));
                } else {
                    respond();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failure(String msg) {
            dismissProgressDialog();
            respond();
        }
    }

    private class LoginWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String cookies = CookieManager.getInstance().getCookie(URL);
            if (url.contains("AuthCanceled")) {
                cancelResult();
            } else if (cookies != null && cookies.contains("") && cookies.contains(KEY_COOKIE) && authCallCount == 0) {
                authCallCount++;
                validateToken(url);
                try {
                    showProgressDialog();
                    AuthenticationClient.authenticate(
                            environment,
                            LivefyreConfig.origin,
                            LivefyreConfig.referer,
                            cookies,
                            new AuthCallback());
                    webview.setVisibility(View.GONE);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (authCallCount > 0 && authCallCompleted) {
                sendResult(getToken());
            }
            return false;
        }
    }

    private static final String TAG = AuthenticationActivity.class.getName();
    public static final int AUTHENTICATION_REQUEST_CODE = 200;
    public static String KEY_COOKIE = "lfsp-profile=";
    public static String TOKEN = "token";
    public static String ENVIRONMENT = "environment";
    public static String NETWORK_ID = "network_id";
    public static String ENCODED_URL = "encoded_url_param_string";
    public static String NEXT = "next";
    private String environment, network, encodedUrlParamString, next;
    private WebView webview;
    private Toolbar toolbar;
    private static String URL;
    private boolean authCallCompleted;
    private int authCallCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        CookieManager.getInstance().removeAllCookie();

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

        buildToolBar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        respond();
    }

    private void buildToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar
        setSupportActionBar(toolbar);
        //disable title on toolbar
        if (null != getSupportActionBar()) getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView cancel_txt = (TextView) findViewById(R.id.cancel_txt);
        if (cancel_txt != null)
            cancel_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    respond();
                }
            });
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

    private void respond() {
        String token = getToken();
        if (token == null || token.length() == 0) {
            cancelResult();
        } else {
            sendResult(getToken());
        }
    }

    private void validateToken(String url) {
        String token = getToken();
        //if token not found just load Url with redirection Url
        if (token == null || token.length() == 0) {
            webview.loadUrl(url);
            return;
        }
    }

    /**
     * starts authentication activity
     *
     * @param activity              your activity
     * @param environment           environment
     * @param networkId             networkId
     * @param encodedUrlParamString encodedUrlParamString
     * @param next                  next
     */
    public static void start(Activity activity, String environment, String networkId, String encodedUrlParamString, String next) {
        Intent authenticationActivity = new Intent(activity, AuthenticationActivity.class);
        authenticationActivity.putExtra(AuthenticationActivity.ENVIRONMENT, environment);
        authenticationActivity.putExtra(AuthenticationActivity.NETWORK_ID, networkId);
        authenticationActivity.putExtra(AuthenticationActivity.ENCODED_URL, encodedUrlParamString);
        authenticationActivity.putExtra(AuthenticationActivity.NEXT, next);
        activity.startActivityForResult(authenticationActivity, AuthenticationActivity.AUTHENTICATION_REQUEST_CODE);
    }

    /**
     * delete session
     */
    public static void logout() {
        CookieManager.getInstance().removeAllCookie();
    }

    /**
     * get token
     *
     * @return lf token
     */
    public static String getToken() {
        if (null == URL) return "";

        String token = "";
        String cookies = CookieManager.getInstance().getCookie(URL);

        if (null == cookies) return "";

        if ((!cookies.contains("sessionid")) && (!cookies.contains("lfsp-profile")))
            return "";

        if (cookies.split(";").length < 2) return "";


        String inToken = cookies.split(";")[1];

        if (inToken == null) return "";

        if (inToken.length() < 0) return "";

        inToken = inToken.replace("\"", "");

        if (!inToken.contains("=")) return "";

        inToken = inToken.substring(inToken.indexOf("=") + 1, inToken.length());

        try {
            JSONObject tokenobject = new JSONObject(Util.base64ToString(inToken));
            token = tokenobject.optString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return token;
    }
}
