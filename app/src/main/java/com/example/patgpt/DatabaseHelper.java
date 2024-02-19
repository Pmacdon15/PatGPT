package com.example.patgpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "AppDBManager.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create users table
        db.execSQL(UserDB.CREATE_TABLE);
        // Create tables

        // Insert data into the database
        ContentValues values = new ContentValues();
        values.put(UserDB.COLUMN_EMAIL, "admin");
        values.put(UserDB.COLUMN_FIRST_NAME, "Admin");
        values.put(UserDB.COLUMN_LAST_NAME, "Admin");
        values.put(UserDB.COLUMN_PASSWORD, "admin");
        db.insert(UserDB.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + UserDB.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Method to query email and password from the database
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + UserDB.TABLE_NAME + " WHERE "
                + UserDB.COLUMN_EMAIL + " = ? AND "
                + UserDB.COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }
    public String getFirstName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + UserDB.COLUMN_FIRST_NAME + " FROM " + UserDB.TABLE_NAME + " WHERE "
                + UserDB.COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String firstName = "";
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(UserDB.COLUMN_FIRST_NAME);
            if (columnIndex != -1) {
                firstName = cursor.getString(columnIndex);
            }
        }
        cursor.close();
        return firstName;
    }
    public String getLastName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + UserDB.COLUMN_LAST_NAME + " FROM " + UserDB.TABLE_NAME + " WHERE "
                + UserDB.COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String lastName = "";
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(UserDB.COLUMN_LAST_NAME);
            if (columnIndex != -1) {
                lastName = cursor.getString(columnIndex);
            }
        }
        cursor.close();
        return lastName;
    }

    public boolean editUserFirstNameDB(String email, String newFirstName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDB.COLUMN_FIRST_NAME, newFirstName);
        int rowsAffected = db.update(UserDB.TABLE_NAME, values, UserDB.COLUMN_EMAIL + " = ?", new String[]{email});
        return rowsAffected > 0;
    }

}

