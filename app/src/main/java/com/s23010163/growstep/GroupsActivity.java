package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GroupsActivity extends AppCompatActivity {

    LinearLayout navHome, navGroups, navChallenges, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(GroupsActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        navGroups.setOnClickListener(v -> Toast.makeText(this, "You're already on Groups", Toast.LENGTH_SHORT).show());

        navChallenges.setOnClickListener(v -> Toast.makeText(this, "Challenges clicked", Toast.LENGTH_SHORT).show());
        navProfile.setOnClickListener(v -> Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show());

        TextView startLabel = findViewById(R.id.startLabel);
        startLabel.setOnClickListener(v -> Toast.makeText(this, "Start clicked", Toast.LENGTH_SHORT).show());
    }
}
