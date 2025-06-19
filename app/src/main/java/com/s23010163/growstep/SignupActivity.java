package com.s23010163.growstep;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class SignupActivity extends Activity {

    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> {
            Toast.makeText(SignupActivity.this, "Sign up clicked!", Toast.LENGTH_SHORT).show();
        });
    }
}
