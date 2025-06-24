package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChallengesActivity extends AppCompatActivity {

    private LinearLayout navHome, navGroups, navChallenges, navProfile;
    private TextView startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Initialize navigation bar views
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);
        startButton = findViewById(R.id.startLabel);


        navHome.setOnClickListener(v -> {
            startActivity(new Intent(ChallengesActivity.this, HomeActivity.class));
            finish();
        });

        navGroups.setOnClickListener(v -> {
            startActivity(new Intent(ChallengesActivity.this, GroupsActivity.class));
            finish();
        });

        navChallenges.setOnClickListener(v -> {

        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(ChallengesActivity.this, ProfileActivity.class));
            finish();
        });

       
        startButton.setOnClickListener(v -> {
            startActivity(new Intent(ChallengesActivity.this, StartWalkingActivity.class));
        });
    }
}
