package com.example.gameapp.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "game_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_EMAIL = "user_email";
    // save QR Code
    private static final String KEY_QR_CODE = "qr_code";

    // NEW KEY for storing Support WhatsApp Number
    private static final String KEY_SUPPORT_WHATSAPP = "support_whatsapp";

    // ========================= EMAIL =========================

    public static void saveEmail(Context context, String email) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, null);
    }

    // ========================= LOGIN =========================

    public static void saveLogin(Context context, String token) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_LOGGED_IN, false);
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, "");
    }

    // ========================= BALANCE =========================

    public static void saveBalance(Context context, int balance) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_BALANCE, balance)
                .apply();
    }

    public static int getBalance(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_BALANCE, 0);
    }

    // ========================= SUPPORT WHATSAPP =========================
    // 🔥 Used for Home Screen WhatsApp Button

    public static void saveSupportWhatsapp(Context context, String number) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_SUPPORT_WHATSAPP, number).apply();
    }

    public static String getSupportWhatsapp(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SUPPORT_WHATSAPP, "");
    }

    ///  save Qr and get Qr
    public static void saveQrCode(Context context, String qrUrl) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_QR_CODE, qrUrl).apply();
    }

    public static String getQrCode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_QR_CODE, "");
    }

    // ========================= LOGOUT =========================

    public static void logout(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}

// all sessions will be store here like login , logout , balance and all other things which will be resued later
