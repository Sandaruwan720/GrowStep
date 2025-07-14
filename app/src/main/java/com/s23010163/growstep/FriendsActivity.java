package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

public class FriendsActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserLevel;
    private TextView numberSteps, numberDistance, numberGroupWalks, numberCalories;

    private TextView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friends);

        // Profile info
        tvUserName = findViewById(R.id.tvUserName);
        tvUserLevel = findViewById(R.id.tvUserLevel);
        numberSteps = findViewById(R.id.NupberSteps);
        numberDistance = findViewById(R.id.Nupberdistance);
        numberGroupWalks = findViewById(R.id.NupberGroupsWalked);
        numberCalories = findViewById(R.id.NupberCalories);
        profileIcon = findViewById(R.id.profileIcon);

        // Get full name from SharedPreferences
        String fullName = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("full_name", "");
        if (!fullName.isEmpty()) {
            tvUserName.setText(fullName);
            // Set initials
            String[] parts = fullName.trim().split("\\s+");
            String initials = "";
            if (parts.length > 0) initials += parts[0].substring(0, 1).toUpperCase();
            if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
            profileIcon.setText(initials);
        }

        // Update stats with today's data from SharedPreferences
        int todaySteps = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("today_steps", 0);
        float todayDistance = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("today_distance", 0f);
        float todayCalories = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("today_calories", 0f);
        numberSteps.setText(String.valueOf(todaySteps));
        numberDistance.setText(String.format("%.2f km", todayDistance));
        numberCalories.setText(String.valueOf(Math.round(todayCalories)));

        // Set static values
        tvUserLevel.setText("Level 8 Walker • 2,450 points");
        numberGroupWalks.setText("12");

        // Add dummy friends
        LinearLayout friendsList = findViewById(R.id.friendsList);
        addDummyFriend(friendsList, "Alice Pro", "Level 12", 78500, true);
        addDummyFriend(friendsList, "Bob Walker", "Level 10", 65400, false);
        addDummyFriend(friendsList, "Charlie Explorer", "Level 9", 60200, false);
        addDummyFriend(friendsList, "Diana Fastfeet", "Level 11", 72000, false);
    }

    private void addDummyFriend(LinearLayout parent, String name, String level, int steps, boolean highlight) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setPadding(0, 8, 0, 8);
        if (highlight) {
            row.setBackgroundColor(Color.parseColor("#E6D6FF"));
        }

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextColor(Color.parseColor("#4B0082"));
        tvName.setTextSize(15f);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));

        TextView tvLevel = new TextView(this);
        tvLevel.setText(level);
        tvLevel.setTextColor(Color.parseColor("#6C63FF"));
        tvLevel.setTextSize(14f);
        tvLevel.setGravity(Gravity.CENTER);
        tvLevel.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvSteps = new TextView(this);
        tvSteps.setText(String.format("%d steps", steps));
        tvSteps.setTextColor(Color.parseColor("#333333"));
        tvSteps.setTextSize(14f);
        tvSteps.setGravity(Gravity.END);
        tvSteps.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));

        row.addView(tvName);
        row.addView(tvLevel);
        row.addView(tvSteps);
        parent.addView(row);
    }
}