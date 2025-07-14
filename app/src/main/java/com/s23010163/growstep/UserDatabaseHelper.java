package com.s23010163.growstep;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ue3.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String TABLE_GROUPS = "groups";
    public static final String COLUMN_GROUP_ID = "id";
    public static final String COLUMN_GROUP_NAME = "name";
    public static final String COLUMN_GROUP_TIME = "time";
    public static final String COLUMN_GROUP_PARTICIPANTS = "participants";
    public static final String COLUMN_GROUP_ROUTE = "route";
    public static final String TABLE_GROUP_MEMBERS = "group_members";
    public static final String COLUMN_MEMBER_GROUP_ID = "group_id";
    public static final String COLUMN_MEMBER_USERNAME = "username";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + " ("
                + COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_GROUP_NAME + " TEXT, "
                + COLUMN_GROUP_TIME + " TEXT, "
                + COLUMN_GROUP_PARTICIPANTS + " INTEGER, "
                + COLUMN_GROUP_ROUTE + " TEXT)";
        db.execSQL(CREATE_GROUPS_TABLE);

        String CREATE_GROUP_MEMBERS_TABLE = "CREATE TABLE " + TABLE_GROUP_MEMBERS + " ("
                + COLUMN_MEMBER_GROUP_ID + " INTEGER, "
                + COLUMN_MEMBER_USERNAME + " TEXT)";
        db.execSQL(CREATE_GROUP_MEMBERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_MEMBERS);
        onCreate(db);
    }

    // Register user
    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Validate login
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // Check if username exists
    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // Insert a new group
    public boolean insertGroup(String name, String time, int participants, String route) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, name);
        values.put(COLUMN_GROUP_TIME, time);
        values.put(COLUMN_GROUP_PARTICIPANTS, participants);
        values.put(COLUMN_GROUP_ROUTE, route);
        long result = db.insert(TABLE_GROUPS, null, values);
        db.close();
        return result != -1;
    }

    // Add a user to a group
    public void addGroupMember(int groupId, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEMBER_GROUP_ID, groupId);
        values.put(COLUMN_MEMBER_USERNAME, username);
        db.insert(TABLE_GROUP_MEMBERS, null, values);
        db.close();
    }

    // Get all members of a group
    public Cursor getGroupMembers(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GROUP_MEMBERS, null, COLUMN_MEMBER_GROUP_ID + "=?", new String[]{String.valueOf(groupId)}, null, null, null);
    }

    // Get all groups
    public Cursor getAllGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GROUPS, null, null, null, null, null, null);
    }
} 