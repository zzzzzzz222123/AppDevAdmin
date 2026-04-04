package com.example.appdevadmin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    public interface OnRoomClickListener {
        void onRoomClick(RoomModel room);
    }

    private List<RoomModel> roomList;
    private final Context context;
    private final OnRoomClickListener listener;

    public RoomAdapter(Context context, List<RoomModel> roomList, OnRoomClickListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_card, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);

        holder.tvRoomName.setText(room.getRoomNumber() + " - " + room.getRoomType());
        holder.tvRoomFloor.setText(room.getFloor());
        holder.tvRoomPrice.setText("₱" + String.format("%,.2f", room.getMonthlyRent()) + "/mo");
        holder.tvRoomStatus.setText(room.getStatus());

        // Status badge styling
        if (room.getStatus().equalsIgnoreCase("Occupied")) {
            holder.tvRoomStatus.setBackgroundResource(R.drawable.badge_occupied);
            holder.tvRoomStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else if (room.getStatus().equalsIgnoreCase("Vacant")) {
            holder.tvRoomStatus.setBackgroundResource(R.drawable.badge_vacant);
            holder.tvRoomStatus.setTextColor(Color.parseColor("#1976D2"));
        } else {
            holder.tvRoomStatus.setBackgroundResource(R.drawable.badge_vacant);
            holder.tvRoomStatus.setTextColor(Color.parseColor("#F57C00"));
        }

        holder.itemView.setOnClickListener(v -> listener.onRoomClick(room));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void updateList(List<RoomModel> newList) {
        this.roomList = newList;
        notifyDataSetChanged();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvRoomFloor, tvRoomPrice, tvRoomStatus;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomFloor = itemView.findViewById(R.id.tvRoomFloor);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomStatus = itemView.findViewById(R.id.tvRoomStatus);
        }
    }
}