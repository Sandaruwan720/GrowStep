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

                    TextView tvRoute = new TextView(this);
                    tvRoute.setText(route + " • " + participants + " people");
                    tvRoute.setTextColor(android.graphics.Color.WHITE);
                    tvRoute.setTextSize(14f);
                    cardContent.addView(tvRoute);

                    TextView tvTime = new TextView(this);
                    tvTime.setText("Starts at " + time);
                    tvTime.setTextColor(android.graphics.Color.WHITE);
                    tvTime.setTextSize(14f);
                    tvTime.setPadding(0, 8, 0, 0);
                    cardContent.addView(tvTime);

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
                    btnJoin.setText("Join");
                    btnJoin.setTextColor(android.graphics.Color.WHITE);
                    btnJoin.setBackgroundResource(R.drawable.bg_button_edit); // purple background
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
                        try {
                            UserDatabaseHelper db = new UserDatabaseHelper(this);
                            db.addGroupMember(groupIndex, username);
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
                    groupsContainer.addView(card);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
