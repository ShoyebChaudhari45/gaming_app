package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.gameapp.R;
import com.example.gameapp.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force light mode BEFORE super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (SessionManager.isLoggedIn(this)) {
                // ⭐ ROLE-BASED NAVIGATION
                navigateBasedOnUserType();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }

    // ⭐ Navigate to appropriate home screen based on user type
    private void navigateBasedOnUserType() {
        if (SessionManager.isEmployee(this)) {
            startActivity(new Intent(this, EmployeeHomeActivity.class));
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
}