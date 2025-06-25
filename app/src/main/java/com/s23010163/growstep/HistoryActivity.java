package com.s23010163.growstep;



import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryActivity extends AppCompatActivity {

    String[] titles = {
            "Total Steps", "Total Distance",
            "Total Calories", "Avg Steps",
            "Best Session", "Total Sessions"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Load session history
        String historyJson = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("session_history", "[]");
        int totalSteps = 0;
        float totalDistance = 0f;
        float totalCalories = 0f;
        int bestSession = 0;
        int sessionCount = 0;
        try {
            JSONArray history = new JSONArray(historyJson);
            sessionCount = history.length();
            for (int i = 0; i < history.length(); i++) {
                JSONObject session = history.getJSONObject(i);
                int steps = session.optInt("steps", 0);
                float distance = (float) session.optDouble("distance", 0f);
                float calories = (float) session.optDouble("calories", 0f);
                totalSteps += steps;
                totalDistance += distance;
                totalCalories += calories;
                if (steps > bestSession) bestSession = steps;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int avgSteps = sessionCount > 0 ? totalSteps / sessionCount : 0;

        // Update fitness history stat cards
        TextView tvTotalSteps = findViewById(R.id.tvTotalSteps);
        TextView tvTotalDistance = findViewById(R.id.tvTotalDistance);
        TextView tvTotalCalories = findViewById(R.id.tvTotalCalories);
        TextView tvAvgSteps = findViewById(R.id.tvAvgSteps);
        TextView tvBestSession = findViewById(R.id.tvBestSession);
        TextView tvTotalSessions = findViewById(R.id.tvTotalSessions);
        if (tvTotalSteps != null) tvTotalSteps.setText(String.valueOf(totalSteps));
        if (tvTotalDistance != null) tvTotalDistance.setText(String.format("%.2fKm", totalDistance));
        if (tvTotalCalories != null) tvTotalCalories.setText(String.valueOf(Math.round(totalCalories)));
        if (tvAvgSteps != null) tvAvgSteps.setText(String.valueOf(avgSteps));
        if (tvBestSession != null) tvBestSession.setText(String.valueOf(bestSession));
        if (tvTotalSessions != null) tvTotalSessions.setText(String.valueOf(sessionCount));
        // Show today's step count details at the bottom
        LinearLayout rootLayout = findViewById(R.id.rootHistoryLayout);
        TextView todayTitle = new TextView(this);
        todayTitle.setText("Today's Step Details");
        todayTitle.setTextColor(0xFFFFFFFF);
        todayTitle.setTextSize(20);
        todayTitle.setPadding(0, 32, 0, 8);
        rootLayout.addView(todayTitle);

        TextView todayDetails = new TextView(this);
        String todayDetailsText = "Steps: " + totalSteps + "\nDistance: " + String.format("%.2f km", totalDistance) + "\nCalories: " + Math.round(totalCalories);
        todayDetails.setText(todayDetailsText);
        todayDetails.setTextColor(0xFFFFFFFF);
        todayDetails.setTextSize(16);
        rootLayout.addView(todayDetails);
    }
}
