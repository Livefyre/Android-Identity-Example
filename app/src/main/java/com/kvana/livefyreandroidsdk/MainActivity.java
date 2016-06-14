package com.kvana.livefyreandroidsdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kvana.streamhub_android_sdk.activity.AuthenticationActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView token_tv;
    private Button sign_in_btn;
    private ImageView iv_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        token_tv = (TextView) findViewById(R.id.tv_name);
        sign_in_btn = (Button) findViewById(R.id.btn_sign_in);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        sign_in_btn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AuthenticationActivity.AUTHENTICATION_REQUEST_CODE) {
                //you can receive token here with key - TOKEN
                try {
                    JSONObject jsonObject = new JSONObject(data.getStringExtra(AuthenticationActivity.DATA));
                    JSONObject dataJson = jsonObject.optJSONObject("data");
                    JSONObject profileJson = dataJson.optJSONObject("profile");

                    token_tv.setText(profileJson.optString("displayName"));

                    Glide.with(this)
                            .load(profileJson.optString("avatar"))
                            .centerCrop()
                            .crossFade()
                            .into(iv_avatar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        //starting an activity for authentication
        Intent authenticationActivity = new Intent(this, AuthenticationActivity.class);
        startActivityForResult(authenticationActivity, AuthenticationActivity.AUTHENTICATION_REQUEST_CODE);
    }
}
