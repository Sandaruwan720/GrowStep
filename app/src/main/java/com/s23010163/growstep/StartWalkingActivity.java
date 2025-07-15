package com.s23010163.growstep;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

public class StartWalkingActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;

    private boolean isStarted = false;
    private boolean isPaused = false;

    private int initialSteps = 0;
    private int totalSteps = 0;

    private long startTime = 0;
    private long pausedTime = 0;
    private long pausedDuration = 0;

    private ProgressBar stepsCircle;
    private TextView tvStepsCount, stepsCount, distance, duration, calories;
    private Button pauseButton, finishButton, seeMapButton;
    private TextView startButton;

    private final int MAX_STEPS = 10000;
    private final Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    // Bottom navigation
    private LinearLayout navHome, navGroups, navChallenges, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_walking);

        // Bind views
        stepsCircle = findViewById(R.id.stepsCircle);
        tvStepsCount = findViewById(R.id.tvStepsCount);
        stepsCount = findViewById(R.id.stepsCount);
        distance = findViewById(R.id.distance);
        duration = findViewById(R.id.duration);
        calories = findViewById(R.id.calories);
        pauseButton = findViewById(R.id.pauseButton);
        finishButton = findViewById(R.id.finishButton);
        seeMapButton = findViewById(R.id.btnSeeMap);
        startButton = findViewById(R.id.startLabel);
        stepsCircle.setMax(MAX_STEPS);

        // Navigation setup
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupsActivity.class);
            startActivity(intent);
            finish();
        });

        navChallenges.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChallengesActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            Toast.makeText(this, "Step Counter sensor not available", Toast.LENGTH_LONG).show();
        }

        startButton.setOnClickListener(v -> {
            if (!isStarted) {
                isStarted = true;
                isPaused = false;
                initialSteps = 0;
                totalSteps = 0;
                pausedDuration = 0;
                startTime = SystemClock.elapsedRealtime();
                pausedTime = 0;

                resetUI();
                startTimer();

                Toast.makeText(this, "Walk started", Toast.LENGTH_SHORT).show();
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isStarted && !isPaused) {
                isPaused = true;
                pausedTime = SystemClock.elapsedRealtime();
                stopTimer();
                Toast.makeText(this, "Walk paused", Toast.LENGTH_SHORT).show();
            }
        });

        finishButton.setOnClickListener(v -> {
            if (isStarted) {
                isStarted = false;
                isPaused = false;
                stopTimer();
                updateStats(); // final update
                // Save today's stats to SharedPreferences
                float distKm = totalSteps * 0.0008f;
                float cal = totalSteps * 0.04f;
                getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .edit()
                    .putInt("today_steps", totalSteps)
                    .putFloat("today_distance", distKm)
                    .putFloat("today_calories", cal)
                    .apply();
                // Save session to history
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                try {
                    JSONObject session = new JSONObject();
                    session.put("steps", totalSteps);
                    session.put("distance", distKm);
                    session.put("calories", cal);
                    String historyJson = prefs.getString("session_history", "[]");
                    JSONArray history = new JSONArray(historyJson);
                    history.put(session);
                    prefs.edit().putString("session_history", history.toString()).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Walk finished", Toast.LENGTH_SHORT).show();
                int groupWalks =  prefs.getInt("group_walks", 0) + 1;
                prefs.edit().putInt("group_walks", groupWalks).apply();
                // Notify challenge progress update
                android.content.Intent intentUpdate = new android.content.Intent("com.growstep.CHALLENGE_PROGRESS_UPDATED");
                sendBroadcast(intentUpdate);
            }
        });

        seeMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeeMapActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stepSensor != null) {
            sensorManager.unregisterListener(this);
        }
        stopTimer(); // stop when paused
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isStarted && !isPaused) {
            if (initialSteps == 0) {
                initialSteps = (int) event.values[0];
            }
            totalSteps = (int) event.values[0] - initialSteps;

            tvStepsCount.setText(String.valueOf(totalSteps));
            stepsCount.setText(String.valueOf(totalSteps));
            stepsCircle.setProgress(totalSteps);

            updateStats();
        }
    }

    private void updateStats() {
        float distKm = totalSteps * 0.0008f;
        float cal = totalSteps * 0.04f;

        long now = SystemClock.elapsedRealtime();
        long effectiveElapsed = now - startTime - pausedDuration;
        if (isPaused) {
            effectiveElapsed = pausedTime - startTime - pausedDuration;
            pausedDuration += now - pausedTime;
        }

        int hrs = (int) (effectiveElapsed / 3600000);
        int mins = (int) ((effectiveElapsed % 3600000) / 60000);
        int secs = (int) ((effectiveElapsed % 60000) / 1000);

        distance.setText(String.format("Distance\n%.2f km", distKm));
        calories.setText(String.format("Calories\n%d", Math.round(cal)));
        duration.setText(String.format("Duration\n%02d:%02d:%02d", hrs, mins, secs));
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isStarted && !isPaused) {
                    updateStats();
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resetUI() {
        tvStepsCount.setText("0");
        stepsCount.setText("0");
        stepsCircle.setProgress(0);
        distance.setText("Distance\n0.00 km");
        calories.setText("Calories\n0");
        duration.setText("Duration\n00:00:00");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
