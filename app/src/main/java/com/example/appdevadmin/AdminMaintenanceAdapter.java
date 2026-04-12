package com.example.appdevadmin;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminMaintenanceAdapter
        extends RecyclerView.Adapter<AdminMaintenanceAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(AdminMaintenanceRequest request);
    }

    private final List<AdminMaintenanceRequest> items;
    private final OnItemClickListener listener;

    public AdminMaintenanceAdapter(List<AdminMaintenanceRequest> items,
                                   OnItemClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_maintenance_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ── ViewHolder ─────────────────────────────────────────────────────────
    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View     sidebar;
        private final TextView tvTitle, tvStatus, tvRoom, tvDate;

        ViewHolder(View v) {
            super(v);
            sidebar  = v.findViewById(R.id.viewStatusSidebar);
            tvTitle  = v.findViewById(R.id.tvMaintenanceTitle);
            tvStatus = v.findViewById(R.id.tvMaintenanceStatus);
            tvRoom   = v.findViewById(R.id.tvMaintenanceRoom);
            tvDate   = v.findViewById(R.id.tvMaintenanceDate);
        }

        void bind(AdminMaintenanceRequest req, OnItemClickListener listener) {
            tvTitle.setText(req.title != null ? req.title : "—");
            tvRoom.setText(req.unit   != null ? req.unit  : "—");
            tvDate.setText(req.formattedDate());

            // Status label
            String statusLabel = req.status != null
                ? capitalize(req.status) : "Pending";
            tvStatus.setText(statusLabel);

            // Colors
            int color = req.statusColor();
            tvStatus.setTextColor(color);
            sidebar.setBackgroundColor(req.sidebarColor());

            // Status badge background tint
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(
                withAlpha(color, 0x22))); // 13% alpha background

            itemView.setOnClickListener(v -> listener.onClick(req));
        }

        private String capitalize(String s) {
            if (s == null || s.isEmpty()) return s;
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }

        private int withAlpha(int color, int alpha) {
            return (color & 0x00FFFFFF) | (alpha << 24);
        }
    }
}
