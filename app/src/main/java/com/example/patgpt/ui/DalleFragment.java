package com.example.patgpt.ui;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.example.patgpt.R;

import com.squareup.picasso.Picasso;

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

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link DalleFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class DalleFragment extends Fragment {
    private static final String URL = "https://api.openai.com/v1/images/generations";
    private EditText editTextPrompt;
    private ImageView imageViewContent;
    private final String[] url = new String[1];

    public DalleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dalle, container, false);
        Button buttonSend = root.findViewById(R.id.button_Send);
        imageViewContent = root.findViewById(R.id.ImageView_Content);
        editTextPrompt = root.findViewById(R.id.editText_Prompt);

        buttonSend.setOnClickListener(view -> makeApiRequest());
        imageViewContent.setOnClickListener(view -> shareImage(url[0]));

        // Inflate the layout for this fragment
        return root;
    }


    private String getAPIKey(Context context) {
        return context.getString(R.string.API_KEY);
    }

    private void makeApiRequest() {
        String API_KEY = getAPIKey(requireContext());
        // Customize the OkHttpClient with increased timeouts
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Increase connect timeout
                .writeTimeout(30, TimeUnit.SECONDS)   // Increase write timeout
                .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout, especially important for your use case
                .build();


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "dall-e-3");
            jsonBody.put("prompt", editTextPrompt.getText().toString());
            jsonBody.put("size", "1024x1024");
            jsonBody.put("quality", "standard");
            jsonBody.put("n", 1);
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

                Log.d("OkHttp", "Response: " + responseData);

                try {
                    // Convert the response to a JSONObject
                    JSONObject jsonObject = new JSONObject(responseData);

                    // Get the 'data' JSONArray
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    // Get the first object of 'data' array
                    JSONObject dataObject = dataArray.getJSONObject(0);

                    // Get the 'url' string
                    url[0] = dataObject.getString("url");
                    Log.d("url", url[0]);

                    // Now 'url' contains the URL string you need
                } catch (Exception e) {
                    e.printStackTrace();
                }
//
                requireActivity().runOnUiThread(() -> {
                    // Set the TextView to display the content
                    //Log.d("responseDate",responseData);
                    //textViewContent.setText(url[0]);
                    // Make imageViewContent display the image from the URL

                    Picasso.get().load(url[0]).into(imageViewContent);

                });

            }


            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OkHttpError", "Error: " + e);
                // Handle error here
            }
        });

    }
    public void shareImage(String imageUrl) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, imageUrl);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

}