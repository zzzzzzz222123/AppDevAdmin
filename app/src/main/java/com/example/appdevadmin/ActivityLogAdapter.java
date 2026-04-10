package com.example.appdevadmin;

import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ViewHolder> {
    private List<ActivityLogModel> logList;

    public ActivityLogAdapter(List<ActivityLogModel> logList) { this.logList = logList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log_activity, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityLogModel log = logList.get(position);
        holder.tvTitle.setText(log.getTitle());
        holder.tvDetails.setText(log.getDetails());

        // Time formatting (e.g. "Just now", "2m ago")
        if (log.getTimestamp() != null) {
            long time = log.getTimestamp().toDate().getTime();
            holder.tvTime.setText(android.text.format.DateUtils.getRelativeTimeSpanString(time));
        }

        // DYNAMIC ICON AND COLOR LOGIC
        String type = log.getType() != null ? log.getType() : "general";

        switch (type) {
            case "tenant":
                holder.iconContainer.setCardBackgroundColor(Color.parseColor("#E9F2FF")); // Blue
                holder.ivIcon.setImageResource(R.drawable.outline_article_person_24);
                holder.ivIcon.setColorFilter(Color.parseColor("#2F80ED"));
                break;
            case "room":
                holder.iconContainer.setCardBackgroundColor(Color.parseColor("#FFF9E6")); // Yellow
                holder.ivIcon.setImageResource(R.drawable.outline_aq_indoor_24);
                holder.ivIcon.setColorFilter(Color.parseColor("#FBC02D"));
                break;
            case "payment":
                holder.iconContainer.setCardBackgroundColor(Color.parseColor("#E6F4EA")); // Green
                holder.ivIcon.setImageResource(R.drawable.outline_attach_money_24);
                holder.ivIcon.setColorFilter(Color.parseColor("#1E8E3E"));
                break;
            default: // maintenance or other
                holder.iconContainer.setCardBackgroundColor(Color.parseColor("#FEECEB")); // Red
                holder.ivIcon.setImageResource(android.R.drawable.stat_notify_error);
                holder.ivIcon.setColorFilter(Color.parseColor("#D32F2F"));
                break;
        }
    }

    @Override
    public int getItemCount() { return logList.size(); }
    // Add this method inside ActivityLogAdapter.java
    public void updateList(List<ActivityLogModel> newList) {
        this.logList = newList;
        notifyDataSetChanged();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetails, tvTime;
        ImageView ivIcon;
        MaterialCardView iconContainer;
        ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvLogTitle);
            tvDetails = v.findViewById(R.id.tvLogDetails);
            tvTime = v.findViewById(R.id.tvLogTime);
            ivIcon = v.findViewById(R.id.ivLogIcon);
            iconContainer = v.findViewById(R.id.logIconContainer);
        }
    }
}