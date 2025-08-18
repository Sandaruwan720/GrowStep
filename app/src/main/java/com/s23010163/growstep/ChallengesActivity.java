package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChallengesActivity extends AppCompatActivity {

    private LinearLayout navHome, navGroups, navChallenges, navProfile;
    private TextView startButton, tvTotalPoints;
    private BroadcastReceiver challengeUpdateReceiver;
    
    // Challenge IDs for tracking completion
    private static final String CHALLENGE_WEEKLY_STEPS = "weekly_steps";
    private static final String CHALLENGE_SOCIAL_WALKER = "social_walker";
    private static final String CHALLENGE_SOCIAL_BUTTERFLY = "social_butterfly";
    
    // Challenge rewards
    private static final int REWARD_WEEKLY_STEPS = 500;
    private static final int REWARD_SOCIAL_WALKER = 750;
    private static final int REWARD_SOCIAL_BUTTERFLY = 300;
    
    // Weekly challenge tracking
    private static final String WEEKLY_CHALLENGE_START = "weekly_challenge_start";

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
        
        // Check if Social Walker challenge is completed
        checkAndAwardChallenge(CHALLENGE_SOCIAL_WALKER, groupWalks >= groupWalkGoal, REWARD_SOCIAL_WALKER, false);
        
        int groupsJoined = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("groups_joined", 0);
        int butterflyGoal = 3;
        int butterflyPercent = Math.min(100, (int) (groupsJoined * 100.0 / butterflyGoal));
        progressExplorerBadge.setProgress(butterflyPercent);
        textExplorerBadgeProgress.setText(String.format("%d / %d groups joined", groupsJoined, butterflyGoal));
        
        // Check if Social Butterfly challenge is completed
        checkAndAwardChallenge(CHALLENGE_SOCIAL_BUTTERFLY, groupsJoined >= butterflyGoal, REWARD_SOCIAL_BUTTERFLY, false);
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
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        
        // Update total points display
        tvTotalPoints.setText("Total Points: " + getCurrentTotalPoints());
        
        // Add long press on title to reset challenges (for testing)
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setOnLongClickListener(v -> {
            resetAllChallenges();
            return true;
        });

        // --- Challenge Progress Logic ---
        // Weekly Step Goal
        ProgressBar progressWeeklyStep = findViewById(R.id.progressWeeklyStep);
        TextView textWeeklyStepProgress = findViewById(R.id.textWeeklyStepProgress);
        int weeklyGoal = 50000;
        int weeklySteps = 0;
        // Calculate weekly steps from session_history
        String historyJson = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("session_history", "[]");
        try {
            JSONArray history = new JSONArray(historyJson);
            long now = System.currentTimeMillis();
            long weekAgo = now - 7L * 24 * 60 * 60 * 1000;
            for (int i = 0; i < history.length(); i++) {
                JSONObject session = history.getJSONObject(i);
                int steps = session.optInt("steps", 0);
                // If you store a timestamp, use it here. For now, sum all.
                weeklySteps += steps;
            }
        } catch (Exception e) { e.printStackTrace(); }
        int weeklyPercent = Math.min(100, (int) (weeklySteps * 100.0 / weeklyGoal));
        progressWeeklyStep.setProgress(weeklyPercent);
        textWeeklyStepProgress.setText(String.format("%d / %d steps", weeklySteps, weeklyGoal));
        
        // Check if Weekly Steps challenge is completed
        checkAndAwardChallenge(CHALLENGE_WEEKLY_STEPS, weeklySteps >= weeklyGoal, REWARD_WEEKLY_STEPS, true);

        // Social Walker (use real value from SharedPreferences)
        ProgressBar progressSocialWalker = findViewById(R.id.progressSocialWalker);
        TextView textSocialWalkerProgress = findViewById(R.id.textSocialWalkerProgress);
        int groupWalks = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("group_walks", 0);
        int groupWalkGoal = 5;
        int groupWalkPercent = Math.min(100, (int) (groupWalks * 100.0 / groupWalkGoal));
        progressSocialWalker.setProgress(groupWalkPercent);
        textSocialWalkerProgress.setText(String.format("%d / %d group walks", groupWalks, groupWalkGoal));

        // Explorer Badge (use real value from SharedPreferences)
        ProgressBar progressExplorerBadge = findViewById(R.id.progressExplorerBadge);
        TextView textExplorerBadgeProgress = findViewById(R.id.textExplorerBadgeProgress);
        int groupsJoined = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("groups_joined", 0);
        int routeGoal = 3;
        int routePercent = Math.min(100, (int) (groupsJoined * 100.0 / routeGoal));
        progressExplorerBadge.setProgress(routePercent);
        textExplorerBadgeProgress.setText(String.format("%d / %d groups joined", groupsJoined, routeGoal));

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

    /**
     * Check if a challenge is completed and award points if it's newly completed
     * @param challengeId Unique identifier for the challenge
     * @param isCompleted Whether the challenge is currently completed
     * @param rewardPoints Points to award for completing the challenge
     */
    private void checkAndAwardChallenge(String challengeId, boolean isCompleted, int rewardPoints) {
        if (!isCompleted) return;
        
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String completedChallenges = prefs.getString("completed_challenges", "[]");
        
        try {
            JSONArray completed = new JSONArray(completedChallenges);
            boolean alreadyCompleted = false;
            
            // Check if challenge was already completed
            for (int i = 0; i < completed.length(); i++) {
                if (completed.getString(i).equals(challengeId)) {
                    alreadyCompleted = true;
                    break;
                }
            }
            
            // If newly completed, award points
            if (!alreadyCompleted) {
                // Add to completed challenges list
                completed.put(challengeId);
                prefs.edit().putString("completed_challenges", completed.toString()).apply();
                
                // Award points
                int currentPoints = prefs.getInt("pts", 0);
                int newTotalPoints = currentPoints + rewardPoints;
                prefs.edit().putInt("pts", newTotalPoints).apply();
                
                // Show success message
                String challengeName = getChallengeName(challengeId);
                Toast.makeText(this, "🎉 Challenge Completed: " + challengeName + "! +" + rewardPoints + " points", Toast.LENGTH_LONG).show();
                
                // Broadcast points update
                Intent intentUpdate = new Intent("com.growstep.CHALLENGE_PROGRESS_UPDATED");
                sendBroadcast(intentUpdate);
                
                // Update total points display
                if (tvTotalPoints != null) {
                    tvTotalPoints.setText("Total Points: " + getCurrentTotalPoints());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Check if a challenge is completed and award points if it's newly completed
     * @param challengeId Unique identifier for the challenge
     * @param isCompleted Whether the challenge is currently completed
     * @param rewardPoints Points to award for completing the challenge
     * @param isWeekly Whether the challenge is a weekly challenge
     */
    private void checkAndAwardChallenge(String challengeId, boolean isCompleted, int rewardPoints, boolean isWeekly) {
        if (!isCompleted) return;
        
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String completedChallenges = prefs.getString("completed_challenges", "[]");
        
        try {
            JSONArray completed = new JSONArray(completedChallenges);
            boolean alreadyCompleted = false;
            
            if (isWeekly) {
                // For weekly challenges, check if completed this week
                long currentWeek = System.currentTimeMillis() / (7L * 24 * 60 * 60 * 1000); // Weeks since epoch
                long lastCompletedWeek = prefs.getLong("weekly_challenge_" + challengeId, 0);
                
                if (lastCompletedWeek == currentWeek) {
                    alreadyCompleted = true;
                } else {
                    // Mark as completed for this week
                    prefs.edit().putLong("weekly_challenge_" + challengeId, currentWeek).apply();
                }
            } else {
                // For non-weekly challenges, check if ever completed
                for (int i = 0; i < completed.length(); i++) {
                    if (completed.getString(i).equals(challengeId)) {
                        alreadyCompleted = true;
                        break;
                    }
                }
                
                // If newly completed, add to completed challenges list
                if (!alreadyCompleted) {
                    completed.put(challengeId);
                    prefs.edit().putString("completed_challenges", completed.toString()).apply();
                }
            }
            
            // If newly completed, award points
            if (!alreadyCompleted) {
                // Award points
                int currentPoints = prefs.getInt("pts", 0);
                int newTotalPoints = currentPoints + rewardPoints;
                prefs.edit().putInt("pts", newTotalPoints).apply();
                
                // Show success message
                String challengeName = getChallengeName(challengeId);
                String message = isWeekly ? 
                    "🎉 Weekly Challenge Completed: " + challengeName + "! +" + rewardPoints + " points" :
                    "🎉 Challenge Completed: " + challengeName + "! +" + rewardPoints + " points";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                
                // Broadcast points update
                Intent intentUpdate = new Intent("com.growstep.CHALLENGE_PROGRESS_UPDATED");
                sendBroadcast(intentUpdate);
                
                // Update total points display
                if (tvTotalPoints != null) {
                    tvTotalPoints.setText("Total Points: " + getCurrentTotalPoints());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the display name for a challenge
     * @param challengeId Challenge identifier
     * @return Display name
     */
    private String getChallengeName(String challengeId) {
        switch (challengeId) {
            case CHALLENGE_WEEKLY_STEPS:
                return "Weekly Step Goal";
            case CHALLENGE_SOCIAL_WALKER:
                return "Social Walker";
            case CHALLENGE_SOCIAL_BUTTERFLY:
                return "Social Butterfly";
            default:
                return "Challenge";
        }
    }
    
    /**
     * Reset all challenges (for testing purposes)
     */
    private void resetAllChallenges() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit()
            .putString("completed_challenges", "[]")
            .putLong("weekly_challenge_" + CHALLENGE_WEEKLY_STEPS, 0)
            .putLong("weekly_challenge_" + CHALLENGE_SOCIAL_WALKER, 0)
            .putLong("weekly_challenge_" + CHALLENGE_SOCIAL_BUTTERFLY, 0)
            .apply();
        
        Toast.makeText(this, "All challenges reset for testing", Toast.LENGTH_SHORT).show();
        updateChallengeProgress();
    }
    
    /**
     * Get current total points from SharedPreferences
     * @return Current total points
     */
    private int getCurrentTotalPoints() {
        return getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("pts", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateChallengeProgress();
        tvTotalPoints.setText("Total Points: " + getCurrentTotalPoints());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (challengeUpdateReceiver != null) {
            unregisterReceiver(challengeUpdateReceiver);
        }
    }
}
