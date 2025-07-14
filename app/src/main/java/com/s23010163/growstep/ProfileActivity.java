package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserLevel;
    private TextView numberSteps, numberDistance, numberGroupWalks, numberCalories;
    private LinearLayout navHome, navGroups, navChallenges, navProfile;
    private Button btnHistory;
    private Button btnRewards;
    private Button btnFriends;
    private Button btnAchievement;
    private TextView startLabel;
    private TextView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        // Bottom Navigation bar
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, GroupsActivity.class);
            startActivity(intent);
            finish();
        });

        navChallenges.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChallengesActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            // Already on profile
        });

        // Start Button
        startLabel = findViewById(R.id.startLabel);
        startLabel.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, StartWalkingActivity.class);
            startActivity(intent);
        });

        // History Button
        btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
        btnRewards = findViewById(R.id.btnRewards);
        btnRewards.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, RewardsActivity.class);
            startActivity(intent);
        });
        btnFriends = findViewById(R.id.btnFriends);
        btnFriends.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
            startActivity(intent);
        });
        btnAchievement= findViewById(R.id.btnAchievement);
        btnAchievement.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AchievementsActivity.class);
            startActivity(intent);
        });
    }
}
