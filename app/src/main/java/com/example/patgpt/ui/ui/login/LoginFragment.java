package com.example.patgpt.ui.ui.login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patgpt.DatabaseHelper;
import com.example.patgpt.R;
import com.example.patgpt.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;


public class LoginFragment extends Fragment {

    private GoogleSignInClient signInClient;
    private FragmentLoginBinding binding;
    private DatabaseHelper databaseHelper;
    public static String newUserName = "";
    public static String LoggedInUser = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(requireContext(), gso);


        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if user is already logged in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account != null) {
            // User is already logged in, navigate to the home activity directly
            Log.d("LoginFragment", "Account: " + account.getEmail());
            // Below doesn't work
            LoginFragment.LoggedInUser = account.getEmail();
            Log.d("LoginFragment", "LoggedInUser: " + LoggedInUser);
            navigateToHome();
            return;
        }
        if (!loadUserEmail().equals("")) {
            LoggedInUser = loadUserEmail();
            Log.d("LoginFragment", "LoggedInUser: " + LoggedInUser);
            navigateToHome();
            return;
        }

        databaseHelper = new DatabaseHelper(getContext());
        EditText usernameEditText = view.findViewById(R.id.username);
        EditText passwordEditText = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.login);

        final Button signInWithGoogle = view.findViewById(R.id.login_with_google);
        signInWithGoogle.setOnClickListener(v -> signInWithGoogle());
        loginButton.setOnClickListener(v -> signIn(usernameEditText.getText().toString(), passwordEditText.getText().toString()));

        // Set login button to be clicked when the user presses the enter key
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.performClick();
                return true;
            }
            return false;
        });

    }

    public void signIn(String username, String password) {
        // check if you can remove nesting with return later
        if (databaseHelper.checkUser(username, password)) {
            LoggedInUser = username;
            saveUserEmail(LoggedInUser);
            setNavHeaderUsername();
            navigateToHome();
        } else {
            showLoginFailed(R.string.login_failed);
            if (!databaseHelper.doesUserExist(username)) {
                newUserName = username;
                navigateToRegistration();
            }

        }
    }


    // For the login button, check the login result and navigate to HomeFragment
    private void navigateToHome() {
        // If login is successful, navigate to HomeFragment
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.nav_home);
    }

    private void navigateToRegistration() {
        // If login is successful, navigate to HomeFragment
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.nav_registration);
    }


    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void signInWithGoogle() {
        Intent signInIntent = signInClient.getSignInIntent();
        signInLauncher.launch(signInIntent); // Launch the sign-in activity
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Toast.makeText(getContext(), "Google sign-in successful", Toast.LENGTH_SHORT).show();
                        LoggedInUser = account.getEmail();
                        saveUserEmail(LoggedInUser);
                        //saveProfileImage(account.getPhotoUrl());
                        setNavHeaderUsername();
                        Navigation.findNavController(requireView()).navigate(R.id.nav_home);
                    } catch (ApiException e) {
                        // Handle sign-in failure (e.g., show error message)
                        Toast.makeText(getContext(), "Google sign-in failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Save user's email to SharedPreferences
    private void saveUserEmail(String email) {
        if (getContext() != null) {
            getContext().getSharedPreferences("LoggedInUser", 0).edit().putString("email", email).apply();
        }
    }
    // Save uri for profile image to SharedPreferences
    private void saveProfileImage(Uri url) {
        if (getContext() != null) {
            getContext().getSharedPreferences("LoggedInUserProfileImage", 0).edit().putString("profileImage", url.toString()).apply();
        }
    }
    // Load user's email from SharedPreferences
    private String loadUserEmail() {
        if (getContext() != null) {
            return getContext().getSharedPreferences("LoggedInUser", 0).getString("email", "");
        }
        return "";
    }
    // Load uri for profile image from SharedPreferences
    private String loadProfileImage() {
        if (getContext() != null) {
            return getContext().getSharedPreferences("LoggedInUserProfileImage", 0).getString("profileImage", "");
        }
        return "";
    }
    // Set navHeader to user's profile image
//    private void setNavHeaderProfileImage() {
//        String url = loadProfileImage();
//        if (getContext() != null && binding.navView != null) {
//            // Set profile image in nav header
//            ImageView profileImageView = binding.navView.getHeaderView(0).findViewById(R.id.imageView);
//            Picasso.get().load(url).into(profileImageView);
//        }
//    }

    public void setNavHeaderUsername() {
        Activity activity = getActivity();
        if (activity != null) {
            NavigationView navigationView = activity.findViewById(R.id.nav_view);
            if (navigationView != null) {
                View headerView = navigationView.getHeaderView(0);
                TextView textViewNavHeader = headerView.findViewById(R.id.textView);
                if (textViewNavHeader != null) {
                    textViewNavHeader.setText(loadUserEmail());
                }
            }else {
                Log.d("LoginFragment", "navigationView is null");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}