package com.s23010163.growstep;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StartWalkingActivity extends AppCompatActivity {

    private ProgressBar stepsCircle;
    private TextView tvStepsCount;
    private final int maxSteps = 10000;
    private final int currentSteps = 7250;

    private TextView stepsCount, distance, duration, calories;
    private Button pauseButton, finishButton, seeMapButton;
    private TextView startButton; // this is textView3 in XML

    private boolean isStarted = false;
    private boolean isPaused = false;

    LinearLayout navHome, navGroups, navChallenges, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_walking);

        // Step Progress View
        stepsCircle = findViewById(R.id.stepsCircle);
        tvStepsCount = findViewById(R.id.tvStepsCount);
        stepsCircle.setMax(maxSteps);
        animateSteps();

        // Stats
        stepsCount = findViewById(R.id.stepsCount);
        distance = findViewById(R.id.distance);
        duration = findViewById(R.id.duration);
        calories = findViewById(R.id.calories);

        // Buttons
        pauseButton = findViewById(R.id.pauseButton);
        finishButton = findViewById(R.id.finishButton);
        seeMapButton = findViewById(R.id.btnSeeMap);
        startButton = findViewById(R.id.textView3); // Start TextView as button

        // Bottom Nav
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);

        startButton.setOnClickListener(v -> {
            if (!isStarted) {
                isStarted = true;
                isPaused = false;
                Toast.makeText(this, "Walking started!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Already started", Toast.LENGTH_SHORT).show();
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isStarted && !isPaused) {
                isPaused = true;
                Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
            } else if (!isStarted) {
                Toast.makeText(this, "Start walk first!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Already paused", Toast.LENGTH_SHORT).show();
            }
        });

        finishButton.setOnClickListener(v -> {
            if (isStarted) {
                isStarted = false;
                isPaused = false;
                Toast.makeText(this, "Walk finished", Toast.LENGTH_SHORT).show();
                stepsCount.setText("0");
                distance.setText("Distance\n0.0 km");
                duration.setText("Duration\n00:00:00");
                calories.setText("Calories\n0");
                stepsCircle.setProgress(0);
                tvStepsCount.setText("0");
            } else {
                Toast.makeText(this, "Start walk first!", Toast.LENGTH_SHORT).show();
            }
        });

        seeMapButton.setOnClickListener(v ->
                Toast.makeText(this, "Map View coming soon!", Toast.LENGTH_SHORT).show());

        navHome.setOnClickListener(v ->
                Toast.makeText(this, "You're already on Home", Toast.LENGTH_SHORT).show());

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(StartWalkingActivity.this, GroupsActivity.class);
            startActivity(intent);
        });

        navChallenges.setOnClickListener(v ->
                Toast.makeText(this, "Challenges Clicked", Toast.LENGTH_SHORT).show());

        navProfile.setOnClickListener(v ->
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show());
    }

    private void animateSteps() {
        ObjectAnimator animator = ObjectAnimator.ofInt(stepsCircle, "progress", 0, currentSteps);
        animator.setDuration(1500);
        animator.start();

        new Handler().postDelayed(() -> {
            tvStepsCount.setText(String.format("%,d", currentSteps));
            stepsCount.setText(String.format("%,d", currentSteps));
        }, 1500);
    }
}
