package com.example.gameapp;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class GameApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Force Light Mode globally
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
