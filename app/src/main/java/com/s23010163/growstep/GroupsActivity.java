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
            android.content.SharedPreferences prefs = getSharedPreferences("groups_prefs", MODE_PRIVATE);
            String groupsJson = prefs.getString("groups_list", "[]");
            JSONArray groupsArray = new JSONArray(groupsJson);
            for (int i = 0; i < groupsArray.length(); i++) {
                JSONObject group = groupsArray.getJSONObject(i);
                final int groupIndex = i;
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
                tvName.setText(group.optString("name", "Group"));
                tvName.setTextColor(android.graphics.Color.WHITE);
                tvName.setTextSize(18f);
                tvName.setTypeface(null, android.graphics.Typeface.BOLD);
                cardContent.addView(tvName);

                TextView tvRoute = new TextView(this);
                tvRoute.setText(group.optString("route", "") + " • " + group.optInt("participants", 0) + " people");
                tvRoute.setTextColor(android.graphics.Color.WHITE);
                tvRoute.setTextSize(14f);
                cardContent.addView(tvRoute);

                TextView tvTime = new TextView(this);
                tvTime.setText("Starts at " + group.optString("time", ""));
                tvTime.setTextColor(android.graphics.Color.WHITE);
                tvTime.setTextSize(14f);
                tvTime.setPadding(0, 8, 0, 0);
                cardContent.addView(tvTime);

                // Button row
                LinearLayout buttonRow = new LinearLayout(this);
                buttonRow.setOrientation(LinearLayout.HORIZONTAL);
                buttonRow.setPadding(0, 16, 0, 0);

                // Responsive LayoutParams for equal width, with spacing
                int spacingPx = (int) (8 * getResources().getDisplayMetrics().density); // 8dp
                LinearLayout.LayoutParams btnParamsDelete = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                btnParamsDelete.setMargins(0, 0, spacingPx, 0);
                LinearLayout.LayoutParams btnParamsEdit = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                btnParamsEdit.setMargins(0, 0, spacingPx, 0);
                LinearLayout.LayoutParams btnParamsShare = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                btnParamsShare.setMargins(0, 0, 0, 0);

                int minButtonWidth = (int) (64 * getResources().getDisplayMetrics().density); // 64dp
                int minButtonHeight = (int) (48 * getResources().getDisplayMetrics().density); // 48dp

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
                    // Remove group from SharedPreferences
                    try {
                        JSONArray arr = new JSONArray(prefs.getString("groups_list", "[]"));
                        JSONArray newArr = new JSONArray();
                        for (int j = 0; j < arr.length(); j++) {
                            if (j != groupIndex) newArr.put(arr.getJSONObject(j));
                        }
                        prefs.edit().putString("groups_list", newArr.toString()).apply();
                        recreate(); // Refresh activity
                    } catch (Exception e) { e.printStackTrace(); }
                });
                buttonRow.addView(btnDelete);

                // Edit Button
                Button btnEdit = new Button(this);
                btnEdit.setText("Edit");
                btnEdit.setTextColor(android.graphics.Color.WHITE);
                btnEdit.setBackgroundResource(R.drawable.bg_button_edit);
                Drawable iconEdit = ContextCompat.getDrawable(this, R.drawable.ic_edit);
                btnEdit.setCompoundDrawablesWithIntrinsicBounds(iconEdit, null, null, null);
                btnEdit.setCompoundDrawablePadding((int)(8 * getResources().getDisplayMetrics().density));
                btnEdit.setLayoutParams(btnParamsEdit);
                btnEdit.setAllCaps(false);
                btnEdit.setTextSize(15);
                btnEdit.setMinWidth(minButtonWidth);
                btnEdit.setMinHeight(minButtonHeight);
                btnEdit.setPadding(0, (int)(8 * getResources().getDisplayMetrics().density), 0, (int)(8 * getResources().getDisplayMetrics().density));
                btnEdit.setOnClickListener(v -> {
                    // Show dialog to edit group
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Edit Group");
                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(32, 32, 32, 32);
                    final EditText inputName = new EditText(this);
                    inputName.setHint("Group Name");
                    inputName.setText(group.optString("name", ""));
                    layout.addView(inputName);
                    final EditText inputRoute = new EditText(this);
                    inputRoute.setHint("Route");
                    inputRoute.setText(group.optString("route", ""));
                    layout.addView(inputRoute);
                    final EditText inputTime = new EditText(this);
                    inputTime.setHint("Time");
                    inputTime.setText(group.optString("time", ""));
                    layout.addView(inputTime);
                    final EditText inputParticipants = new EditText(this);
                    inputParticipants.setHint("Participants");
                    inputParticipants.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputParticipants.setText(String.valueOf(group.optInt("participants", 0)));
                    layout.addView(inputParticipants);
                    builder.setView(layout);
                    builder.setPositiveButton("Save", (dialog, which) -> {
                        try {
                            JSONArray arr = new JSONArray(prefs.getString("groups_list", "[]"));
                            JSONObject edited = arr.getJSONObject(groupIndex);
                            edited.put("name", inputName.getText().toString().trim());
                            edited.put("route", inputRoute.getText().toString().trim());
                            edited.put("time", inputTime.getText().toString().trim());
                            edited.put("participants", Integer.parseInt(inputParticipants.getText().toString().trim()));
                            arr.put(groupIndex, edited);
                            prefs.edit().putString("groups_list", arr.toString()).apply();
                            recreate();
                        } catch (Exception e) { e.printStackTrace(); }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                });
                buttonRow.addView(btnEdit);

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
                            "Group: " + group.optString("name", "") + "\n" +
                            "Route: " + group.optString("route", "") + "\n" +
                            "Time: " + group.optString("time", "") + "\n" +
                            "Participants: " + group.optInt("participants", 0);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(shareIntent, "Share Group via"));
                });
                buttonRow.addView(btnShare);

                cardContent.addView(buttonRow);
                card.addView(cardContent);
                groupsContainer.addView(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
