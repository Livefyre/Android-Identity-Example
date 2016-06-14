package com.livefyre.comments.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.livefyre.comments.R;

public class LoginActivity extends AppCompatActivity {

    private View lfLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lfLogin = findViewById(R.id.LFLogin_view);
         lfLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent(LoginActivity.this, CommentsActivity.class);
                 startActivity(i);
             }
         });
    }
}
