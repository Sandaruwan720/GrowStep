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

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                addMessage(message);
                messageInput.setText("");
            }
        });

        // Update group name sections based on Intent extra
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("group_name");
        TextView tvMorningFitnessWalk = findViewById(R.id.tvMorningFitnessWalk);
        TextView tvWalkingWithFriends = findViewById(R.id.tvWalkingWithFriends);
        int groupParticipants = intent.getIntExtra("group_participants", 4);
        if (groupName != null && !groupName.isEmpty()) {
            tvMorningFitnessWalk.setText(groupName + " Fitness Walk");
            tvWalkingWithFriends.setText("Walking with " + groupParticipants + " friends in " + groupName);
        }

        // Show group members in RecyclerView
        int groupId = intent.getIntExtra("group_id", -1);
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

    private void addMessage(String message) {
        TextView msgView = new TextView(this);
        msgView.setText(message);
        msgView.setTextSize(16f);
        msgView.setTextColor(getResources().getColor(android.R.color.black));
        msgView.setBackground(getResources().getDrawable(R.drawable.wallking_group_bg_card, null));
        msgView.setPadding(24, 16, 24, 16);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 8, 0, 8);
        msgView.setLayoutParams(layoutParams);

        chatMessagesLayout.addView(msgView);

        chatScroll.post(() -> chatScroll.fullScroll(View.FOCUS_DOWN));
    }
}
