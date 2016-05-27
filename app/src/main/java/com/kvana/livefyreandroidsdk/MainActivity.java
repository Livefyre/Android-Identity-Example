package com.kvana.livefyreandroidsdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kvana.streamhub_android_sdk.AuthenticationActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView token_tv;
    private Button sign_in_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        token_tv = (TextView) findViewById(R.id.token_tv);
        sign_in_btn = (Button) findViewById(R.id.sign_in_btn);
        sign_in_btn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AuthenticationActivity.AUTHENTICATION_REQUEST_CODE) {
                //you can receive token here with key - TOKEN
                token_tv.setText(data.getStringExtra(AuthenticationActivity.TOKEN));
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
