package com.example.patgpt.ui.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;

import com.example.patgpt.R;
import com.example.patgpt.ui.data.LoginRepository;

import com.example.patgpt.ui.data.Result;
import com.example.patgpt.ui.data.model.LoggedInUser;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    public static String profileUsername;

//    public LoginViewModel(LoginRepository loginRepository, Context context) {
//        this.loginRepository = loginRepository;
//        this.databaseHelper = new DatabaseHelper(context); // Initialize DatabaseHelper
//    }


    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

        public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            profileUsername = username;
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }
//    public void login(String username, String password) {
//        boolean isLoggedIn = databaseHelper.checkUser(username, password);
//
//        if (isLoggedIn) {
//            // Login successful
//            LoggedInUser data = new LoggedInUser(java.util.UUID.randomUUID().toString(), username);
//            profileUsername = username;
//            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
//
//        } else {
//            // Login failed
//            loginResult.setValue(new LoginResult(R.string.login_failed));
//        }
//    }
    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 4;
    }
}