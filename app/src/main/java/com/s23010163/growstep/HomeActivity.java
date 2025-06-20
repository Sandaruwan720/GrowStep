package com.s23010163.growstep;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private ProgressBar stepsCircle;
    private TextView tvStepsCount;
    private final int maxSteps = 10000;
    private final int currentSteps = 7250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        stepsCircle = findViewById(R.id.stepsCircle);
        tvStepsCount = findViewById(R.id.tvStepsCount);

        stepsCircle.setMax(maxSteps);
        animateSteps();
    }

    private void animateSteps() {
        ObjectAnimator animator = ObjectAnimator.ofInt(stepsCircle, "progress", 0, currentSteps);
        animator.setDuration(1500);
        animator.start();

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            tvStepsCount.setText(String.format("%,d", currentSteps));
        }, 1500);
    }
}

