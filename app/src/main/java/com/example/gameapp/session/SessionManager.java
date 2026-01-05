package com.example.gameapp.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "game_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LOGGED_IN = "logged_in";

    // ✅ SAVE LOGIN
    public static void saveLogin(Context context, String token) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    // ✅ CHECK LOGIN
    public static boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_LOGGED_IN, false);
    }

    // ✅ GET TOKEN (🔥 THIS WAS MISSING)
    public static String getToken(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, "");
    }

    // ✅ LOGOUT
    public static void logout(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply();
    }
}
