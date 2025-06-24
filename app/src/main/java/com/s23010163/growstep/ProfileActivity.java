package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserLevel;
    private TextView numberSteps, numberDistance, numberGroupWalks, numberCalories;
    private LinearLayout navHome, navGroups, navChallenges, navProfile;
    private Button btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Profile header
        tvUserName = findViewById(R.id.tvUserName);
        tvUserLevel = findViewById(R.id.tvUserLevel);

        // Weekly stats values
        numberSteps = findViewById(R.id.NupberSteps);
        numberDistance = findViewById(R.id.Nupberdistance);
        numberGroupWalks = findViewById(R.id.NupberGroupsWalked);
        numberCalories = findViewById(R.id.NupberCalories);

        // Set profile values
        tvUserName.setText("Tharuka Sandaruwan");
        tvUserLevel.setText("Level 8 Walker • 2,450 points");
        numberSteps.setText("56,847");
        numberDistance.setText("42.3 km");
        numberGroupWalks.setText("12");
        numberCalories.setText("2,847");

        // Bottom nav
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);

        navHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        navGroups.setOnClickListener(v -> startActivity(new Intent(this, GroupsActivity.class)));

        navProfile.setOnClickListener(v -> {
            // Already on profile
        });

        // History button navigation
        btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }
}
