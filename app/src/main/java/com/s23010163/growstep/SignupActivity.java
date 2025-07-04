package com.s23010163.growstep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends Activity {

    EditText usernameInput, emailInput, cityInput, passwordInput, confirmPasswordInput;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        cityInput = findViewById(R.id.cityInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String city = cityInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Validation checks
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (city.isEmpty()) {
                Toast.makeText(this, "Please enter your city", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill both password fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Integrate with SQLite DB
            UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
            if (dbHelper.userExists(username)) {
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = dbHelper.registerUser(username, email, password);
            if (success) {
                // Save username to SharedPreferences
                getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("username", username)
                    .apply();
                Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Sign up failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
