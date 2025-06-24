package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}
