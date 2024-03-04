package com.example.patgpt;

public class HistoryDB {
    public static final String TABLE_NAME = "history";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "userId"; // Foreign key
    public static final String COLUMN_RESULT = "result";

    private int id;
    private int userId;
    private String result;


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_ID + " INTEGER,"
                    + COLUMN_RESULT + " TEXT,"
                    // Define foreign key constraint
                    + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + UserDB.TABLE_NAME + "(" + UserDB.COLUMN_USER_ID + ")"
                    + ")";

    public HistoryDB(int userId, String result) {
        this.userId = userId;
        this.result = result;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}

