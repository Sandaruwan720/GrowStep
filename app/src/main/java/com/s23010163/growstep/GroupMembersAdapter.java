package com.s23010163.growstep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.MemberViewHolder> {
    private final List<String> memberNames;

    public GroupMembersAdapter(List<String> memberNames) {
        this.memberNames = memberNames;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String name = memberNames.get(position);
        holder.memberName.setText(name); // No bullet
        // Set profile icon with beautiful tint
        holder.memberIcon.setImageDrawable(ContextCompat.getDrawable(holder.memberIcon.getContext(), R.drawable.ic_person));
        holder.memberIcon.setColorFilter(pickColor(name));
    }

    private int pickColor(String name) {
        // Pick a color based on the hash of the name
        int[] colors = {0xFF6C63FF, 0xFF00BFAE, 0xFFFFB300, 0xFFEF5350, 0xFF42A5F5, 0xFFAB47BC, 0xFF26A69A};
        int hash = Math.abs(name.hashCode());
        return colors[hash % colors.length];
    }

    @Override
    public int getItemCount() {
        return memberNames.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView memberIcon;
        TextView memberName;
        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberIcon = itemView.findViewById(R.id.memberIcon);
            memberName = itemView.findViewById(R.id.memberName);
        }
    }
} 