package com.example.patgpt;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


public class Logout extends Fragment {
    private GoogleSignInClient gsc;

    public Logout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logout, container, false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(requireContext(), gso);


        signOut();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void signOut() {
        deleteSharedPreferencesLoggedInUser();
        //deleteSharedPreferencesProfileImage();

        gsc.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                navigateToLogin();
            } else {
                // Handle sign-out failure
                Log.d("Logout", "Sign out failed: " + task.getException());
                // Optionally display a toast or some UI feedback to the user
            }
        });
    }

    public void navigateToLogin() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.nav_login);
    }

    // Delete shared preferences loggedInUser
    public void deleteSharedPreferencesLoggedInUser() {
//        Context context = requireContext();
//        // Clear the user email
//        SharedPreferences userPrefs = context.getSharedPreferences("LoggedInUser", 0);
//        userPrefs.edit().remove("email").apply();
        requireActivity().getSharedPreferences("LoggedInUser", 0).edit().clear().apply();

    }

//
//        requireActivity().getSharedPreferences("LoggedInUser", 0).edit().clear().apply();
//        requireActivity().getSharedPreferences("LoggedInUserProfileImage", 0).edit().clear().apply();

//    public void deleteSharedPreferences() {
//        Context context = requireContext();
//
//        // Clear the user email
//        SharedPreferences userPrefs = context.getSharedPreferences("LoggedInUser", 0);
//        userPrefs.edit().remove("email").apply();
//
//
//        // Clear the profile image URL
//        SharedPreferences imagePrefs = context.getSharedPreferences("LoggedInUserProfileImage", 0);
//        imagePrefs.edit().remove("profileImage").apply();
//    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
