package com.example.appdevadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.tvUnit.setText(user.getRoomNumber());
        holder.tvRentingSince.setText("Renting since: " + user.getLeaseStart());

        // Generate initials from full name
        String[] nameParts = user.getFullName().trim().split(" ");
        String initials = "";
        if (nameParts.length >= 2) {
            initials = String.valueOf(nameParts[0].charAt(0)) +
                    String.valueOf(nameParts[nameParts.length - 1].charAt(0));
        } else if (nameParts.length == 1) {
            initials = String.valueOf(nameParts[0].charAt(0));
        }
        holder.tvAvatarInitials.setText(initials.toUpperCase());

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

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvRentingSince = itemView.findViewById(R.id.tvRentingSince);
            tvAvatarInitials = itemView.findViewById(R.id.tvAvatarInitials);
        }
    }
}