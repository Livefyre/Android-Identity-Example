package com.kvana.streamhub_android_sdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kvana.streamhub_android_sdk.network.RetrofitHandler;
import com.kvana.streamhub_android_sdk.util.Constant;
import com.kvana.streamhub_android_sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationActivity extends BaseActivity {

    private class LoginWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading() called with: " + " url = [" + url + "]");
            if (url.contains("AuthCanceled")) {
                cancelResult();
            } else if (url.contains("jwtProfileToken=")) {
                adminClient(url.split("\\?")[1].split("&")[0].split("=")[1]);
            } else if (url.contains("lftoken")) {
                adminClient(url.split("#")[1].split(":")[1]);
            } else if (url.equals("http://livefyre-cdn-dev.s3.amazonaws.com/demos/lfep2-comments.html")) {
                getCookie(CookieManager.getInstance(), URL);
            } else {
                webview.loadUrl(url);
            }
            return true;
        }
    }

    private static final String TAG = AuthenticationActivity.class.getName();
    private WebView webview;
    //    private String URL = "https://identity.qa-ext.livefyre.com/qa-blank.fyre.co/pages/auth/engage/?app=https%3A%2F%2Fidentity.qa-ext.livefyre.com%2Fqa-blank.fyre.co&next=aHR0cDovL2xpdmVmeXJlLWNkbi1kZXYuczMuYW1hem9uYXdzLmNvbS9kZW1vcy9sZmVwMi1jb21tZW50cy5odG1s";
    private String URL = "https://identity.qa-ext.livefyre.com/qa-blank.fyre.co/pages/auth/engage/?app=https%3A%2F%2Fidentity.qa-ext.livefyre.com%2Fqa-blank.fyre.co&next=aHR0cDovL2xpdmVmeXJlLWNkbi1kZXYuczMuYW1hem9uYXdzLmNvbS9kZW1vcy9sZmVwMi1jb21tZW50cy5odG1s";

    public static String DATA = "data";
    public static final int AUTHENTICATION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        webview = (WebView) findViewById(R.id.webview);
        //removing all previous cookies
//        CookieManager.getInstance().removeAllCookie();
        //TODO : remove single cookie
//        CookieManager.getInstance().setAcceptCookie(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new LoginWebViewClient());
        webview.loadUrl(URL);
    }

    private void sendResult(String token) {
        Intent intent = new Intent();
        intent.putExtra(DATA, token);
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

    private void adminClient(String lfToken) {
        RetrofitHandler.getInstance().adminClient(
                Util.stringToBase64(Constant.ARTICLE_ID),
                lfToken,
                Constant.SITE_ID).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccess()) {
                    Log.d(TAG, "adminClient-onResponse: " + response.body());
                    sendResult(response.body());
                } else {
                    cancelResult();
                    Log.e(TAG, "adminClient-onResponse: " + response.raw());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                cancelResult();
                Log.e(TAG, "adminClient-onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    public void getCookie(CookieManager cookieManager, String siteName) {
        String cookies = cookieManager.getCookie(siteName);
        String token = cookies.split(";")[2];
        token = token.substring(token.indexOf("=")+2, token.length());
        try {
            JSONObject jsonObject = new JSONObject(Util.base64ToString(token));
            adminClient(jsonObject.optString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
