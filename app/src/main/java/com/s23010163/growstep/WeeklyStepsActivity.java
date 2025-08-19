package com.s23010163.growstep;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeeklyStepsActivity extends AppCompatActivity {

    private LinearLayout navHome, navGroups, navChallenges, navProfile;
    private TextView tvWeekTitle, tvTotalSteps, tvTotalDistance, tvTotalCalories, tvGroupsJoined;
    private TextView tvGroupSteps, tvGroupDistance, tvGroupCalories;
    private RecyclerView rvDailyBreakdown;
    private WeeklyStepsAdapter weeklyStepsAdapter;
    private UserDatabaseHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_steps);

        // Initialize database helper
        dbHelper = new UserDatabaseHelper(this);
        currentUsername = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");

        // Initialize views
        initializeViews();
        setupNavigation();
        loadWeeklyData();
        setupDailyBreakdown();
    }

    private void initializeViews() {
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navChallenges = findViewById(R.id.nav_challenges);
        navProfile = findViewById(R.id.nav_profile);

        tvWeekTitle = findViewById(R.id.tvWeekTitle);
        tvTotalSteps = findViewById(R.id.tvTotalSteps);
        tvTotalDistance = findViewById(R.id.tvTotalDistance);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvGroupsJoined = findViewById(R.id.tvGroupsJoined);
        tvGroupSteps = findViewById(R.id.tvGroupSteps);
        tvGroupDistance = findViewById(R.id.tvGroupDistance);
        tvGroupCalories = findViewById(R.id.tvGroupCalories);
        rvDailyBreakdown = findViewById(R.id.rvDailyBreakdown);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(WeeklyStepsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(WeeklyStepsActivity.this, GroupsActivity.class);
            startActivity(intent);
            finish();
        });

        navChallenges.setOnClickListener(v -> {
            Intent intent = new Intent(WeeklyStepsActivity.this, ChallengesActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(WeeklyStepsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadWeeklyData() {
        if (currentUsername.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set week title
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String weekStart = sdf.format(calendar.getTime());
        
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        String weekEnd = sdf.format(calendar.getTime());
        tvWeekTitle.setText("This Week: " + weekStart + " - " + weekEnd);

        // Load personal weekly totals
        try {
            int totalSteps = dbHelper.getThisWeekTotalSteps(currentUsername);
            float totalDistance = dbHelper.getThisWeekTotalDistance(currentUsername);
            float totalCalories = dbHelper.getThisWeekTotalCalories(currentUsername);

            tvTotalSteps.setText(String.format("%,d", totalSteps));
            tvTotalDistance.setText(String.format("%.2f km", totalDistance));
            tvTotalCalories.setText(String.format("%.0f kcal", totalCalories));
        } catch (Exception e) {
            e.printStackTrace();
            tvTotalSteps.setText("0");
            tvTotalDistance.setText("0.00 km");
            tvTotalCalories.setText("0 kcal");
        }

        // Load groups joined this week
        try {
            int groupsJoined = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("groups_joined", 0);
            tvGroupsJoined.setText(String.valueOf(groupsJoined));
        } catch (Exception e) {
            e.printStackTrace();
            tvGroupsJoined.setText("0");
        }

        // Load group totals (if user is in any groups)
        try {
            loadGroupTotals();
        } catch (Exception e) {
            e.printStackTrace();
            tvGroupSteps.setText("0");
            tvGroupDistance.setText("0.00 km");
            tvGroupCalories.setText("0 kcal");
        }
    }

    private void loadGroupTotals() {
        // Get all groups the user is a member of
        Cursor groupCursor = dbHelper.getAllGroups();
        int totalGroupSteps = 0;
        float totalGroupDistance = 0f;
        float totalGroupCalories = 0f;
        int groupCount = 0;

        if (groupCursor != null && groupCursor.moveToFirst()) {
            do {
                int groupId = groupCursor.getInt(groupCursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_GROUP_ID));
                if (dbHelper.isGroupMember(groupId, currentUsername)) {
                    totalGroupSteps += dbHelper.getGroupThisWeekTotalSteps(groupId);
                    totalGroupDistance += dbHelper.getGroupThisWeekTotalDistance(groupId);
                    totalGroupCalories += dbHelper.getGroupThisWeekTotalCalories(groupId);
                    groupCount++;
                }
            } while (groupCursor.moveToNext());
            groupCursor.close();
        }

        if (groupCount > 0) {
            tvGroupSteps.setText(String.format("%,d", totalGroupSteps));
            tvGroupDistance.setText(String.format("%.2f km", totalGroupDistance));
            tvGroupCalories.setText(String.format("%.0f kcal", totalGroupCalories));
        } else {
            tvGroupSteps.setText("0");
            tvGroupDistance.setText("0.00 km");
            tvGroupCalories.setText("0 kcal");
        }
    }

    private void setupDailyBreakdown() {
        rvDailyBreakdown.setLayoutManager(new LinearLayoutManager(this));
        
        List<DailyStepData> dailyDataList = new ArrayList<>();
        
        // Get current week's data
        try {
            Cursor cursor = dbHelper.getThisWeekSteps(currentUsername);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_WEEKLY_DATE));
                    int steps = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_WEEKLY_STEPS));
                    float distance = cursor.getFloat(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_WEEKLY_DISTANCE));
                    float calories = cursor.getFloat(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_WEEKLY_CALORIES));
                    
                    dailyDataList.add(new DailyStepData(date, steps, distance, calories));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If there's an error, just continue with empty list
        }

        // Fill in missing days with zero values
        fillMissingDays(dailyDataList);
        
        weeklyStepsAdapter = new WeeklyStepsAdapter(dailyDataList);
        rvDailyBreakdown.setAdapter(weeklyStepsAdapter);
    }

    private void fillMissingDays(List<DailyStepData> dailyDataList) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat daySdf = new SimpleDateFormat("EEE", Locale.getDefault());

        List<DailyStepData> completeWeek = new ArrayList<>();
        
        for (int i = 0; i < 7; i++) {
            String date = sdf.format(calendar.getTime());
            String dayName = daySdf.format(calendar.getTime());
            
            // Check if we have data for this day
            DailyStepData existingData = null;
            for (DailyStepData data : dailyDataList) {
                if (data.getDate().equals(date)) {
                    existingData = data;
                    break;
                }
            }
            
            if (existingData != null) {
                completeWeek.add(existingData);
            } else {
                completeWeek.add(new DailyStepData(date, dayName, 0, 0f, 0f));
            }
            
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        dailyDataList.clear();
        dailyDataList.addAll(completeWeek);
    }

    public static class DailyStepData {
        private String date;
        private String dayName;
        private int steps;
        private float distance;
        private float calories;

        public DailyStepData(String date, int steps, float distance, float calories) {
            this.date = date;
            this.steps = steps;
            this.distance = distance;
            this.calories = calories;
            
            // Extract day name from date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sdf.parse(date));
                SimpleDateFormat daySdf = new SimpleDateFormat("EEE", Locale.getDefault());
                this.dayName = daySdf.format(calendar.getTime());
            } catch (Exception e) {
                this.dayName = "Unknown";
            }
        }

        public DailyStepData(String date, String dayName, int steps, float distance, float calories) {
            this.date = date;
            this.dayName = dayName;
            this.steps = steps;
            this.distance = distance;
            this.calories = calories;
        }

        public String getDate() { return date; }
        public String getDayName() { return dayName; }
        public int getSteps() { return steps; }
        public float getDistance() { return distance; }
        public float getCalories() { return calories; }
    }

    private static class WeeklyStepsAdapter extends RecyclerView.Adapter<WeeklyStepsAdapter.ViewHolder> {
        private List<DailyStepData> dailyDataList;

        public WeeklyStepsAdapter(List<DailyStepData> dailyDataList) {
            this.dailyDataList = dailyDataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_daily_steps, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DailyStepData data = dailyDataList.get(position);
            holder.tvDay.setText(data.getDayName());
            holder.tvSteps.setText(String.format("%,d", data.getSteps()));
            holder.tvDistance.setText(String.format("%.2f km", data.getDistance()));
            holder.tvCalories.setText(String.format("%.0f kcal", data.getCalories()));
        }

        @Override
        public int getItemCount() {
            return dailyDataList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDay, tvSteps, tvDistance, tvCalories;

            ViewHolder(View itemView) {
                super(itemView);
                tvDay = itemView.findViewById(R.id.tvDay);
                tvSteps = itemView.findViewById(R.id.tvSteps);
                tvDistance = itemView.findViewById(R.id.tvDistance);
                tvCalories = itemView.findViewById(R.id.tvCalories);
            }
        }
    }
}
