package com.s23010163.growstep;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ue3.db";
    private static final int DATABASE_VERSION = 4;
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
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_MESSAGE_GROUP_ID = "group_id";
    public static final String COLUMN_MESSAGE_USERNAME = "username";
    public static final String COLUMN_MESSAGE_TEXT = "text";
    public static final String COLUMN_MESSAGE_TIMESTAMP = "timestamp";
    
    // Weekly Steps Table
    public static final String TABLE_WEEKLY_STEPS = "weekly_steps";
    public static final String COLUMN_WEEKLY_ID = "id";
    public static final String COLUMN_WEEKLY_USERNAME = "username";
    public static final String COLUMN_WEEKLY_DATE = "date";
    public static final String COLUMN_WEEKLY_STEPS = "steps";
    public static final String COLUMN_WEEKLY_DISTANCE = "distance";
    public static final String COLUMN_WEEKLY_CALORIES = "calories";
    public static final String COLUMN_WEEKLY_WEEK_START = "week_start";

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

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + " ("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MESSAGE_GROUP_ID + " INTEGER, "
                + COLUMN_MESSAGE_USERNAME + " TEXT, "
                + COLUMN_MESSAGE_TEXT + " TEXT, "
                + COLUMN_MESSAGE_TIMESTAMP + " INTEGER)";
        db.execSQL(CREATE_MESSAGES_TABLE);
        
        String CREATE_WEEKLY_STEPS_TABLE = "CREATE TABLE " + TABLE_WEEKLY_STEPS + " ("
                + COLUMN_WEEKLY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_WEEKLY_USERNAME + " TEXT, "
                + COLUMN_WEEKLY_DATE + " TEXT, "
                + COLUMN_WEEKLY_STEPS + " INTEGER DEFAULT 0, "
                + COLUMN_WEEKLY_DISTANCE + " REAL DEFAULT 0.0, "
                + COLUMN_WEEKLY_CALORIES + " REAL DEFAULT 0.0, "
                + COLUMN_WEEKLY_WEEK_START + " TEXT, "
                + "UNIQUE(" + COLUMN_WEEKLY_USERNAME + ", " + COLUMN_WEEKLY_DATE + "))";
        db.execSQL(CREATE_WEEKLY_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Add weekly steps table for new installations
            String CREATE_WEEKLY_STEPS_TABLE = "CREATE TABLE " + TABLE_WEEKLY_STEPS + " ("
                    + COLUMN_WEEKLY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_WEEKLY_USERNAME + " TEXT, "
                    + COLUMN_WEEKLY_DATE + " TEXT, "
                    + COLUMN_WEEKLY_STEPS + " INTEGER DEFAULT 0, "
                    + COLUMN_WEEKLY_DISTANCE + " REAL DEFAULT 0.0, "
                    + COLUMN_WEEKLY_CALORIES + " REAL DEFAULT 0.0, "
                    + COLUMN_WEEKLY_WEEK_START + " TEXT, "
                    + "UNIQUE(" + COLUMN_WEEKLY_USERNAME + ", " + COLUMN_WEEKLY_DATE + "))";
            db.execSQL(CREATE_WEEKLY_STEPS_TABLE);
        }
        // Don't drop existing tables - just add the new one
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

    // Check if a user is already a member of a group
    public boolean isGroupMember(int groupId, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_MEMBER_GROUP_ID + "=? AND " + COLUMN_MEMBER_USERNAME + "=?";
        String[] selectionArgs = {String.valueOf(groupId), username};
        Cursor cursor = db.query(TABLE_GROUP_MEMBERS, null, selection, selectionArgs, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
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

    public void insertMessage(int groupId, String username, String text, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_GROUP_ID, groupId);
        values.put(COLUMN_MESSAGE_USERNAME, username);
        values.put(COLUMN_MESSAGE_TEXT, text);
        values.put(COLUMN_MESSAGE_TIMESTAMP, timestamp);
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public Cursor getMessagesForGroup(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_MESSAGES, null, COLUMN_MESSAGE_GROUP_ID + "=?",
                new String[]{String.valueOf(groupId)}, null, null, COLUMN_MESSAGE_TIMESTAMP + " ASC");
    }

    // Update group route and details
    public boolean updateGroupRoute(int groupId, String route) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ROUTE, route);
        int rows = db.update(TABLE_GROUPS, values, COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupId)});
        db.close();
        return rows > 0;
    }

    // Update username across all related tables
    public boolean updateUsername(String oldUsername, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Update users table
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_USERNAME, newUsername);
            int affectedUsers = db.update(TABLE_USERS, userValues, COLUMN_USERNAME + "=?", new String[]{oldUsername});
            if (affectedUsers <= 0) {
                db.endTransaction();
                return false;
            }

            // Update group_members table
            ContentValues memberValues = new ContentValues();
            memberValues.put(COLUMN_MEMBER_USERNAME, newUsername);
            db.update(TABLE_GROUP_MEMBERS, memberValues, COLUMN_MEMBER_USERNAME + "=?", new String[]{oldUsername});

            // Update messages table
            ContentValues messageValues = new ContentValues();
            messageValues.put(COLUMN_MESSAGE_USERNAME, newUsername);
            db.update(TABLE_MESSAGES, messageValues, COLUMN_MESSAGE_USERNAME + "=?", new String[]{oldUsername});

            // Update weekly_steps table
            ContentValues weeklyValues = new ContentValues();
            weeklyValues.put(COLUMN_WEEKLY_USERNAME, newUsername);
            db.update(TABLE_WEEKLY_STEPS, weeklyValues, COLUMN_WEEKLY_USERNAME + "=?", new String[]{oldUsername});

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Weekly Steps Methods
    /**
     * Get current week start date (Monday) in yyyy-MM-dd format
     */
    public String getCurrentWeekStart() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    /**
     * Get today's date in yyyy-MM-dd format
     */
    public String getTodayDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    /**
     * Add or update daily steps for a user
     */
    public boolean addDailySteps(String username, int steps, float distance, float calories) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String today = getTodayDate();
            String weekStart = getCurrentWeekStart();
            
            ContentValues values = new ContentValues();
            values.put(COLUMN_WEEKLY_USERNAME, username);
            values.put(COLUMN_WEEKLY_DATE, today);
            values.put(COLUMN_WEEKLY_STEPS, steps);
            values.put(COLUMN_WEEKLY_DISTANCE, distance);
            values.put(COLUMN_WEEKLY_CALORIES, calories);
            values.put(COLUMN_WEEKLY_WEEK_START, weekStart);
            
            long result = db.insertWithOnConflict(TABLE_WEEKLY_STEPS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get daily steps for a specific user and date
     */
    public android.database.Cursor getDailySteps(String username, String date) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selection = COLUMN_WEEKLY_USERNAME + "=? AND " + COLUMN_WEEKLY_DATE + "=?";
            String[] selectionArgs = {username, date};
            return db.query(TABLE_WEEKLY_STEPS, null, selection, selectionArgs, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all daily steps for a user for the current week
     */
    public android.database.Cursor getThisWeekSteps(String username) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String weekStart = getCurrentWeekStart();
            String selection = COLUMN_WEEKLY_USERNAME + "=? AND " + COLUMN_WEEKLY_WEEK_START + "=?";
            String[] selectionArgs = {username, weekStart};
            return db.query(TABLE_WEEKLY_STEPS, null, selection, selectionArgs, null, null, COLUMN_WEEKLY_DATE + " ASC");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get total steps for this week
     */
    public int getThisWeekTotalSteps(String username) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String weekStart = getCurrentWeekStart();
            String selection = COLUMN_WEEKLY_USERNAME + "=? AND " + COLUMN_WEEKLY_WEEK_START + "=?";
            String[] selectionArgs = {username, weekStart};
            
            android.database.Cursor cursor = db.query(TABLE_WEEKLY_STEPS, 
                new String[]{"SUM(" + COLUMN_WEEKLY_STEPS + ") as total_steps"}, 
                selection, selectionArgs, null, null, null);
            
            int totalSteps = 0;
            if (cursor != null && cursor.moveToFirst()) {
                totalSteps = cursor.getInt(cursor.getColumnIndex("total_steps"));
                cursor.close();
            }
            db.close();
            return totalSteps;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get total distance for this week
     */
    public float getThisWeekTotalDistance(String username) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String weekStart = getCurrentWeekStart();
            String selection = COLUMN_WEEKLY_USERNAME + "=? AND " + COLUMN_WEEKLY_WEEK_START + "=?";
            String[] selectionArgs = {username, weekStart};
            
            android.database.Cursor cursor = db.query(TABLE_WEEKLY_STEPS, 
                new String[]{"SUM(" + COLUMN_WEEKLY_DISTANCE + ") as total_distance"}, 
                selection, selectionArgs, null, null, null);
            
            float totalDistance = 0f;
            if (cursor != null && cursor.moveToFirst()) {
                totalDistance = cursor.getFloat(cursor.getColumnIndex("total_distance"));
                cursor.close();
            }
            db.close();
            return totalDistance;
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }

    /**
     * Get total calories for this week
     */
    public float getThisWeekTotalCalories(String username) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String weekStart = getCurrentWeekStart();
            String selection = COLUMN_WEEKLY_USERNAME + "=? AND " + COLUMN_WEEKLY_WEEK_START + "=?";
            String[] selectionArgs = {username, weekStart};
            
            android.database.Cursor cursor = db.query(TABLE_WEEKLY_STEPS, 
                new String[]{"SUM(" + COLUMN_WEEKLY_CALORIES + ") as total_calories"}, 
                selection, selectionArgs, null, null, null);
            
            float totalCalories = 0f;
            if (cursor != null && cursor.moveToFirst()) {
                totalCalories = cursor.getFloat(cursor.getColumnIndex("total_calories"));
                cursor.close();
            }
            db.close();
            return totalCalories;
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }

    /**
     * Get group total steps for this week
     */
    public int getGroupThisWeekTotalSteps(int groupId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String weekStart = getCurrentWeekStart();
            
            String query = "SELECT SUM(ws." + COLUMN_WEEKLY_STEPS + ") as total_steps " +
                          "FROM " + TABLE_WEEKLY_STEPS + " ws " +
                          "JOIN " + TABLE_GROUP_MEMBERS + " gm ON ws." + COLUMN_WEEKLY_USERNAME + " = gm." + COLUMN_MEMBER_USERNAME + " " +
                          "WHERE gm." + COLUMN_MEMBER_GROUP_ID + " = ? AND ws." + COLUMN_WEEKLY_WEEK_START + " = ?";
            
            android.database.Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId), weekStart});
            
            int totalSteps = 0;
            if (cursor != null && cursor.moveToFirst()) {
                totalSteps = cursor.getInt(cursor.getColumnIndex("total_steps"));
                cursor.close();
            }
            db.close();
            return totalSteps;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get group total distance for this week
     */
    public float getGroupThisWeekTotalDistance(int groupId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String weekStart = getCurrentWeekStart();
            
            String query = "SELECT SUM(ws." + COLUMN_WEEKLY_DISTANCE + ") as total_distance " +
                          "FROM " + TABLE_WEEKLY_STEPS + " ws " +
                          "JOIN " + TABLE_GROUP_MEMBERS + " gm ON ws." + COLUMN_WEEKLY_USERNAME + " = gm." + COLUMN_MEMBER_USERNAME + " " +
                          "WHERE gm." + COLUMN_MEMBER_GROUP_ID + " = ? AND ws." + COLUMN_WEEKLY_WEEK_START + " = ?";
            
            android.database.Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId), weekStart});
            
            float totalDistance = 0f;
            if (cursor != null && cursor.moveToFirst()) {
                totalDistance = cursor.getFloat(cursor.getColumnIndex("total_distance"));
                cursor.close();
            }
            db.close();
            return totalDistance;
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }

    /**
     * Get group total calories for this week
     */
    public float getGroupThisWeekTotalCalories(int groupId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String weekStart = getCurrentWeekStart();
            
            String query = "SELECT SUM(ws." + COLUMN_WEEKLY_CALORIES + ") as total_calories " +
                          "FROM " + TABLE_WEEKLY_STEPS + " ws " +
                          "JOIN " + TABLE_GROUP_MEMBERS + " gm ON ws." + COLUMN_WEEKLY_USERNAME + " = gm." + COLUMN_MEMBER_USERNAME + " " +
                          "WHERE gm." + COLUMN_MEMBER_GROUP_ID + " = ? AND ws." + COLUMN_WEEKLY_WEEK_START + " = ?";
            
            android.database.Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId), weekStart});
            
            float totalCalories = 0f;
            if (cursor != null && cursor.moveToFirst()) {
                totalCalories = cursor.getFloat(cursor.getColumnIndex("total_calories"));
                cursor.close();
            }
            db.close();
            return totalCalories;
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }
} 