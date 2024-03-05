package com.example.patgpt;

public class HistoryGoogleUserDB {


    public static final String TABLE_NAME = "historyGoogleUser";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_E_MAIL = "userEmail"; // Foreign key
    public static final String COLUMN_RESULT = "result";

    private int id;
    private int userEmail;
    private String result;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_E_MAIL + " TEXT,"
                    + COLUMN_RESULT + " TEXT"
                    + ")";

    public HistoryGoogleUserDB(int userEmail, String result) {
        this.userEmail = userEmail;
        this.result = result;
    }


}
