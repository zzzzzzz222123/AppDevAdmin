package com.example.appdevadmin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RenterHistoryAdapter extends RecyclerView.Adapter<RenterHistoryAdapter.ViewHolder> {
    private List<RenterHistoryModel> list;

    public RenterHistoryAdapter(List<RenterHistoryModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_renter_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RenterHistoryModel item = list.get(position);

        // Use updated getter names from the model
        holder.name.setText(item.getTenantName());
        holder.duration.setText(item.getLeaseRange());
        holder.status.setText(item.getStatus());

        // Simple avatar initial (e.g., "John Doe" -> "J")
        if (item.getTenantName() != null && !item.getTenantName().isEmpty()) {
            holder.initial.setText(item.getTenantName().substring(0, 1).toUpperCase());
        }

        // Optional: Change status color based on "Current" vs "Past"
        if ("Current".equalsIgnoreCase(item.getStatus())) {
            holder.status.setTextColor(Color.parseColor("#2E7D32")); // Green for Current
        } else {
            holder.status.setTextColor(Color.parseColor("#757575")); // Gray for Past
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, duration, status, initial;

        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.tvRenterName);
            duration = v.findViewById(R.id.tvRenterDuration);
            status = v.findViewById(R.id.tvHistoryStatus);
            initial = v.findViewById(R.id.tvRenterInitial);
        }
    }
}