package com.s23010163.growstep;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private ProgressBar stepsCircle;
    private TextView tvStepsCount;
    private final int maxSteps = 10000;
    private final int currentSteps = 7250;

    private LinearLayout navHome, navGroups, navChallenges, navProfile;
    private TextView startButtonText, tvGreeting, tvStepsDetail, tvDistance, tvCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Step progress animation setup
        stepsCircle = findViewById(R.id.stepsCircle);
        tvStepsCount = findViewById(R.id.tvStepsCount);
        stepsCircle.setMax(maxSteps);
        animateSteps();

        // Navigation bar buttons
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);
        startButtonText = findViewById(R.id.startLabel); // Start text/button
        tvGreeting = findViewById(R.id.tvGreeting);
        tvStepsDetail = findViewById(R.id.tvStepsDetail);
        tvDistance = findViewById(R.id.tvDistance);
        tvCalories = findViewById(R.id.tvCalories);

        // Set greeting with username from SharedPreferences
        String username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
        if (!username.isEmpty()) {
            tvGreeting.setText("Hello, " + username + "!");
        }

        // Load today's stats from SharedPreferences
        int todaySteps = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("today_steps", 0);
        float todayDistance = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("today_distance", 0f);
        float todayCalories = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("today_calories", 0f);

        // Update UI
        tvStepsCount.setText(String.valueOf(todaySteps));
        stepsCircle.setProgress(todaySteps);
        tvStepsDetail.setText(String.valueOf(todaySteps));
        tvDistance.setText(String.format("%.2f km", todayDistance));
        tvCalories.setText(String.format("%d kcal", Math.round(todayCalories)));

        // Navigation logic
        navHome.setOnClickListener(v -> {
            // Already on Home
        });

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GroupsActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        });

        navChallenges.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChallengesActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Start walking button
        startButtonText.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StartWalkingActivity.class);
            startActivity(intent);
        });
    }

    private void animateSteps() {
        ObjectAnimator animator = ObjectAnimator.ofInt(stepsCircle, "progress", 0, currentSteps);
        animator.setDuration(1500);
        animator.start();

        new Handler().postDelayed(() -> {
            tvStepsCount.setText(String.format("%,d", currentSteps));
        }, 1500);
    }
}
