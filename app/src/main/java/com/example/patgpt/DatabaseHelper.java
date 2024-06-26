package com.example.patgpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 5;
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
        // Insert data into the database
        ContentValues values = new ContentValues();
        values.put(UserDB.COLUMN_EMAIL, "admin");
        values.put(UserDB.COLUMN_FIRST_NAME, "Admin");
        values.put(UserDB.COLUMN_LAST_NAME, "Admin");
        values.put(UserDB.COLUMN_PASSWORD, "admin");
        db.insert(UserDB.TABLE_NAME, null, values);

        // Create History table
        db.execSQL(HistoryDB.CREATE_TABLE);
        // Insert data into the history table
        ContentValues historyValues = new ContentValues();
        historyValues.put(HistoryDB.COLUMN_USER_ID, 1);
        historyValues.put(HistoryDB.COLUMN_RESULT, "This is a sample result");
        db.insert(HistoryDB.TABLE_NAME, null, historyValues);
        // Create HistoryGoogleUser table
        db.execSQL(HistoryGoogleUserDB.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed and create new tables
        db.execSQL("DROP TABLE IF EXISTS " + UserDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HistoryGoogleUserDB.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Method to query email and password from the database
    public boolean checkUser(String email, String password) {
        // Select All Query from the UserDB
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + UserDB.TABLE_NAME + " WHERE "
                + UserDB.COLUMN_EMAIL + " = ? AND "
                + UserDB.COLUMN_PASSWORD + " = ?";
        // Get the cursor which contains the result
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean result = cursor.getCount() > 0;
        // Close the cursor
        cursor.close();
        // return the result
        return result;
    }
    // Method to check if the user exists in the database returns true if the user exists
    public boolean doesUserExist(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + UserDB.TABLE_NAME + " WHERE "
                + UserDB.COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }
    // Method to check if the user exists in the database returns true if the user exists
    private boolean isGoogleUser(String email) {
        // Select All Query from the HistoryGoogleUserDB returns true if the user is a Google user
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + HistoryGoogleUserDB.TABLE_NAME + " WHERE "
                + HistoryGoogleUserDB.COLUMN_USER_E_MAIL + " = ?";
        // Get the cursor which contains the result
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean isGoogleUser = cursor.getCount() > 0;
        cursor.close();
        return isGoogleUser;
    }
    // Method to add a user to the database returns true if the user is added successfully
    public boolean addUser(String email, String firstName, String lastName, String password) {
        // Inserting Row in the UserDB
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDB.COLUMN_EMAIL, email);
        values.put(UserDB.COLUMN_FIRST_NAME, firstName);
        values.put(UserDB.COLUMN_LAST_NAME, lastName);
        values.put(UserDB.COLUMN_PASSWORD, password);
        long newRowId = db.insert(UserDB.TABLE_NAME, null, values);
        // Return true if the user is added successfully
        return newRowId != -1;
    }
    // Method to add a Google user to the database returns true if the user is added successfully
    public String getFirstName(String email) {
        // Select All Query from the UserDB to get the first name based on email
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + UserDB.COLUMN_FIRST_NAME + " FROM " + UserDB.TABLE_NAME + " WHERE "
                + UserDB.COLUMN_EMAIL + " = ?";
        // Get the cursor which contains the result
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String firstName = "";
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(UserDB.COLUMN_FIRST_NAME);
            // if the column index is not -1 then get the first name
            if (columnIndex != -1) {
                firstName = cursor.getString(columnIndex);
            }
        }
        cursor.close();
        return firstName;
    }
    // Method to get the last name based on email returns the last name
    public String getLastName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + UserDB.COLUMN_LAST_NAME + " FROM " + UserDB.TABLE_NAME + " WHERE "
                + UserDB.COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String lastName = "";
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(UserDB.COLUMN_LAST_NAME);
            // if the column index is not -1 then get the last name
            if (columnIndex != -1) {
                lastName = cursor.getString(columnIndex);
            }
        }
        cursor.close();
        return lastName;
    }
    // Method to edit the first name based on email returns true if the first name is edited successfully
    public boolean editUserFirstNameDB(String email, String newFirstName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDB.COLUMN_FIRST_NAME, newFirstName);
        int rowsAffected = db.update(UserDB.TABLE_NAME, values, UserDB.COLUMN_EMAIL + " = ?", new String[]{email});
        // if the rows affected is greater than 0 then return true becuase there is a successful edit
        return rowsAffected > 0;
    }
    // Method to edit the last name based on email returns true if the last name is edited successfully
    public boolean editUserLastNameDB(String email, String newLastName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDB.COLUMN_LAST_NAME, newLastName);
        int rowsAffected = db.update(UserDB.TABLE_NAME, values, UserDB.COLUMN_EMAIL + " = ?", new String[]{email});
        // if the rows affected is greater than 0 then return true becuase there is a successful edit
        return rowsAffected > 0;
    }
    // Method to confirm the user password based on email returns true if the password is correct
    private boolean confirmUserPasswordDB(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + UserDB.TABLE_NAME + " WHERE "
                + UserDB.COLUMN_EMAIL + " = ? AND "
                + UserDB.COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        // result is true if the password is correct
        return result;
    }
    // Method to edit the user password based on email returns true if the password is edited successfully
    public boolean editUserPasswordDB(String email, String currentPassword, String newPassword, String confirmPassword) {
        // Check if the current password is correct
        if (!confirmUserPasswordDB(email, currentPassword)) return false; // return false if the password is incorrect
        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) return false; // return false if the new password and confirm password do not match
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDB.COLUMN_PASSWORD, newPassword);
        int rowsAffected = db.update(UserDB.TABLE_NAME, values, UserDB.COLUMN_EMAIL + " = ? AND " + UserDB.COLUMN_PASSWORD + " = ?", new String[]{email, currentPassword});
        return rowsAffected > 0;
    }
    // Method to get the user id based on email returns the history for user
    public Cursor getHistoryForUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Querying UserDB to get userId based on email
        Cursor userCursor = db.query(UserDB.TABLE_NAME, new String[]{UserDB.COLUMN_USER_ID}, UserDB.COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int userId = -1; // Default value if user is not found
        if (userCursor.moveToFirst()) {
            int columnIndex = userCursor.getColumnIndex(UserDB.COLUMN_USER_ID);
            if (columnIndex != -1) {
                userId = userCursor.getInt(columnIndex);
            }
        }
        userCursor.close();

        // Querying History table based on userId
        Cursor historyCursor = null;
        if (userId != -1) {
            historyCursor = db.query(HistoryDB.TABLE_NAME, null, HistoryDB.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        }

        // You can return the cursor here, and handle it accordingly in the calling code
        return historyCursor;
    }
    // Method to get the user id based on email returns history for Google user
    public Cursor getHistoryForGoogleUser(String email) {
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + HistoryGoogleUserDB.TABLE_NAME + " WHERE "
                    + HistoryGoogleUserDB.COLUMN_USER_E_MAIL + " = ?";
            return db.rawQuery(query, new String[]{email});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // Method to delete the history for the user returns true if the history is deleted successfully
    public Boolean deleteHistoryForUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the user is a regular user or a Google user
        boolean isGoogleUser = isGoogleUser(email);

        if (isGoogleUser) {
            // If the user is a Google user, delete history from the Google user table
            return deleteHistoryForGoogleUser(email);
        } else {
            // If the user is a regular user, delete history from the regular user table
            // Querying UserDB to get userId based on email
            Cursor userCursor = db.query(UserDB.TABLE_NAME, new String[]{UserDB.COLUMN_USER_ID}, UserDB.COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
            int userId = -1; // Default value if user is not found
            if (userCursor.moveToFirst()) {
                int columnIndex = userCursor.getColumnIndex(UserDB.COLUMN_USER_ID);
                if (columnIndex != -1) {
                    userId = userCursor.getInt(columnIndex);
                }
            }
            userCursor.close();

            // Deleting from History table
            int rowsAffected = db.delete(HistoryDB.TABLE_NAME, HistoryDB.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
            return rowsAffected > 0;
        }
    }

    public Boolean deleteHistoryForGoogleUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(HistoryGoogleUserDB.TABLE_NAME, HistoryGoogleUserDB.COLUMN_USER_E_MAIL + "=?", new String[]{email});
        return rowsAffected > 0;
    }

    public boolean addHistoryForUser(String email, String result) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Querying UserDB to get userId based on email
        Cursor userCursor = db.query(UserDB.TABLE_NAME, new String[]{UserDB.COLUMN_USER_ID}, UserDB.COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int userId = -1; // Default value if user is not found
        if (userCursor.moveToFirst()) {
            int columnIndex = userCursor.getColumnIndex(UserDB.COLUMN_USER_ID);
            if (columnIndex != -1) {
                userId = userCursor.getInt(columnIndex);
            }
        }
        // Check if user exists in the database
        if (userId == -1) {
            return false;
        }
        userCursor.close();

        // Inserting into History table
        ContentValues values = new ContentValues();
        values.put(HistoryDB.COLUMN_USER_ID, userId);
        values.put(HistoryDB.COLUMN_RESULT, result);
        long newRowId = db.insert(HistoryDB.TABLE_NAME, null, values);
        return newRowId != -1;
    }

    public boolean addHistoryGoogleUser(String email, String result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HistoryGoogleUserDB.COLUMN_USER_E_MAIL, email);
        values.put(HistoryGoogleUserDB.COLUMN_RESULT, result);
        long newRowId = db.insert(HistoryGoogleUserDB.TABLE_NAME, null, values);
        return newRowId != -1;
    }
}

