package com.example.gameapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (SessionManager.isLoggedIn(this)) {
                startActivity(new Intent(this, HomeActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();

        }, 2000);
    }
}
