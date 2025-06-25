package com.s23010163.growstep;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateGroupWalkActivity extends AppCompatActivity {

    EditText groupName, startTime, maxParticipants, route;
    Button createGroupButton;
    Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_walk);

        // Initialize views
        groupName = findViewById(R.id.groupName);
        startTime = findViewById(R.id.startTime);
        maxParticipants = findViewById(R.id.maxParticipants);
        route = findViewById(R.id.route);
        createGroupButton = findViewById(R.id.createGroupButton);

        // Open date & time picker
        startTime.setOnClickListener(v -> openDateTimePicker());

        // Create Group button click listener
        createGroupButton.setOnClickListener(v -> {
            String name = groupName.getText().toString().trim();
            String time = startTime.getText().toString().trim();
            String participants = maxParticipants.getText().toString().trim();
            String walkRoute = route.getText().toString().trim();

            // Input validation
            if (name.isEmpty()) {
                groupName.setError("Enter group name");
                return;
            }

            if (time.isEmpty() || time.equals("mm/dd/yyyy,  -- : -- --")) {
                startTime.setError("Please select a valid date and time");
                return;
            }

            if (participants.isEmpty()) {
                maxParticipants.setError("Enter max participants");
                return;
            }

            int participantCount;
            try {
                participantCount = Integer.parseInt(participants);
                if (participantCount <= 0) {
                    maxParticipants.setError("Enter a positive number");
                    return;
                }
            } catch (NumberFormatException e) {
                maxParticipants.setError("Enter a valid number");
                return;
            }

            if (walkRoute.isEmpty()) {
                route.setError("Enter route name");
                return;
            }

            // Validate date and time not in the past
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
                Date selected = sdf.parse(time);
                if (selected != null && selected.before(new Date())) {
                    startTime.setError("Please select a future time");
                    return;
                }
            } catch (Exception e) {
                startTime.setError("Invalid date format");
                return;
            }

            // Success
            Toast.makeText(this, "Group Created!", Toast.LENGTH_SHORT).show();

            // Save group to SharedPreferences
            try {
                android.content.SharedPreferences prefs = getSharedPreferences("groups_prefs", MODE_PRIVATE);
                String groupsJson = prefs.getString("groups_list", "[]");
                org.json.JSONArray groupsArray = new org.json.JSONArray(groupsJson);
                org.json.JSONObject groupObj = new org.json.JSONObject();
                groupObj.put("name", name);
                groupObj.put("time", time);
                groupObj.put("participants", participantCount);
                groupObj.put("route", walkRoute);
                groupsArray.put(groupObj);
                prefs.edit().putString("groups_list", groupsArray.toString()).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Navigate to GroupsActivity
            Intent intent = new Intent(CreateGroupWalkActivity.this, GroupsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Opens date and time picker
    private void openDateTimePicker() {
        selectedDateTime = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(this,
                            (view1, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
                                String formattedDate = sdf.format(selectedDateTime.getTime());
                                startTime.setText(formattedDate);
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            false);
                    timePicker.show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }
}
