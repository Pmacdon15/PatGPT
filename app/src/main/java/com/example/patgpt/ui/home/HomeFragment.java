package com.example.patgpt.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;

import com.example.patgpt.R;
import com.example.patgpt.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button buttonSend = root.findViewById(R.id.button_Send);
        textViewContent = root.findViewById(R.id.textView_Content);
        editTextPrompt = root.findViewById(R.id.editText_Prompt);


        buttonSend.setOnClickListener(view -> makeApiRequest());

        return root;
    }

    private String getAPIKey(Context context) {
        Log.d("API_KEY", context.getString(R.string.API_KEY));
        return context.getString(R.string.API_KEY);
    }

    private void makeApiRequest() {
        String API_KEY = getAPIKey(requireContext());
        OkHttpClient client = new OkHttpClient();

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

                    requireActivity().runOnUiThread(() -> {
                        // Set the TextView to display the content
                        textViewContent.setText(content);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}