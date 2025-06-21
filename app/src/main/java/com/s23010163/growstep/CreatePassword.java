package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePassword extends AppCompatActivity {

    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password); // Make sure this matches your XML filename

        passwordInput = findViewById(R.id.new_passwordt);
        confirmPasswordInput = findViewById(R.id.comfrim_password);
        confirmButton = findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill both password fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to HomeActivity
            Intent intent = new Intent(CreatePassword.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
