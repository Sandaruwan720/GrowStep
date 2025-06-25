package com.s23010163.growstep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PersonalInfoActivity extends Activity {

    EditText inputFullName, inputGender, inputAge, inputHeight, inputWeight;
    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        inputFullName = findViewById(R.id.inputFullName);
        inputGender = findViewById(R.id.inputGender);
        inputAge = findViewById(R.id.inputAge);
        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(view -> {
            String fullName = inputFullName.getText().toString().trim();
            String gender = inputGender.getText().toString().trim().toLowerCase();
            String ageStr = inputAge.getText().toString().trim();
            String heightStr = inputHeight.getText().toString().trim();
            String weightStr = inputWeight.getText().toString().trim();

            // Validate empty fields
            if (fullName.isEmpty() || gender.isEmpty() || ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate gender
            if (!(gender.equals("male") || gender.equals("female") || gender.equals("other"))) {
                Toast.makeText(this, "Gender must be male, female, or other.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate numeric values
            int age;
            float height, weight;

            try {
                age = Integer.parseInt(ageStr);
                height = Float.parseFloat(heightStr);
                weight = Float.parseFloat(weightStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numeric values for age, height, and weight.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (age <= 0 || height <= 0 || weight <= 0) {
                Toast.makeText(this, "Values must be greater than 0.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save full name to SharedPreferences
            getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putString("full_name", fullName)
                .apply();

            // Success
            String result = "Saved: Gender: " + gender + "\nAge: " + age + "\nHeight: " + height + " cm\nWeight: " + weight + " kg";
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();

            // Navigate to HomeActivity
            Intent intent = new Intent(PersonalInfoActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
