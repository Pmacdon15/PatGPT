package com.example.patgpt.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patgpt.DatabaseHelper;
import com.example.patgpt.R;
import com.example.patgpt.ui.ui.login.LoginViewModel;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }
    private ImageView imageViewProfile;
    private TextView textviewFirstName;
    private EditText editTextFirstName;
    private TextView textviewLastName;
    private EditText editTextLastName, editTextPassword_Current, editTextPassword_New, editTextPassword_Confirm;

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
        // Set the profile image
        imageViewProfile = rootView.findViewById(R.id.imageView_Profile);
        imageViewProfile.setOnClickListener(this::loadImage);

        // Set the text views with the current Information
        textviewFirstName = rootView.findViewById(R.id.textView_First_Name);
        textviewLastName = rootView.findViewById(R.id.textView_Last_Name);//
        SetFirstNameTextView();
        SetLastNameTextView();

        // Set the edit texts
        editTextFirstName = rootView.findViewById(R.id.editText_First_Name);
        editTextLastName = rootView.findViewById(R.id.editText_Last_Name);
        editTextPassword_Current = rootView.findViewById(R.id.editTextPassword_Current);
        editTextPassword_New = rootView.findViewById(R.id.editTextPassword_New);
        editTextPassword_Confirm = rootView.findViewById(R.id.editTextPassword_Confirm);

        // Set on clicks for the buttons
        Button buttonEditFirstName = rootView.findViewById(R.id.button_Edit_First_Name);
        buttonEditFirstName.setOnClickListener(this::editFirstNameProfilePage);
        Button buttonEditLastName = rootView.findViewById(R.id.button_Edit_Last_Name);
        buttonEditLastName.setOnClickListener(this::editLastNameProfilePage);
        Button buttonEditPassword = rootView.findViewById(R.id.button_Edit_Password);
        buttonEditPassword.setOnClickListener(this::editPasswordProfilePage);

    }
    // Set Profile Image
    public void loadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageSelector.launch(intent);
    }

    private ActivityResultLauncher<Intent> imageSelector = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    imageViewProfile.setImageURI(imageUri);


                }
            }
    );
    // Set The Text views
    private void SetFirstNameTextView() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            String currentFirstName = databaseHelper.getFirstName(LoginViewModel.profileUsername);
            textviewFirstName.setText(currentFirstName);
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
        Toast.makeText(getContext(), "First Name Updated", Toast.LENGTH_SHORT).show();
    }
    private void SetLastNameTextView() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            String currentLastName = databaseHelper.getLastName(LoginViewModel.profileUsername);
            textviewLastName.setText(currentLastName);
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
        Toast.makeText(getContext(), "Last Name Updated", Toast.LENGTH_SHORT).show();
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

    public void editPasswordProfilePage(View view) {
        Log.d("Maintenance", " Edit Password Button Clicked");
        String currentPassword = editTextPassword_Current.getText().toString();
        String newPassword = editTextPassword_New.getText().toString();
        String confirmPassword = editTextPassword_Confirm.getText().toString();
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            if (databaseHelper.editUserPasswordDB(LoginViewModel.profileUsername, currentPassword, newPassword, confirmPassword)) {
                Log.d("Maintenance", "Password Updated");
                Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Maintenance", "Password Not Updated");
                Toast.makeText(getContext(), "Password Not Updated, Check Input is Correct!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}