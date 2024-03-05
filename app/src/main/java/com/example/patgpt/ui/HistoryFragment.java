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
import com.example.patgpt.ui.ui.login.LoginFragment;

public class HistoryFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private String UserEmail;
    private TextView textViewContent;
    private Button clearHistoryButton;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        assignViews(view); // Call assignViews() before accessing textViewContent
        databaseHelper = new DatabaseHelper(getActivity());
        UserEmail = LoginFragment.LoggedInUser;
        textViewContent.setText(loadHistoryForUser());
        assignButton(view);
        clearHistoryButton.setOnClickListener(v -> clearHistory());
        return view;
    }

    public void assignViews(View view) {
        textViewContent = view.findViewById(R.id.textView_Content);
    }

    public void assignButton(View view) {
        clearHistoryButton = view.findViewById(R.id.button_Clear);
    }

    public String loadHistoryForUser() {
        Cursor cursor = databaseHelper.getHistoryForUser(UserEmail);

        StringBuilder historyContentBuilder = new StringBuilder();
        if (cursor == null || cursor.getCount() == 0) { // Check if cursor is null or empty
            cursor = databaseHelper.getHistoryForGoogleUser(UserEmail); // Fallback to Google user history
        }

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int resultIndex = cursor.getColumnIndex(HistoryDB.COLUMN_RESULT);
                    do {
                        String result = cursor.getString(resultIndex);
                        historyContentBuilder.append(result).append("\n \n \n");
                        // No need to log id and userId for Google user history
                        Log.d("HistoryFragment", "result: " + result);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }

            String historyContent = historyContentBuilder.toString();
            Log.d("HistoryFragment", "History content:\n" + historyContent);
        } else {
            Log.e("LocalHistoryFragment", "Cursor is null");
        }
        return historyContentBuilder.toString();
    }


    public void clearHistory() {
        // Clear history for the current user
        boolean isHistoryCleared = databaseHelper.deleteHistoryForUser(UserEmail);
        if (!isHistoryCleared) {
            isHistoryCleared = databaseHelper.deleteHistoryForGoogleUser(UserEmail);
        }

        if (isHistoryCleared) {
            textViewContent.setText("");
        }
    }


}
