package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

public class GroupsActivity extends AppCompatActivity {

    LinearLayout navHome, navGroups, navChallenges, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Bottom nav bar buttons
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);

        // Navigation handling
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(GroupsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navGroups.setOnClickListener(v -> {
            // Already on Groups
        });

        navChallenges.setOnClickListener(v -> {
            Intent intent = new Intent(GroupsActivity.this, ChallengesActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(GroupsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Floating center start button
        TextView startLabel = findViewById(R.id.startLabel);
        startLabel.setOnClickListener(v -> {
            Intent intent = new Intent(GroupsActivity.this, StartWalkingActivity.class);
            startActivity(intent);
        });

        // "Create Group" button
        Button createGroupButton = findViewById(R.id.createGroup);
        createGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupsActivity.this, CreateGroupWalkActivity.class);
            startActivity(intent);
        });

        // Dynamically load groups
        LinearLayout groupsContainer = findViewById(R.id.groupsContainer);
        groupsContainer.removeAllViews();
        try {
            UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
            android.database.Cursor cursor = dbHelper.getAllGroups();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final int groupIndex = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_GROUP_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_GROUP_NAME));
                    String route = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_GROUP_ROUTE));
                    int participants = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_GROUP_PARTICIPANTS));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_GROUP_TIME));
                    // Create CardView for each group
                    CardView card = new CardView(this);
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(0, 0, 0, 24);
                    card.setLayoutParams(cardParams);
                    card.setRadius(20f);
                    card.setCardElevation(6f);
                    card.setUseCompatPadding(true);
                    card.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);

                    LinearLayout cardContent = new LinearLayout(this);
                    cardContent.setOrientation(LinearLayout.VERTICAL);
                    cardContent.setBackgroundResource(R.drawable.walking_group_backgroundcolor);
                    cardContent.setPadding(32, 32, 32, 32);

                    TextView tvName = new TextView(this);
                    tvName.setText(name);
                    tvName.setTextColor(android.graphics.Color.WHITE);
                    tvName.setTextSize(18f);
                    tvName.setTypeface(null, android.graphics.Typeface.BOLD);
                    cardContent.addView(tvName);

                    // Get actual member count
                    android.database.Cursor memberCountCursor = dbHelper.getGroupMembers(groupIndex);
                    int actualMemberCount = 0;
                    if (memberCountCursor != null) {
                        actualMemberCount = memberCountCursor.getCount();
                        memberCountCursor.close();
                    }
                    
                    TextView tvRoute = new TextView(this);
                    tvRoute.setText(route + " • " + actualMemberCount + " member" + (actualMemberCount != 1 ? "s" : ""));
                    tvRoute.setTextColor(android.graphics.Color.WHITE);
                    tvRoute.setTextSize(14f);
                    cardContent.addView(tvRoute);

                    TextView tvTime = new TextView(this);
                    tvTime.setText("Starts at " + time);
                    tvTime.setTextColor(android.graphics.Color.WHITE);
                    tvTime.setTextSize(14f);
                    tvTime.setPadding(0, 8, 0, 0);
                    cardContent.addView(tvTime);

                    // Weekly steps info
                    try {
                        int groupWeeklySteps = dbHelper.getGroupThisWeekTotalSteps(groupIndex);
                        float groupWeeklyDistance = dbHelper.getGroupThisWeekTotalDistance(groupIndex);
                        float groupWeeklyCalories = dbHelper.getGroupThisWeekTotalCalories(groupIndex);
                        
                        TextView tvWeeklyStats = new TextView(this);
                        tvWeeklyStats.setText(String.format("This Week: %,d steps • %.2f km • %.0f kcal", 
                            groupWeeklySteps, groupWeeklyDistance, groupWeeklyCalories));
                        tvWeeklyStats.setTextColor(android.graphics.Color.WHITE);
                        tvWeeklyStats.setTextSize(12f);
                        tvWeeklyStats.setPadding(0, 8, 0, 0);
                        cardContent.addView(tvWeeklyStats);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // If there's an error, just continue without showing weekly stats
                    }
                    
                    // Show if user is already a member
                    String currentUsername = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
                    if (dbHelper.isGroupMember(groupIndex, currentUsername)) {
                        TextView tvMemberStatus = new TextView(this);
                        tvMemberStatus.setText("✓ You're a member");
                        tvMemberStatus.setTextColor(android.graphics.Color.parseColor("#90EE90")); // Light green
                        tvMemberStatus.setTextSize(12f);
                        tvMemberStatus.setPadding(0, 4, 0, 0);
                        cardContent.addView(tvMemberStatus);
                    }

                    // Button row
                    LinearLayout buttonRow = new LinearLayout(this);
                    buttonRow.setOrientation(LinearLayout.HORIZONTAL);
                    buttonRow.setPadding(0, 16, 0, 0);

                    int spacingPx = (int) (8 * getResources().getDisplayMetrics().density); // 8dp
                    LinearLayout.LayoutParams btnParamsJoin = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    btnParamsJoin.setMargins(0, 0, spacingPx, 0);
                    LinearLayout.LayoutParams btnParamsDelete = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    btnParamsDelete.setMargins(0, 0, spacingPx, 0);
                    LinearLayout.LayoutParams btnParamsShare = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    btnParamsShare.setMargins(0, 0, 0, 0);

                    int minButtonWidth = (int) (64 * getResources().getDisplayMetrics().density); // 64dp
                    int minButtonHeight = (int) (48 * getResources().getDisplayMetrics().density); // 48dp

                    // Join Button
                    Button btnJoin = new Button(this);
                    boolean isAlreadyMember = dbHelper.isGroupMember(groupIndex, currentUsername);
                    if (isAlreadyMember) {
                        btnJoin.setText("Open Group");
                        btnJoin.setBackgroundResource(R.drawable.bg_button_share); // Different color for members
                    } else {
                        btnJoin.setText("Join");
                        btnJoin.setBackgroundResource(R.drawable.bg_button_edit); // purple background
                    }
                    btnJoin.setTextColor(android.graphics.Color.WHITE);
                    Drawable iconJoin = ContextCompat.getDrawable(this, R.drawable.ic_person);
                    btnJoin.setCompoundDrawablesWithIntrinsicBounds(iconJoin, null, null, null);
                    btnJoin.setCompoundDrawablePadding((int)(8 * getResources().getDisplayMetrics().density));
                    btnJoin.setLayoutParams(btnParamsJoin);
                    btnJoin.setAllCaps(false);
                    btnJoin.setTextSize(15);
                    btnJoin.setMinWidth(minButtonWidth);
                    btnJoin.setMinHeight(minButtonHeight);
                    btnJoin.setPadding(0, (int)(8 * getResources().getDisplayMetrics().density), 0, (int)(8 * getResources().getDisplayMetrics().density));
                    btnJoin.setOnClickListener(v -> {
                        // Add current user as group member with error handling
                        String username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
                        if (username == null || username.isEmpty()) {
                            android.widget.Toast.makeText(this, "No user logged in!", android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        // If already a member, just open the group
                        if (isAlreadyMember) {
                            Intent intent = new Intent(this, WalkingGroupActivity.class);
                            intent.putExtra("group_id", groupIndex);
                            intent.putExtra("group_name", name);
                            intent.putExtra("group_participants", participants);
                            startActivity(intent);
                            return;
                        }
                        
                        try {
                            UserDatabaseHelper db = new UserDatabaseHelper(this);
                            
                            // First, get all existing members of the group BEFORE adding the new user
                            android.database.Cursor existingMembersCursor = db.getGroupMembers(groupIndex);
                            java.util.HashSet<String> existingMembers = new java.util.HashSet<>();
                            if (existingMembersCursor != null && existingMembersCursor.moveToFirst()) {
                                do {
                                    String member = existingMembersCursor.getString(existingMembersCursor.getColumnIndexOrThrow("username"));
                                    if (!member.equals(username)) {
                                        existingMembers.add(member);
                                    }
                                } while (existingMembersCursor.moveToNext());
                                existingMembersCursor.close();
                            }
                            
                            // Now add the user to the group
                            db.addGroupMember(groupIndex, username);
                            
                            // Add all existing group members as friends for the joining user
                            android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            java.util.Set<String> existingFriends = prefs.getStringSet("friends", new java.util.HashSet<>());
                            java.util.HashSet<String> updatedFriendsSet = new java.util.HashSet<>();
                            if (existingFriends != null) {
                                updatedFriendsSet.addAll(existingFriends);
                            }
                            updatedFriendsSet.addAll(existingMembers);
                            prefs.edit().putStringSet("friends", updatedFriendsSet).apply();
                            
                            // Show success message with number of new friends
                            if (!existingMembers.isEmpty()) {
                                String friendText = existingMembers.size() == 1 ? "friend" : "friends";
                                android.widget.Toast.makeText(this, 
                                    "Joined group! Added " + existingMembers.size() + " new " + friendText, 
                                    android.widget.Toast.LENGTH_SHORT).show();
                            } else {
                                android.widget.Toast.makeText(this, "Joined group! You're the first member.", 
                                    android.widget.Toast.LENGTH_SHORT).show();
                            }
                            
                            String joinedKey = "joined_group_" + groupIndex;
                            if (!prefs.getBoolean(joinedKey, false)) {
                                int groupsJoined = prefs.getInt("groups_joined", 0) + 1;
                                prefs.edit().putInt("groups_joined", groupsJoined).putBoolean(joinedKey, true).apply();
                                // Notify challenge progress update
                                android.content.Intent intentUpdate = new android.content.Intent("com.growstep.CHALLENGE_PROGRESS_UPDATED");
                                sendBroadcast(intentUpdate);
                            }
                        } catch (Exception e) {
                            android.widget.Toast.makeText(this, "Failed to join group: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(this, WalkingGroupActivity.class);
                        intent.putExtra("group_id", groupIndex);
                        intent.putExtra("group_name", name);
                        intent.putExtra("group_participants", participants);
                        startActivity(intent);
                    });
                    buttonRow.addView(btnJoin);

                    // Delete Button
                    Button btnDelete = new Button(this);
                    btnDelete.setText("Delete");
                    btnDelete.setTextColor(android.graphics.Color.WHITE);
                    btnDelete.setBackgroundResource(R.drawable.bg_button_delete);
                    Drawable iconDelete = ContextCompat.getDrawable(this, R.drawable.ic_delete);
                    btnDelete.setCompoundDrawablesWithIntrinsicBounds(iconDelete, null, null, null);
                    btnDelete.setCompoundDrawablePadding((int)(8 * getResources().getDisplayMetrics().density)); // 8dp
                    btnDelete.setLayoutParams(btnParamsDelete);
                    btnDelete.setAllCaps(false);
                    btnDelete.setTextSize(15);
                    btnDelete.setMinWidth(minButtonWidth);
                    btnDelete.setMinHeight(minButtonHeight);
                    btnDelete.setPadding(0, (int)(8 * getResources().getDisplayMetrics().density), 0, (int)(8 * getResources().getDisplayMetrics().density));
                    btnDelete.setOnClickListener(v -> {
                        // Remove group from database
                        UserDatabaseHelper db = new UserDatabaseHelper(this);
                        db.getWritableDatabase().delete(UserDatabaseHelper.TABLE_GROUPS, UserDatabaseHelper.COLUMN_GROUP_ID + "=?", new String[]{String.valueOf(groupIndex)});
                        recreate(); // Refresh activity
                    });
                    buttonRow.addView(btnDelete);

                    // Share Button
                    Button btnShare = new Button(this);
                    btnShare.setText("Share");
                    btnShare.setTextColor(android.graphics.Color.WHITE);
                    btnShare.setBackgroundResource(R.drawable.bg_button_share);
                    Drawable iconShare = ContextCompat.getDrawable(this, R.drawable.ic_share);
                    btnShare.setCompoundDrawablesWithIntrinsicBounds(iconShare, null, null, null);
                    btnShare.setCompoundDrawablePadding((int)(8 * getResources().getDisplayMetrics().density));
                    btnShare.setLayoutParams(btnParamsShare);
                    btnShare.setAllCaps(false);
                    btnShare.setTextSize(15);
                    btnShare.setMinWidth(minButtonWidth);
                    btnShare.setMinHeight(minButtonHeight);
                    btnShare.setPadding(0, (int)(8 * getResources().getDisplayMetrics().density), 0, (int)(8 * getResources().getDisplayMetrics().density));
                    btnShare.setOnClickListener(v -> {
                        String shareText = "Join my walking group!\n" +
                                "Group: " + name + "\n" +
                                "Route: " + route + "\n" +
                                "Time: " + time + "\n" +
                                "Participants: " + participants;
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                        startActivity(Intent.createChooser(shareIntent, "Share Group via"));
                    });
                    buttonRow.addView(btnShare);

                    cardContent.addView(buttonRow);
                    card.addView(cardContent);

                    // Make the card clickable only for members
                    card.setOnClickListener(v -> {
                        String username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
                        UserDatabaseHelper db = new UserDatabaseHelper(this);
                        if (db.isGroupMember(groupIndex, username)) {
                            Intent intent = new Intent(this, WalkingGroupActivity.class);
                            intent.putExtra("group_id", groupIndex);
                            intent.putExtra("group_name", name);
                            intent.putExtra("group_participants", participants);
                            startActivity(intent);
                        }
                        // Optionally, else show a message: "Join the group first!"
                    });

                    groupsContainer.addView(card);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
