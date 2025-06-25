package com.s23010163.growstep;



import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;

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

        // Show charts for last 10 sessions
        LineChart stepsChart = findViewById(R.id.stepsChart);
        LineChart caloriesChart = findViewById(R.id.caloriesChart);
        ArrayList<Entry> stepEntries = new ArrayList<>();
        ArrayList<Entry> calorieEntries = new ArrayList<>();
        try {
            JSONArray history = new JSONArray(historyJson);
            int startIdx = Math.max(0, history.length() - 10);
            for (int i = startIdx; i < history.length(); i++) {
                JSONObject session = history.getJSONObject(i);
                int steps = session.optInt("steps", 0);
                float calories = (float) session.optDouble("calories", 0f);
                stepEntries.add(new Entry(i - startIdx + 1, steps));
                calorieEntries.add(new Entry(i - startIdx + 1, calories));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Steps chart
        LineDataSet stepDataSet = new LineDataSet(stepEntries, "Steps");
        stepDataSet.setColor(0xFF4CAF50);
        stepDataSet.setCircleColor(0xFF4CAF50);
        stepDataSet.setValueTextColor(0xFFFFFFFF);
        stepDataSet.setLineWidth(2f);
        stepDataSet.setCircleRadius(4f);
        stepDataSet.setDrawValues(false);
        LineData stepLineData = new LineData(stepDataSet);
        stepsChart.setData(stepLineData);
        stepsChart.getDescription().setEnabled(false);
        stepsChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        stepsChart.getAxisLeft().setTextColor(0xFFFFFFFF);
        stepsChart.getAxisRight().setEnabled(false);
        stepsChart.getXAxis().setTextColor(0xFFFFFFFF);
        stepsChart.getLegend().setTextColor(0xFFFFFFFF);
        stepsChart.invalidate();
        // Calories chart
        LineDataSet calorieDataSet = new LineDataSet(calorieEntries, "Calories");
        calorieDataSet.setColor(0xFFFF9800);
        calorieDataSet.setCircleColor(0xFFFF9800);
        calorieDataSet.setValueTextColor(0xFFFFFFFF);
        calorieDataSet.setLineWidth(2f);
        calorieDataSet.setCircleRadius(4f);
        calorieDataSet.setDrawValues(false);
        LineData calorieLineData = new LineData(calorieDataSet);
        caloriesChart.setData(calorieLineData);
        caloriesChart.getDescription().setEnabled(false);
        caloriesChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        caloriesChart.getAxisLeft().setTextColor(0xFFFFFFFF);
        caloriesChart.getAxisRight().setEnabled(false);
        caloriesChart.getXAxis().setTextColor(0xFFFFFFFF);
        caloriesChart.getLegend().setTextColor(0xFFFFFFFF);
        caloriesChart.invalidate();
    }
}
