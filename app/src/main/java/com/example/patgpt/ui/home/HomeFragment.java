package com.example.patgpt.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;

import com.example.patgpt.R;
import com.example.patgpt.databinding.FragmentHomeBinding;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private TextView textViewContent;
    private EditText editTextPrompt;
    private FragmentHomeBinding binding;
   // private String prompt;

    public void checkForImageFile() {
        Activity activity = getActivity();
        if (activity != null) {
            String fileName = "profileImage.jpg";
            File file = activity.getFileStreamPath(fileName);
            if (file == null || !file.exists()) {
                Log.d("File Check", fileName + " does not exist.");
            } else {
                Log.d("File Check", fileName + " exists.");
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button buttonSend = root.findViewById(R.id.button_Send);
        textViewContent = root.findViewById(R.id.textView_Content);
        editTextPrompt = root.findViewById(R.id.editText_Prompt);


        buttonSend.setOnClickListener(view -> makeApiRequest());
        textViewContent.setOnClickListener(this::shareContent);
        // If screen rotates, restore the content of the TextViewContent
        if (savedInstanceState != null) onViewStateRestored(savedInstanceState);
        checkForImageFile();
        Log.d("LoginFragment", "onViewCreated");
        setNavHeaderImage();
        return root;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("savedContent", textViewContent.getText().toString());
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            String savedContent = savedInstanceState.getString("savedContent");
            textViewContent.setText(savedContent);
        }
    }

    private String getAPIKey(Context context) {
        //Log.d("API_KEY", context.getString(R.string.API_KEY));
        return context.getString(R.string.API_KEY);
    }

    private void makeApiRequest() {
        String API_KEY = getAPIKey(requireContext());
        String prompt = editTextPrompt.getText().toString();
        // set textViewContent to loading string
        textViewContent.setText(R.string.loading);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Increase connect timeout
                .writeTimeout(30, TimeUnit.SECONDS)   // Increase write timeout
                .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout, especially important for your use case
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
//            HomeViewModel inputText;
            message.put("content", editTextPrompt.getText().toString());
            messages.put(message);
            jsonBody.put("messages", messages);
            jsonBody.put("temperature", 0.7);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.Companion.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String responseData = response.body().string();

                try {
                    // Create a JSONObject from the response data
                    JSONObject jsonObject = new JSONObject(responseData);

                    // Navigate to the 'content' field in the JSON structure
                    String content = jsonObject.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                    // Trailing line breaks for better functionality when scrolling the screen
                    String output = prompt+ ":\n \n \n" + content + "\n \n \n \n \n";

                    requireActivity().runOnUiThread(() -> {
                        closeKeyboard(requireContext(), editTextPrompt);
                        // Set the TextView to display the content
                        Log.d("Content", content);
                        Log.d("Output", output);
                        textViewContent.setText(output);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OkHttpError", "Error: " + e);
                // Handle error here
            }
        });
    }

    public void closeKeyboard(Context context, EditText editText) {
        // Clear the EditText
        editText.setText("");
        // Close the keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void shareContent(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textViewContent.getText().toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void setNavHeaderImage() {
        Activity activity = getActivity();
        if (activity != null) {
            File file = activity.getFileStreamPath("profileImage.jpg");
            if (file.exists()) {
                Uri imageUri = Uri.fromFile(file);
                NavigationView navigationView = activity.findViewById(R.id.nav_view);
                if (navigationView != null) {
                    View headerView = navigationView.getHeaderView(0);
                    ImageView imageViewNavHeader = headerView.findViewById(R.id.imageView);
                    if (imageViewNavHeader != null) {
                        imageViewNavHeader.setImageURI(imageUri);
                    }
                }
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}