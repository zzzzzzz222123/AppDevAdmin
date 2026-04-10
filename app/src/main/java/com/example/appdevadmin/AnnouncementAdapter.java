package com.example.appdevadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private List<AnnouncementModel> list;

    public AnnouncementAdapter(List<AnnouncementModel> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reuse your "item1" style layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnnouncementModel item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.msg.setText(item.getContent());

        if (item.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.time.setText(sdf.format(item.getTimestamp().toDate()));
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, msg, time;
        ViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle1);
            msg = v.findViewById(R.id.tvMsg1);
            time = v.findViewById(R.id.tvTime1);
        }
    }
}