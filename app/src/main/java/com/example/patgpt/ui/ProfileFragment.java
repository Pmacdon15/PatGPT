package com.example.patgpt.ui;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.patgpt.UserData;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;



public class ProfileFragment extends Fragment {
    // Declare variables
    private ImageView imageViewProfile;
    private TextView textviewFirstName;
    private EditText editTextFirstName;
    private TextView textviewLastName;
    private EditText editTextLastName, editTextPassword_Current, editTextPassword_New, editTextPassword_Confirm;
    private String LoggedInUser;
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        // Load the user email from the shared preferences
        LoggedInUser = UserData.loadUserEmail(requireContext());
        // Initialize the views
        initializeViews(root);
        // Set the profile image if it exists
        setProfileImage();
        Log.d("ProfileFragment","LoggedInUser "+ LoggedInUser);

        return root;
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Load the user email from the shared preferences
        LoggedInUser = UserData.loadUserEmail(requireContext());
        // Set the username in the navigation header and set the image if it exists locally
        UserData.setNavHeaders(getActivity());
    }

    private void initializeViews(View rootView) {
        // Set the profile image
        imageViewProfile = rootView.findViewById(R.id.imageView_Profile);
        imageViewProfile.setOnClickListener(this::loadImageToProfile);

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

    // Set Profile page Image
    public void loadImageToProfile(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageSelector.launch(intent);
    }
    // Register for Activity Result
    ActivityResultLauncher<Intent> imageSelector = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    Activity activity = getActivity();
                    if (activity != null) {
                        NavigationView navigationView = activity.findViewById(R.id.nav_view);
                        if (navigationView != null) {
                            View headerView = navigationView.getHeaderView(0);
                            ImageView imageViewNavHeader = headerView.findViewById(R.id.imageView);
                            if (imageViewNavHeader != null) {
                                imageViewNavHeader.setImageURI(imageUri);
                            }
                        }
                    }
                    // Save the image locally
                    saveImageLocally(imageUri);
                    // Set the image to the imageView
                    imageViewProfile.setImageURI(imageUri);
                }
            }
    );
    // Save Image Locally
    public void saveImageLocally(Uri imageUri) {
        Activity activity = getActivity();
        if (activity != null) {
            try {
                InputStream imageStream = activity.getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                FileOutputStream out = activity.openFileOutput(LoggedInUser + "profileImage.jpg", Context.MODE_PRIVATE);
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Log.d("Image", "Image Saved Locally");
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Set First Name Text views
    private void SetFirstNameTextView() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            String currentFirstName = databaseHelper.getFirstName(LoggedInUser);
            Log.d("currentFirstName", currentFirstName);
            textviewFirstName.setText(currentFirstName);
            
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }

    }
    // Set Last Name Text views
    private void SetLastNameTextView() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            Log.d("LoggedInUser inside of SetLastNameTextView()", LoggedInUser);
            String currentLastName = databaseHelper.getLastName(LoggedInUser);
            Log.d("currentLastName", currentLastName);
            textviewLastName.setText(currentLastName);
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
    }

    // Button Clicks
    // Edit First Name
    public void editFirstNameProfilePage(View view) {
        Log.d("Maintenance", " Edit First Name Button Clicked");
        String newFirstName = editTextFirstName.getText().toString();
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            if (databaseHelper.editUserFirstNameDB(LoggedInUser, newFirstName)) {
                Log.d("Maintenance", "First Name Updated");
                Log.d("newFirstName", newFirstName);
                textviewFirstName.setText(newFirstName);
                editTextFirstName.setText("");
                Toast.makeText(getContext(), "First Name Updated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Maintenance", "First Name Not Updated");
            }
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
    }
    // Edit Last Name
    public void editLastNameProfilePage(View view) {
        Log.d("Maintenance", " Edit Last Name Button Clicked");
        String newLastName = editTextLastName.getText().toString();
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            if (databaseHelper.editUserLastNameDB(LoggedInUser, newLastName)) {
                Log.d("Maintenance", "Last Name Updated");
                Log.d("newLastName", newLastName);
                textviewLastName.setText(newLastName);
                editTextLastName.setText("");
                Toast.makeText(getContext(), "Last Name Updated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Maintenance", "Last Name Not Updated");
            }
        } catch (Exception e) {
            Log.e("Error", "An error occurred while accessing the database", e);
        }
    }
    // Edit Password
    public void editPasswordProfilePage(View view) {
        Log.d("Maintenance", " Edit Password Button Clicked");
        String currentPassword = editTextPassword_Current.getText().toString();
        String newPassword = editTextPassword_New.getText().toString();
        String confirmPassword = editTextPassword_Confirm.getText().toString();
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getContext())) {
            if (databaseHelper.editUserPasswordDB(LoggedInUser, currentPassword, newPassword, confirmPassword)) {
                Log.d("Maintenance", "Password Updated");
                editTextPassword_Current.setText("");
                editTextPassword_New.setText("");
                editTextPassword_Confirm.setText("");
                Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Maintenance", "Password Not Updated");
                Toast.makeText(getContext(), "Password Not Updated, Check Input is Correct!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Set Profile Image
    public void setProfileImage() {
        Activity activity = getActivity();
        if (activity != null) {
            File file = activity.getFileStreamPath(LoggedInUser + "profileImage.jpg");
            if (file.exists()) {
                Uri imageUri = Uri.fromFile(file);
                imageViewProfile.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ProfileFragment", "onDestroy");
    }
}