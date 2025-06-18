package com.s23010163.growstep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.text.TextPaint;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Splash screen layout

        // 1. Find the TextView
        TextView textView = findViewById(R.id.textViewGradient);

        // 2. Get TextView dimensions
        TextPaint paint = textView.getPaint();
        float textHeight = textView.getTextSize();

        // 3. Create vertical gradient shader (top to bottom)
        Shader shader = new LinearGradient(
                0, 0, 0, textHeight,  // Vertical gradient
                new int[] {
                        Color.parseColor("#F7EBF8"),  // Top (dark purple)
                          Color.parseColor("#6E006E"), // Middle (medium violet)
                        Color.parseColor("#0D0D0D")   // Bottom (light violet)
                },
                new float[] { 0.5f,01f, 0f },  // Color stops: top, middle, bottom
                Shader.TileMode.CLAMP
        );

        // 4. Apply shader to text
        paint.setShader(shader);
        textView.invalidate();

        // 5. Delay for 3 seconds and move to LoginActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close splash screen
        }, 3000);
    }
}
