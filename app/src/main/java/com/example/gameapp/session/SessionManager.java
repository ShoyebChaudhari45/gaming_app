package com.example.gameapp.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "game_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_QR_CODE = "qr_code";
    private static final String KEY_SUPPORT_WHATSAPP = "support_whatsapp";

    // ⭐ NEW: User Type Management
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_MOBILE = "user_mobile";

    // User Type Constants
    public static final String USER_TYPE_CUSTOMER = "customer";
    public static final String USER_TYPE_EMPLOYEE = "employee";

    // ========================= USER TYPE =========================

    public static void saveUserType(Context context, String userType) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_TYPE, userType).apply();
    }

    public static String getUserType(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_TYPE, USER_TYPE_CUSTOMER); // Default to customer
    }

    public static boolean isEmployee(Context context) {
        return USER_TYPE_EMPLOYEE.equalsIgnoreCase(getUserType(context));
    }

    public static boolean isCustomer(Context context) {
        return USER_TYPE_CUSTOMER.equalsIgnoreCase(getUserType(context));
    }

    // ========================= USER INFO =========================

    public static void saveUserName(Context context, String name) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_NAME, "");
    }

    public static void saveUserMobile(Context context, String mobile) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_MOBILE, mobile).apply();
    }

    public static String getUserMobile(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_MOBILE, "");
    }

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

    // ========================= QR CODE =========================

    public static void saveQrCode(Context context, String qrUrl) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_QR_CODE, qrUrl).apply();
    }

    public static String getQrCode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_QR_CODE, "");
    }
    // Add these constants at the top with other KEY_ constants
    private static final String KEY_GAME_TYPES_SCROLL = "game_types_scroll";
    private static final String KEY_SELECTED_GAME_POS = "selected_game_pos";
    private static final String KEY_SAVED_DIGIT = "saved_digit";
    private static final String KEY_SAVED_POINT = "saved_point";

// Add these methods at the end of the class before the last }
// Add these constants with other KEY_ constants
private static final String KEY_SCROLL_POSITION = "scroll_position";

    // Add these methods at the end of the class
    public static void saveScrollPosition(Context context, int scrollY) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_SCROLL_POSITION, scrollY).apply();
    }

    public static int getScrollPosition(Context context, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SCROLL_POSITION, defaultValue);
    }

    public static void saveGameTypesScrollPosition(Context context, int position) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_GAME_TYPES_SCROLL, position).apply();
    }

    public static int getGameTypesScrollPosition(Context context, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_GAME_TYPES_SCROLL, defaultValue);
    }

    public static void saveSelectedGamePosition(Context context, int position) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_SELECTED_GAME_POS, position).apply();
    }

    public static int getSelectedGamePosition(Context context, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SELECTED_GAME_POS, defaultValue);
    }

    public static void saveBidInputs(Context context, String digit, String point) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_SAVED_DIGIT, digit)
                .putString(KEY_SAVED_POINT, point)
                .apply();
    }

    public static String getSavedDigit(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SAVED_DIGIT, "");
    }

    public static String getSavedPoint(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SAVED_POINT, "");
    }
    // ========================= LOGOUT =========================

    public static void logout(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}