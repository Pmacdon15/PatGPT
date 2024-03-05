package com.example.patgpt.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.patgpt.R;
import com.example.patgpt.UserData;


public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Set the username in the navigation header and set the image if it exists or set the google image
        UserData.setNavHeaderUsername(getActivity(), UserData.loadUserEmail(requireContext()));
        if (!UserData.checkForImageFileAndSetNavHeaderImage(getActivity())) {
            UserData.setNavHeaderGoogleImage(getActivity());
        }
    }
}