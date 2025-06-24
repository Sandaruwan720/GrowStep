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

    String[] values = {
            "456", "0.34Km",
            "23", "288",
            "435", "2"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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
