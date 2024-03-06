package com.example.patgpt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.patgpt.DatabaseHelper;
import com.example.patgpt.R;
import com.example.patgpt.UserData;
import com.example.patgpt.ui.ui.login.LoginFragment;

public class RegistrationFragment extends Fragment {

    private Button registerButton;
    private EditText firstname, lastname, password, confirm_password;
    DatabaseHelper databaseHelper;

    private String first_name;
    private String last_name;
    private String pass;
    private String confirm_pass;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        assignViewsAndButton(view);
        createOnClickListeners();
        return view;
    }

    private void assignViewsAndButton(View view) {
        registerButton = view.findViewById(R.id.registration_button);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        password = view.findViewById(R.id.password);
        confirm_password = view.findViewById(R.id.confirm_password);
    }

    public void createOnClickListeners() {
        registerButton.setOnClickListener(v -> registerUser());
    }

    public void getTextValues() {
        first_name = firstname.getText().toString();
        last_name = lastname.getText().toString();
        pass = password.getText().toString();
        confirm_pass = confirm_password.getText().toString();
    }

    public void registerUser() {
        getTextValues();
        if (!confirmPasswordsMatch()) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if(first_name.isEmpty() || last_name.isEmpty() || pass.isEmpty() || confirm_pass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseHelper = new DatabaseHelper(getActivity());
        if (databaseHelper.addUser(LoginFragment.newUserName, first_name, last_name, pass)) {
            UserData.saveUserEmail(requireContext(),LoginFragment.newUserName);
            Toast.makeText(getActivity(), "User Registered", Toast.LENGTH_SHORT).show();
            navigateToHome();
        } else {
            Toast.makeText(getActivity(), "User not Registered", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean confirmPasswordsMatch() {
        if (pass.equals(confirm_pass)) {
            return true;
        }
        // Else show a toast message
        Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void navigateToHome() {
        // If login is successful, navigate to HomeFragment
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.nav_home);
    }

}
