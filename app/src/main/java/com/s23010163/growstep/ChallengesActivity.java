package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;

public class ChallengesActivity extends AppCompatActivity {

    private LinearLayout navHome, navGroups, navChallenges, navProfile;
    private TextView startButton;
    private BroadcastReceiver challengeUpdateReceiver;

    private void updateChallengeProgress() {
        ProgressBar progressSocialWalker = findViewById(R.id.progressSocialWalker);
        TextView textSocialWalkerProgress = findViewById(R.id.textSocialWalkerProgress);
        ProgressBar progressExplorerBadge = findViewById(R.id.progressExplorerBadge);
        TextView textExplorerBadgeProgress = findViewById(R.id.textExplorerBadgeProgress);
        int groupWalks = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("group_walks", 0);
        int groupWalkGoal = 5;
        int groupWalkPercent = Math.min(100, (int) (groupWalks * 100.0 / groupWalkGoal));
        progressSocialWalker.setProgress(groupWalkPercent);
        textSocialWalkerProgress.setText(String.format("%d / %d group walks", groupWalks, groupWalkGoal));
        int groupsJoined = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("groups_joined", 0);
        int butterflyGoal = 3;
        int butterflyPercent = Math.min(100, (int) (groupsJoined * 100.0 / butterflyGoal));
        progressExplorerBadge.setProgress(butterflyPercent);
        textExplorerBadgeProgress.setText(String.format("%d / %d groups joined", groupsJoined, butterflyGoal));
    }

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

        // --- Challenge Progress Logic ---
        // Weekly Step Goal
        ProgressBar progressWeeklyStep = findViewById(R.id.progressWeeklyStep);
        TextView textWeeklyStepProgress = findViewById(R.id.textWeeklyStepProgress);
        int weeklyGoal = 50000;
        int weeklySteps = 0;
        // Calculate weekly steps from session_history
        String historyJson = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("session_history", "[]");
        try {
            org.json.JSONArray history = new org.json.JSONArray(historyJson);
            long now = System.currentTimeMillis();
            long weekAgo = now - 7L * 24 * 60 * 60 * 1000;
            for (int i = 0; i < history.length(); i++) {
                org.json.JSONObject session = history.getJSONObject(i);
                int steps = session.optInt("steps", 0);
                // If you store a timestamp, use it here. For now, sum all.
                weeklySteps += steps;
            }
        } catch (Exception e) { e.printStackTrace(); }
        int weeklyPercent = Math.min(100, (int) (weeklySteps * 100.0 / weeklyGoal));
        progressWeeklyStep.setProgress(weeklyPercent);
        textWeeklyStepProgress.setText(String.format("%d / %d steps", weeklySteps, weeklyGoal));

        // Social Walker (dummy: 2/5 group walks)
        ProgressBar progressSocialWalker = findViewById(R.id.progressSocialWalker);
        TextView textSocialWalkerProgress = findViewById(R.id.textSocialWalkerProgress);
        int groupWalks = 2; // TODO: Replace with real value
        int groupWalkGoal = 5;
        int groupWalkPercent = Math.min(100, (int) (groupWalks * 100.0 / groupWalkGoal));
        progressSocialWalker.setProgress(groupWalkPercent);
        textSocialWalkerProgress.setText(String.format("%d / %d group walks", groupWalks, groupWalkGoal));

        // Explorer Badge (dummy: 1/3 routes)
        ProgressBar progressExplorerBadge = findViewById(R.id.progressExplorerBadge);
        TextView textExplorerBadgeProgress = findViewById(R.id.textExplorerBadgeProgress);
        int routesDiscovered = 1; // TODO: Replace with real value
        int routeGoal = 3;
        int routePercent = Math.min(100, (int) (routesDiscovered * 100.0 / routeGoal));
        progressExplorerBadge.setProgress(routePercent);
        textExplorerBadgeProgress.setText(String.format("%d / %d routes", routesDiscovered, routeGoal));

        updateChallengeProgress();
        // Listen for challenge updates
        challengeUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateChallengeProgress();
            }
        };
        registerReceiver(challengeUpdateReceiver, new IntentFilter("com.growstep.CHALLENGE_PROGRESS_UPDATED"));


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

    @Override
    protected void onResume() {
        super.onResume();
        updateChallengeProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (challengeUpdateReceiver != null) {
            unregisterReceiver(challengeUpdateReceiver);
        }
    }
}
