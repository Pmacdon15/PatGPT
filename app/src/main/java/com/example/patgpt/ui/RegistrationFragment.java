package com.example.patgpt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.patgpt.R;

public class RegistrationFragment extends Fragment {

    private Button registerButton;
    private EditText firstname, lastname, password, confirm_password;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        assignViews(view);
        return view;
    }

    private void assignViews(View view) {
        registerButton = view.findViewById(R.id.registration_button);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        password = view.findViewById(R.id.password);
        confirm_password = view.findViewById(R.id.confirm_password);
    }
}
