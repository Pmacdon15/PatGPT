package com.example.patgpt.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.patgpt.R;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

        imageViewContent.setOnClickListener(view -> shareImageFromImageView());
        // If screen rotates, restore the content of the ImageViewContent
       // if (savedInstanceState != null) onViewStateRestored(savedInstanceState);
        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the url of the imageViewContent
        outState.putString("url", url[0]);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // Restore the url of the imageViewContent
        if (savedInstanceState != null) {
            url[0] = savedInstanceState.getString("url");
            if (url[0] != null) {
                Picasso.get().load(url[0]).into(imageViewContent);
            }
        }
    }

    private String getAPIKey(Context context) {
        //Log.d("API_KEY", context.getString(R.string.API_KEY));
        return context.getString(R.string.API_KEY);
    }

    private void makeApiRequest() {
        // Set loading image to imageview
        imageViewContent.setImageResource(R.drawable.loading);
        // Get the API key from the resources
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

                requireActivity().runOnUiThread(() -> {
                    // Make imageViewContent display the image from the URL
                    Picasso.get().load(url[0]).into(imageViewContent);
                    // Close the keyboard and clear the EditText
                    closeKeyboard(requireContext(), editTextPrompt);

                });
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

    private Bitmap getBitmapFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    public void shareImageFromImageView() {
        try {
            Activity activity = getActivity();
            if (activity != null) {
                saveImageLocally();
                File savedImageFile = new File(activity.getFilesDir(), "sharedImage.jpg");

                // Create an Intent to share the image
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".fileprovider", savedImageFile);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Add this line
                startActivity(Intent.createChooser(shareIntent, "Share Image"));
            } else {
                Toast.makeText(requireContext(), "No image to share", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveImageLocally()  {
        Activity activity = getActivity();
        if (activity != null) {
            try {
                // Get uri from imageViewContent
                Bitmap bitmap = getBitmapFromImageView(imageViewContent);
                if (bitmap != null) {
                    File file = new File(activity.getFilesDir(), "sharedImage.jpg");
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                    Log.d("Image", "Image Saved Locally");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}