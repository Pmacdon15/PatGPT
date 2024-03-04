package com.example.patgpt.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.patgpt.DatabaseHelper;
import com.example.patgpt.HistoryDB;
import com.example.patgpt.R;
import com.example.patgpt.ui.ui.login.LoginViewModel;

public class HistoryFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private String UserEmail;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        databaseHelper = new DatabaseHelper(getActivity());
        UserEmail = LoginViewModel.profileUsername;
        loadHistoryForUser();

        return view;
    }

    public void loadHistoryForUser() {
        Cursor cursor = databaseHelper.getHistoryForUser(UserEmail);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(HistoryDB.COLUMN_ID);
                int userIdIndex = cursor.getColumnIndex(HistoryDB.COLUMN_USER_ID);
                int resultIndex = cursor.getColumnIndex(HistoryDB.COLUMN_RESULT);

                do {
                    // Check if column indexes are valid before accessing data
                    if (idIndex != -1 && userIdIndex != -1 && resultIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        int userId = cursor.getInt(userIdIndex);
                        String result = cursor.getString(resultIndex);
                        Log.d("HistoryFragment", "id: " + id + ", userId: " + userId + ", result: " + result);
                    } else {
                        Log.e("HistoryFragment", "Invalid column index detected");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            Log.e("HistoryFragment", "Cursor is null");
        }
    }





}
