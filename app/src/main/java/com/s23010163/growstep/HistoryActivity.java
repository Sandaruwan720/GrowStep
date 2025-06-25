package com.s23010163.growstep;



import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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

        // Get today's stats from SharedPreferences
        int todaySteps = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("today_steps", 0);
        float todayDistance = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("today_distance", 0f);
        float todayCalories = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("today_calories", 0f);

        String[] values = {
            String.valueOf(todaySteps),
            String.format("%.2fKm", todayDistance),
            String.valueOf(Math.round(todayCalories)),
            "288",
            "435",
            "2"
        };

        // Dynamically set data for each stat card
        for (int i = 0; i < titles.length; i++) {
            int statLayoutId = getResources().getIdentifier("stat_card", "id", getPackageName());
            int titleId = getResources().getIdentifier("statTitle", "id", getPackageName());
            int valueId = getResources().getIdentifier("statValue", "id", getPackageName());

            TextView title = findViewById(titleId);
            TextView value = findViewById(valueId);

            if (title != null && value != null) {
                title.setText(titles[i]);
                value.setText(values[i]);
            }
        }

    }
}
