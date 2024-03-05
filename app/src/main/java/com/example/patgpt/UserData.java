package com.example.patgpt;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class UserData {
    private static final String PREF_NAME = "LoggedInUser";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PROFILEIMAGE = "profileImage";

    // Save user's email to SharedPreferences
    public static void saveUserEmail(Context context, String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    /// Save uri for profile image to SharedPreferences
    public static void saveProfileImage(Context context, Uri url) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROFILEIMAGE, url.toString());
        editor.apply();
    }

    // Load user's email from SharedPreferences
    public static String loadUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    // Clear user's email from SharedPreferences
    public static void clearUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_EMAIL);
        editor.apply();
    }


}
