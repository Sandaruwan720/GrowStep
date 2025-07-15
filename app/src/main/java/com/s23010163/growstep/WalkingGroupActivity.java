package com.s23010163.growstep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageView;

public class WalkingGroupActivity extends AppCompatActivity {

    EditText messageInput;
    ImageButton sendButton;
    LinearLayout chatMessagesLayout;
    ScrollView chatScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_group);

        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatMessagesLayout = findViewById(R.id.chatMessagesLayout);
        chatScroll = findViewById(R.id.chatScroll);

        // Get groupId from intent
        Intent intent = getIntent();
        int groupId = intent.getIntExtra("group_id", -1);

        // Load all messages for this group
        if (groupId != -1) {
            UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
            android.database.Cursor cursor = dbHelper.getMessagesForGroup(groupId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String sender = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_MESSAGE_USERNAME));
                    String text = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_MESSAGE_TEXT));
                    addMessage(text, sender);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                String username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
                addMessage(message, username);
                messageInput.setText("");
                // Save message to DB
                if (groupId != -1) {
                    UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
                    dbHelper.insertMessage(groupId, username, message, System.currentTimeMillis());
                }
            }
        });

        // Update group name sections based on Intent extra
        String groupName = intent.getStringExtra("group_name");
        TextView tvMorningFitnessWalk = findViewById(R.id.tvMorningFitnessWalk);
        TextView tvWalkingWithFriends = findViewById(R.id.tvWalkingWithFriends);
        int groupParticipants = intent.getIntExtra("group_participants", 4);
        if (groupName != null && !groupName.isEmpty()) {
            tvMorningFitnessWalk.setText(groupName + " Fitness Walk");
            tvWalkingWithFriends.setText("Walking with " + groupParticipants + " friends in " + groupName);
        }

        // Show group members in RecyclerView
        RecyclerView membersRecyclerView = findViewById(R.id.groupMembersRecyclerView);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> memberNames = new ArrayList<>();
        if (groupId != -1) {
            UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
            android.database.Cursor cursor = dbHelper.getGroupMembers(groupId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String memberName = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_MEMBER_USERNAME));
                    memberNames.add(memberName);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        GroupMembersAdapter adapter = new GroupMembersAdapter(memberNames);
        membersRecyclerView.setAdapter(adapter);
    }

    // Update addMessage to accept message and username
    private void addMessage(String message, String username) {
        // Get current user
        String currentUser = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");
        boolean isCurrentUser = username.equals(currentUser);

        // Outer vertical layout for the message row
        LinearLayout messageRow = new LinearLayout(this);
        messageRow.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, 8, 0, 8);
        messageRow.setLayoutParams(rowParams);

        // FrameLayout for bubble + username/icon overlay
        android.widget.FrameLayout bubbleFrame = new android.widget.FrameLayout(this);
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bubbleParams.gravity = isCurrentUser ? android.view.Gravity.END : android.view.Gravity.START;
        bubbleFrame.setLayoutParams(bubbleParams);

        // Message bubble
        TextView msgView = new TextView(this);
        msgView.setText(message);
        msgView.setTextSize(16f);
        msgView.setTextColor(getResources().getColor(android.R.color.black));
        if (isCurrentUser) {
            msgView.setBackground(getResources().getDrawable(R.drawable.chat_bubble_right, null));
        } else {
            msgView.setBackground(getResources().getDrawable(R.drawable.chat_bubble_left, null));
        }
        msgView.setPadding(32, 48, 32, 24); // Extra top padding for icon+username bubble

        android.widget.FrameLayout.LayoutParams msgParams = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        );
        msgParams.gravity = isCurrentUser ? (android.view.Gravity.END | android.view.Gravity.BOTTOM) : (android.view.Gravity.START | android.view.Gravity.BOTTOM);
        msgView.setLayoutParams(msgParams);

        // --- Username and profile icon pill bubble ---
        LinearLayout userInfoBubble = new LinearLayout(this);
        userInfoBubble.setOrientation(LinearLayout.HORIZONTAL);
        userInfoBubble.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int[] colors = {0xFF6C63FF, 0xFF00BFAE, 0xFFFFB300, 0xFFEF5350, 0xFF42A5F5, 0xFFAB47BC, 0xFF26A69A};
        int color = colors[Math.abs(username.hashCode()) % colors.length];
        android.graphics.drawable.GradientDrawable pillBg = new android.graphics.drawable.GradientDrawable();
        pillBg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        pillBg.setCornerRadius(32f * getResources().getDisplayMetrics().density); // pill shape
        pillBg.setColor(color);
        pillBg.setAlpha(230); // slightly transparent for a modern look
        userInfoBubble.setBackground(pillBg);
        int pillPadV = (int)(4 * getResources().getDisplayMetrics().density);
        int pillPadH = (int)(12 * getResources().getDisplayMetrics().density);
        userInfoBubble.setPadding(pillPadH, pillPadV, pillPadH, pillPadV);
        android.widget.FrameLayout.LayoutParams userInfoParams = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        );
        userInfoParams.gravity = isCurrentUser ? (android.view.Gravity.END | android.view.Gravity.TOP) : (android.view.Gravity.START | android.view.Gravity.TOP);
        if (isCurrentUser) {
            userInfoParams.setMargins(0, 8, 16, 0); // Top and right margin inside bubble
        } else {
            userInfoParams.setMargins(16, 8, 0, 0); // Top and left margin inside bubble
        }
        userInfoBubble.setLayoutParams(userInfoParams);

        // Profile icon (ImageView with color filter, like group members)
        ImageView avatar = new ImageView(this);
        avatar.setImageResource(R.drawable.ic_person);
        int size = (int) (20 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(size, size);
        avatarParams.setMargins(0, 0, 8, 0);
        avatar.setLayoutParams(avatarParams);
        avatar.setColorFilter(0xFFFFFFFF); // White icon for contrast

        // Username
        TextView nameView = new TextView(this);
        nameView.setText(username);
        nameView.setTextSize(12f);
        nameView.setTextColor(0xFFFFFFFF); // White text for contrast
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameView.setLayoutParams(nameParams);

        // Add avatar and username to userInfoBubble
        userInfoBubble.addView(avatar);
        userInfoBubble.addView(nameView);

        // Add message and user info bubble to bubble frame
        bubbleFrame.addView(msgView);
        bubbleFrame.addView(userInfoBubble);

        // Add bubble to message row
        messageRow.addView(bubbleFrame);

        // Add the whole row to the chat
        chatMessagesLayout.addView(messageRow);
        chatScroll.post(() -> chatScroll.fullScroll(View.FOCUS_DOWN));
    }
}
