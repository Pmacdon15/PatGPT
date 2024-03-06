package com.example.patgpt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UserData {
    private static final String PREF_NAME = "LoggedInUser";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PROFILEIMAGE_GOOGLE = "profileImage";

    public static void setNavHeaders(Activity activity) {
       String loggedInUser = UserData.loadUserEmail(activity);
        UserData.setNavHeaderUsername(activity, loggedInUser);
        if (!UserData.checkForImageFileAndSetNavHeaderImage(activity)) {
            UserData.setNavHeaderGoogleImage(activity);
        }
    }
    // Save user's email to SharedPreferences
    public static void saveUserEmail(Context context, String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    // Save uri for profile image to SharedPreferences
    public static void saveGoogleProfileImage(Context context, Uri url) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROFILEIMAGE_GOOGLE, url.toString());
        editor.apply();
    }

    // Load Google profile image
    public static String loadGoogleProfileImage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PROFILEIMAGE_GOOGLE, "");
    }

    // Set Google profile image in the navigation header
    public static void setNavHeaderGoogleImage(Activity activity) {
        if (activity != null) {
            String profileImageUrl = UserData.loadGoogleProfileImage(activity);
            if (!profileImageUrl.isEmpty()) {
                NavigationView navigationView = activity.findViewById(R.id.nav_view);
                if (navigationView != null) {
                    View headerView = navigationView.getHeaderView(0);
                    ImageView imageViewNavHeader = headerView.findViewById(R.id.imageView);
                    if (imageViewNavHeader != null) {
                        Picasso.get().load(profileImageUrl).into(imageViewNavHeader);
                    }
                }
            }
        }
    }

    // Check for regular user profile image and set it in the navigation header
    public static boolean checkForImageFileAndSetNavHeaderImage(Activity activity) {
        if (activity != null) {
            String userName = loadUserEmail(activity);
            String fileName = userName + "profileImage.jpg";
            Log.d("File Check", "Checking for " + fileName);

            File file = activity.getFileStreamPath(fileName);
            if (file != null && file.exists()) {
                Uri imageUri = Uri.fromFile(file);
                NavigationView navigationView = activity.findViewById(R.id.nav_view);
                if (navigationView != null) {
                    View headerView = navigationView.getHeaderView(0);
                    ImageView imageViewNavHeader = headerView.findViewById(R.id.imageView);
                    if (imageViewNavHeader != null) {
                        Log.d("HomeFragment", "Setting nav header image" + imageUri);
                        imageViewNavHeader.setImageURI(imageUri);
                        return true;
                    }
                }
            } else {
                Log.d("File Check", fileName + " does not exist.");
            }
        }
        Log.d("File Check", "No profile image to load");
        return false;
    }

    // Load user's email from SharedPreferences
    public static String loadUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    // Set username in the navigation header
    public static void setNavHeaderUsername (Activity activity, String username){
        if (activity != null) {
            NavigationView navigationView = activity.findViewById(R.id.nav_view);
            if (navigationView != null) {
                View headerView = navigationView.getHeaderView(0);
                TextView textViewNavHeader = headerView.findViewById(R.id.textView);
                if (textViewNavHeader != null) {
                    textViewNavHeader.setText(username);
                }
            } else {
                Log.d("HomeFragment", "navigationView is null");
            }
        }
    }

    // Clear user's email from SharedPreferences
    public static void clearUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_EMAIL);
        editor.apply();
    }
}
