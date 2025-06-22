package com.s23010163.growstep;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private ProgressBar stepsCircle;
    private TextView tvStepsCount;
    private final int maxSteps = 10000;
    private final int currentSteps = 7250;

    LinearLayout navHome, navGroups, navChallenges, navProfile;
    TextView startButtonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Step progress
        stepsCircle = findViewById(R.id.stepsCircle);
        tvStepsCount = findViewById(R.id.tvStepsCount);
        stepsCircle.setMax(maxSteps);
        animateSteps();

        // Navigation
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);
        startButtonText = findViewById(R.id.startLabel); // "Start" TextView acts as Start button

        navHome.setOnClickListener(v ->
                Toast.makeText(this, "You're already on Home", Toast.LENGTH_SHORT).show());

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GroupsActivity.class);
            startActivity(intent);
        });

        navChallenges.setOnClickListener(v ->
                Toast.makeText(this, "Challenges Clicked", Toast.LENGTH_SHORT).show());

        navProfile.setOnClickListener(v ->
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show());

        // Start Button Logic
        startButtonText.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StartWalkingActivity.class);
            startActivity(intent);
        });
    }

    private void animateSteps() {
        ObjectAnimator animator = ObjectAnimator.ofInt(stepsCircle, "progress", 0, currentSteps);
        animator.setDuration(1500);
        animator.start();

        new Handler().postDelayed(() ->
                tvStepsCount.setText(String.format("%,d", currentSteps)), 1500);
    }
}
