package com.example.patgpt.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.patgpt.DatabaseHelper;
import com.example.patgpt.R;
import com.example.patgpt.UserData;
import com.example.patgpt.databinding.FragmentHomeBinding;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    // API URL
    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private TextView textViewContent;
    private EditText editTextPrompt;
    private FragmentHomeBinding binding;
    private String LoggedInUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Set Views and Buttons
        Button buttonSend = root.findViewById(R.id.button_Send);
        textViewContent = root.findViewById(R.id.textView_Content);
        editTextPrompt = root.findViewById(R.id.editText_Prompt);

        // Set the onClickListeners
        buttonSend.setOnClickListener(view -> makeApiRequest());
        textViewContent.setOnClickListener(this::shareContent);

        // Set the User
        LoggedInUser = UserData.loadUserEmail(requireContext());

        Log.d("HomeFragment", "onCreateView: " + LoggedInUser);

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the content of the textViewContent
        outState.putString("savedContent", textViewContent.getText().toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            String savedContent = savedInstanceState.getString("savedContent");
            textViewContent.setText(savedContent);
        }
        // The above if seems redundant but it removes a warning in the IDE.
    }

    @Override
    public void onResume() {
        super.onResume();
        UserData.setNavHeaders(getActivity());
    }


    private String getAPIKey(Context context) {
        // Get the API Key from the environment.xml file
        //Log.d("API_KEY", context.getString(R.string.API_KEY));
        return context.getString(R.string.API_KEY);
    }

    private void makeApiRequest() {
        String API_KEY = getAPIKey(requireContext());
        String prompt = editTextPrompt.getText().toString();
        // set textViewContent to loading string
        textViewContent.setText(R.string.loading);

        // Create an OkHttpClient object with increased timeouts for better performance
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Increase connect timeout
                .writeTimeout(30, TimeUnit.SECONDS)   // Increase write timeout
                .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout, especially important for your use case
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonBody = new JSONObject();
        try {
            // Create the request body
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", editTextPrompt.getText().toString());
            messages.put(message);
            jsonBody.put("messages", messages);
            jsonBody.put("temperature", 0.7);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.Companion.create(jsonBody.toString(), JSON);
        // Create the request
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
                    String output = prompt + ":\n \n \n" + content;
                    // Save the content to the database
                    saveHistory(output);

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

    public void saveHistory(String content) {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(getActivity())) {
            if (!databaseHelper.addHistoryForUser(LoggedInUser, content)) {

                if (!databaseHelper.addHistoryGoogleUser(LoggedInUser, content)) {
                    Log.e("HistoryFragment", "Failed to add history");
                }
            }
            // Else successfully added history
            Log.d("HistoryFragment", "History added successfully");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}