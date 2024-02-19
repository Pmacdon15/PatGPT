package com.example.patgpt.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.patgpt.DatabaseHelper;
import com.example.patgpt.R;
import com.example.patgpt.ui.ui.login.LoginViewModel;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }
    private TextView textviewFirstName;
    private EditText editTextFirstName;

    private TextView textviewLastName;
    private EditText editTextLastName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("profileUsername", LoginViewModel.profileUsername);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);//
        initializeViews(root);
        return root;
    }
    private void initializeViews(View rootView) {
        // Set the text views with the current Information
        textviewFirstName = rootView.findViewById(R.id.textView_First_Name);
        textviewLastName = rootView.findViewById(R.id.textView_Last_Name);//
        SetFirstNameTextView();
        SetLastNameTextView();

        // Set the edit texts
        editTextFirstName = rootView.findViewById(R.id.editText_First_Name);
        editTextLastName = rootView.findViewById(R.id.editText_Last_Name);

        // Set on clicks for the buttons
        Button buttonEditFirstName = rootView.findViewById(R.id.button_Edit_First_Name);
        buttonEditFirstName.setOnClickListener(this::editFirstNameProfilePage);
        Button buttonEditLastName = rootView.findViewById(R.id.button_Edit_Last_Name);
        buttonEditLastName.setOnClickListener(this::editLastNameProfilePage);
    }
    // Set The Text views
    private void SetFirstNameTextView() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            String currentFirstName = databaseHelper.getFirstName(LoginViewModel.profileUsername);
            textviewFirstName.setText(currentFirstName);
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
    }
    private void SetLastNameTextView() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            String currentLastName = databaseHelper.getLastName(LoginViewModel.profileUsername);
            textviewLastName.setText(currentLastName);
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
    }
    // Button Clicks
    public void editFirstNameProfilePage(View view) {
        Log.d("Maintenance", " Edit First Name Button Clicked");
        String newFirstName = editTextFirstName.getText().toString();
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            if (databaseHelper.editUserFirstNameDB(LoginViewModel.profileUsername, newFirstName)) {
                Log.d("Maintenance", "First Name Updated");
                Log.d("newFirstName", newFirstName);
                textviewFirstName.setText(newFirstName);
            } else {
                Log.d("Maintenance", "First Name Not Updated");
            }
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
    }
    public void editLastNameProfilePage(View view) {
        Log.d("Maintenance", " Edit Last Name Button Clicked");
        String newLastName = editTextLastName.getText().toString();
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            if (databaseHelper.editUserLastNameDB(LoginViewModel.profileUsername, newLastName)) {
                Log.d("Maintenance", "Last Name Updated");
                Log.d("newLastName", newLastName);
                textviewLastName.setText(newLastName);
            } else {
                Log.d("Maintenance", "Last Name Not Updated");
            }
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
    }
}