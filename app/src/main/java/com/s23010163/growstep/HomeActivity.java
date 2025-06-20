package com.s23010163.growstep;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        stepsCircle = findViewById(R.id.stepsCircle);
        tvStepsCount = findViewById(R.id.tvStepsCount);
        stepsCircle.setMax(maxSteps);
        animateSteps();

        // Setup navigation
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);

        navHome.setOnClickListener(v -> Toast.makeText(this, "You're already on Home", Toast.LENGTH_SHORT).show());

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GroupsActivity.class);
            startActivity(intent);
        });

        navChallenges.setOnClickListener(v -> Toast.makeText(this, "Challenges Clicked", Toast.LENGTH_SHORT).show());

        navProfile.setOnClickListener(v -> Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show());
    }

    private void animateSteps() {
        ObjectAnimator animator = ObjectAnimator.ofInt(stepsCircle, "progress", 0, currentSteps);
        animator.setDuration(1500);
        animator.start();

        new Handler().postDelayed(() -> tvStepsCount.setText(String.format("%,d", currentSteps)), 1500);
    }
}
