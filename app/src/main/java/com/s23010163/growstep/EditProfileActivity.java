package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class EditProfileActivity extends AppCompatActivity {

	private EditText editUsername;
	private EditText editFullName;
	private EditText editAge;
	private EditText editGender;
	private EditText editHeight;
	private EditText editWeight;
	private Button btnSaveProfile;
	private Button btnLogout;
	private String currentUsername;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		editUsername = findViewById(R.id.editUsername);
		editFullName = findViewById(R.id.editFullName);
		editAge = findViewById(R.id.editAge);
		editGender = findViewById(R.id.editGender);
		editHeight = findViewById(R.id.editHeight);
		editWeight = findViewById(R.id.editWeight);
		btnSaveProfile = findViewById(R.id.btnSaveProfile);
		btnLogout = findViewById(R.id.btnLogout);
		MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

		// Prefill from SharedPreferences
		currentUsername = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
		String fullName = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("full_name", "");
		int age = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("age", 0);
		String gender = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("gender", "");
		float height = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("height", 0f);
		float weight = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("weight", 0f);

		editUsername.setText(currentUsername);
		editFullName.setText(fullName);
		if (age > 0) editAge.setText(String.valueOf(age));
		editGender.setText(gender);
		if (height > 0f) editHeight.setText(String.valueOf(height));
		if (weight > 0f) editWeight.setText(String.valueOf(weight));

		topAppBar.setNavigationOnClickListener(v -> onBackPressed());

		btnSaveProfile.setOnClickListener(v -> {
			String newUsername = editUsername.getText().toString().trim();
			String newFullName = editFullName.getText().toString().trim();
			String ageStr = editAge.getText().toString().trim();
			String newGender = editGender.getText().toString().trim();
			String heightStr = editHeight.getText().toString().trim();
			String weightStr = editWeight.getText().toString().trim();

			if (TextUtils.isEmpty(newUsername)) {
				Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
				return;
			}

			if (TextUtils.isEmpty(newFullName)) {
				Toast.makeText(this, "Full name cannot be empty", Toast.LENGTH_SHORT).show();
				return;
			}

			Integer newAge = null;
			Float newHeight = null;
			Float newWeight = null;
			try {
				if (!TextUtils.isEmpty(ageStr)) newAge = Integer.parseInt(ageStr);
				if (!TextUtils.isEmpty(heightStr)) newHeight = Float.parseFloat(heightStr);
				if (!TextUtils.isEmpty(weightStr)) newWeight = Float.parseFloat(weightStr);
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Please enter valid numeric values for age, height, and weight", Toast.LENGTH_SHORT).show();
				return;
			}

			// If username changed, update DB and references
			if (!newUsername.equals(currentUsername)) {
				UserDatabaseHelper db = new UserDatabaseHelper(this);
				if (db.userExists(newUsername)) {
					Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
					return;
				}
				boolean updated = db.updateUsername(currentUsername, newUsername);
				if (!updated) {
					Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show();
					return;
				}
				// Save new username to preferences
				getSharedPreferences("user_prefs", MODE_PRIVATE).edit().putString("username", newUsername).apply();
				currentUsername = newUsername;
			}

			// Save other profile data to SharedPreferences
			android.content.SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
			editor.putString("full_name", newFullName);
			if (newAge != null && newAge > 0) editor.putInt("age", newAge); else editor.remove("age");
			if (!TextUtils.isEmpty(newGender)) editor.putString("gender", newGender); else editor.remove("gender");
			if (newHeight != null && newHeight > 0f) editor.putFloat("height", newHeight); else editor.remove("height");
			if (newWeight != null && newWeight > 0f) editor.putFloat("weight", newWeight); else editor.remove("weight");
			editor.apply();

			Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
			finish();
		});

		btnLogout.setOnClickListener(v -> {
			// Clear session and navigate to Login
			getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
			Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
		});
	}
}


