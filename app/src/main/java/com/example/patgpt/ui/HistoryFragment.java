package com.example.patgpt.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.patgpt.DatabaseHelper;
import com.example.patgpt.HistoryDB;
import com.example.patgpt.R;
import com.example.patgpt.UserData;

public class HistoryFragment extends Fragment {
    // Declare variables
    private DatabaseHelper databaseHelper;
    private String LoggedInUser;
    private TextView textViewContent;
    private Button clearHistoryButton;

    // Constructor
    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        assignViews(view); // Call assignViews() before accessing textViewContent
        // create a new instance of DatabaseHelper
        databaseHelper = new DatabaseHelper(getActivity());
        // Load the history for the current user
        LoggedInUser = UserData.loadUserEmail(requireContext());
        // Set the text view content
        textViewContent.setText(loadHistoryForUser());
        // Assign the clear history button
        assignButton(view);
        clearHistoryButton.setOnClickListener(v -> clearHistory());
        return view;
    }
    // Restore the view state
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Load the history for the current user
        LoggedInUser = UserData.loadUserEmail(requireContext());
        UserData.setNavHeaders(getActivity());
    }
    // Assign the views
    public void assignViews(View view) {
        textViewContent = view.findViewById(R.id.textView_Content);
    }
    // Assign the clear history button
    public void assignButton(View view) {
        clearHistoryButton = view.findViewById(R.id.button_Clear);
    }
    // Load the history for the current user
    public String loadHistoryForUser() {
        // Load the history for the current user
        Cursor cursor = databaseHelper.getHistoryForUser(LoggedInUser);
        // Create a new StringBuilder
        StringBuilder historyContentBuilder = new StringBuilder();
        if (cursor == null || cursor.getCount() == 0) { // Check if cursor is null or empty
            cursor = databaseHelper.getHistoryForGoogleUser(LoggedInUser); // Fallback to Google user history
        }
        // Check if cursor is not null
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int resultIndex = cursor.getColumnIndex(HistoryDB.COLUMN_RESULT);
                    do {
                        // Get the result from the cursor and append it to the StringBuilder
                        String result = cursor.getString(resultIndex);
                        historyContentBuilder.append(result).append("\n \n \n");
                        // No need to log id and userId for Google user history
                        Log.d("HistoryFragment", "result: " + result);
                    } while (cursor.moveToNext());
                }
            } finally {
                // Remember to close the cursor
                cursor.close();
            }

            String historyContent = historyContentBuilder.toString();
            Log.d("HistoryFragment", "History content:\n" + historyContent);
        } else {
            Log.e("LocalHistoryFragment", "Cursor is null");
        }
        return historyContentBuilder.toString();
    }
    // Clear history for the current user
    public void clearHistory() {
        // Clear history for the current user
        boolean isHistoryCleared = databaseHelper.deleteHistoryForUser(LoggedInUser);
        if (!isHistoryCleared) {
            isHistoryCleared = databaseHelper.deleteHistoryForGoogleUser(LoggedInUser);
        }

        if (isHistoryCleared) {
            textViewContent.setText("");
        }
    }


}
