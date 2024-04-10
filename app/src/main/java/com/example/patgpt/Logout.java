package com.example.patgpt;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Logout extends Fragment {

    public Logout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logout, container, false);


        signOut();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void signOut() {

        UserData.clearSharedPreference(requireActivity());
        googleSignOut();
        UserData.resetNavHeaderImage(requireActivity());
        clearFragmentHistory();


    }

    private void clearFragmentHistory() {
        // Clear the fragment history
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }


    }


    public void googleSignOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(requireContext(), gso);

        gsc.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                navigateToLogin();
            } else {
                // Handle sign-out failure
                Log.d("Logout", "Sign out failed: " + task.getException());

            }
        });
    }

    public void navigateToLogin() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.nav_login);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
