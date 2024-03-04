package com.example.patgpt.ui.data;

import android.content.Context;

import com.example.patgpt.DatabaseHelper;
import com.example.patgpt.ui.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final DatabaseHelper databaseHelper;

    public LoginDataSource(Context context) {
        this.databaseHelper = new DatabaseHelper(context); // Initialize DatabaseHelper
    }


    public Result<LoggedInUser> login(String username, String password)    {

        try {
            // Check user credentials using the DatabaseHelper
            boolean isAuthenticated = databaseHelper.checkUser(username, password);

            if (isAuthenticated) {
                // If authentication is successful, create a LoggedInUser instance
                LoggedInUser currentUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                username);
                return new Result.Success<>(currentUser); // Return Result.Success directly
            } else {
                // If authentication fails, return an error result
                return new Result.Error(new IOException("Authentication failed"));
            }
        } catch (Exception e) {
            // If any exception occurs during authentication, return an error result
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
