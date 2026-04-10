package com.example.appdevadmin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(UserModel user);
    }

    private List<UserModel> userList;
    private final Context context;
    private final OnUserClickListener listener;

    public UserAdapter(Context context, List<UserModel> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.tvUserName.setText(user.getFullName());

        // Handle Admin Display
        if ("Admin".equalsIgnoreCase(user.getRole())) {
            holder.tvUnit.setText("System Administrator");
            holder.tvRentingSince.setText("Access: Full Control");
        } else {
            holder.tvUnit.setText("Room: " + user.getRoomNumber());
            holder.tvRentingSince.setText("Renting since: " + user.getLeaseStart());
        }

        // 1. Generate initials
        String[] nameParts = user.getFullName().trim().split(" ");
        String initials = "";
        if (nameParts.length >= 2) {
            initials = String.valueOf(nameParts[0].charAt(0)) +
                    String.valueOf(nameParts[nameParts.length - 1].charAt(0));
        } else if (nameParts.length == 1 && !nameParts[0].isEmpty()) {
            initials = String.valueOf(nameParts[0].charAt(0));
        }
        holder.tvAvatarInitials.setText(initials.toUpperCase());

        // 2. DYNAMIC COLOR LOGIC
        if ("Admin".equalsIgnoreCase(user.getRole())) {
            // GRAY for Admin
            holder.cvAvatarBg.setCardBackgroundColor(Color.parseColor("#EEEEEE"));
            holder.tvAvatarInitials.setTextColor(Color.parseColor("#757575"));
        } else if ("Active".equalsIgnoreCase(user.getStatus())) {
            // BLUE for Active
            holder.cvAvatarBg.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.tvAvatarInitials.setTextColor(Color.parseColor("#1976D2"));
        } else if ("Inactive".equalsIgnoreCase(user.getStatus())) {
            // YELLOW for Inactive
            holder.cvAvatarBg.setCardBackgroundColor(Color.parseColor("#FFF9E6"));
            holder.tvAvatarInitials.setTextColor(Color.parseColor("#FBC02D"));
        }

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() { return userList.size(); }

    public void updateList(List<UserModel> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUnit, tvRentingSince, tvAvatarInitials;
        CardView cvAvatarBg; // The circular background behind initials

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvRentingSince = itemView.findViewById(R.id.tvRentingSince);
            tvAvatarInitials = itemView.findViewById(R.id.tvAvatarInitials);

            // Link the background view (Ensure this ID matches your item_user_card.xml)
            cvAvatarBg = itemView.findViewById(R.id.cvAvatarBg);
        }
    }
}