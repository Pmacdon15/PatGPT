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

    private String currentFirstName;
    private TextView textviewFirstName;
    private EditText editTextFirstName;
    private Button buttonEditFirstName;


    //    public static ProfileFragment newInstance() {
//        ProfileFragment fragment = new ProfileFragment();
//        Bundle args = new Bundle();
//        return fragment;
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("profileUsername", LoginViewModel.profileUsername);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
//        textviewFirstName = root.findViewById(R.id.textView_First_Name);
//        textviewFirstName.setText(LoginViewModel.profileUsername);
        initializeViews(root);
        return root;
    }
    private void initializeViews(View rootView) {
        textviewFirstName = rootView.findViewById(R.id.textView_First_Name);
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        currentFirstName = databaseHelper.getFirstName(LoginViewModel.profileUsername);
        textviewFirstName.setText(currentFirstName);
        editTextFirstName = rootView.findViewById(R.id.editText_First_Name);
        buttonEditFirstName = rootView.findViewById(R.id.button_Edit_First_Name);
        buttonEditFirstName.setOnClickListener(this::editFirstNameProfilePage);
    }
    public void editFirstNameProfilePage(View view) {
        Log.d("Maintenance", " Edit First Name Button Clicked");
        // call  editUserFirstName method from DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String newFirstName = editTextFirstName.getText().toString();
        if (databaseHelper.editUserFirstNameDB(LoginViewModel.profileUsername, newFirstName)) {
            Log.d("Maintenance", "First Name Updated");
            Log.d("newFirstName", newFirstName);
            textviewFirstName.setText(newFirstName);
        } else {
            Log.d("Maintenance", "First Name Not Updated");
        }

    }
}