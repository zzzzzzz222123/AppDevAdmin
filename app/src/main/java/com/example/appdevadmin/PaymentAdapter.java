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

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    public interface OnPaymentClickListener {
        void onPaymentClick(PaymentModel payment);
    }

    private List<PaymentModel> paymentList;
    private final Context context;
    private final OnPaymentClickListener listener;

    public PaymentAdapter(Context context, List<PaymentModel> paymentList,
                          OnPaymentClickListener listener) {
        this.context = context;
        this.paymentList = paymentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_pending, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentModel payment = paymentList.get(position);

        holder.tvTenantName.setText(payment.getTenantName());
        holder.tvAmount.setText("₱" + String.format("%,.2f", payment.getAmount()));

        // Display Type next to Room Number (e.g., "Room 101 • Monthly Rent")
        String roomAndType = payment.getRoomNumber() + " • " + (payment.getType() != null ? payment.getType() : "Invoice");
        holder.tvRoomInfo.setText(roomAndType);

        holder.tvDueDate.setText(payment.getMonth() + " " + payment.getYear()
                + " · Due: " + payment.getDueDate());

        // Initials
        String[] parts = payment.getTenantName().trim().split(" ");
        String initials = parts.length >= 2
                ? String.valueOf(parts[0].charAt(0)) + parts[parts.length - 1].charAt(0)
                : String.valueOf(parts[0].charAt(0));
        holder.tvAvatarInitials.setText(initials.toUpperCase());

        // Status badge
        holder.tvStatusBadge.setText(payment.getStatus());
        switch (payment.getStatus()) {
            case "Paid":
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_occupied);
                holder.tvStatusBadge.setTextColor(Color.parseColor("#2E7D32"));
                holder.tvDueDate.setTextColor(Color.parseColor("#AAAAAA"));
                break;
            case "Overdue":
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_overdue);
                holder.tvStatusBadge.setTextColor(Color.parseColor("#EF4444"));
                holder.tvDueDate.setTextColor(Color.parseColor("#EF4444"));
                break;
            default: // Pending
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_pending);
                holder.tvStatusBadge.setTextColor(Color.parseColor("#EF6C00"));
                holder.tvDueDate.setTextColor(Color.parseColor("#AAAAAA"));
                break;
        }

        holder.itemView.setOnClickListener(v -> listener.onPaymentClick(payment));
    }

    @Override
    public int getItemCount() { return paymentList.size(); }

    public void updateList(List<PaymentModel> newList) {
        this.paymentList = newList;
        notifyDataSetChanged();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenantName, tvAmount, tvRoomInfo, tvStatusBadge, tvDueDate, tvAvatarInitials;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenantName = itemView.findViewById(R.id.tvTenantName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvRoomInfo = itemView.findViewById(R.id.tvRoomInfo);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvAvatarInitials = itemView.findViewById(R.id.tvAvatarInitials);
        }
    }
}