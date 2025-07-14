package com.s23010163.growstep;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RewardsActivity extends AppCompatActivity {

    TextView tvGreeting, tvSubtitle, tvTotalPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvTotalPoints = findViewById(R.id.tvTotalPoints);

        // Set greeting with username from SharedPreferences
        String username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
        if (!username.isEmpty()) {
            tvGreeting.setText("Hello, " + username + "!");
        }
}}
