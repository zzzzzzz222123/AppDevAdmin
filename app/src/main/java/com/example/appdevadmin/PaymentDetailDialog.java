package com.example.appdevadmin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class PaymentDetailDialog extends DialogFragment {

    public static PaymentDetailDialog newInstance(PaymentModel payment) {
        PaymentDetailDialog fragment = new PaymentDetailDialog();
        Bundle args = new Bundle();
        args.putString("status", payment.getStatus());
        args.putString("name", payment.getTenantName());
        args.putString("room", payment.getRoomNumber());
        args.putDouble("amount", payment.getAmount());
        args.putString("date", payment.getMonth() + " " + payment.getDueDate() + ", " + payment.getYear());
        args.putString("method", payment.getMethod());
        args.putInt("dueDay", payment.getDueDate());

        // ADDED THESE
        args.putString("notes", payment.getNotes());
        args.putString("id", payment.getId()); // Using ID as reference number if Ref is null

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            // 1. Make the dialog full width with margins from your XML
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // 2. This is the key to removing the "double shadow"
            // It lets the system handle the dimming (shadow) naturally
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // 3. Optional: Adjust how dark the background is (0.0f to 1.0f)
            window.setDimAmount(0.5f);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String status = getArguments().getString("status");
        int layoutRes;

        if ("Paid".equalsIgnoreCase(status)) {
            layoutRes = R.layout.fragment_payment_paid;
        } else if ("Pending".equalsIgnoreCase(status)) {
            layoutRes = R.layout.fragment_payment_pending;
        } else {
            layoutRes = R.layout.fragment_payment_overdue;
        }

        View view = inflater.inflate(layoutRes, container, false);

        // Ensure the root of your XML is transparent so it doesn't double-dim
        view.setBackgroundColor(Color.TRANSPARENT);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;



        // Common Views
        TextView tvName = view.findViewById(R.id.tvTenantName);
        TextView tvRoom = view.findViewById(R.id.tvRoom);
        TextView tvAmount = view.findViewById(R.id.tvAmount);
        TextView tvDate = view.findViewById(R.id.tvDate);
        View btnClose = view.findViewById(R.id.btnClose);

        if (tvName != null) tvName.setText(args.getString("name"));
        if (tvRoom != null) tvRoom.setText(args.getString("room"));
        if (tvAmount != null) tvAmount.setText("₱" + String.format("%,.2f", args.getDouble("amount")));
        if (tvDate != null) tvDate.setText(args.getString("date"));

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());

        // Status Specific Logic
        String status = args.getString("status");
        if ("Paid".equalsIgnoreCase(status)) {
            TextView tvMethod = view.findViewById(R.id.tvMethod);
            TextView tvRef = view.findViewById(R.id.tvRef);
            TextView tvNotes = view.findViewById(R.id.tvNotes);
            if (tvMethod != null) {
                tvMethod.setText(args.getString("method") != null ? args.getString("method") : "Cash");
            }
            if (tvRef != null) tvRef.setText("PAY-" + args.getString("id").substring(0, 8).toUpperCase());

            // Setting Notes
            if (tvNotes != null) {
                String notes = args.getString("notes");
                if (notes != null && !notes.trim().isEmpty()) {
                    tvNotes.setText(notes);
                } else {
                    tvNotes.setText("No additional notes provided.");
                }
            }
        } else {
            // Logic for Pending/Overdue
            TextView tvRemaining = view.findViewById(R.id.tvRemaining);
            if (tvRemaining != null) {
                int dueDay = args.getInt("dueDay");
                int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int diff = dueDay - currentDay;
                if (diff > 0) tvRemaining.setText(diff + " days");
                else if (diff == 0) tvRemaining.setText("Due Today");
                else tvRemaining.setText(Math.abs(diff) + " days late");
            }



            View btnRecord = view.findViewById(R.id.btnRecordPayment);
            if (btnRecord != null) {
                btnRecord.setOnClickListener(v -> {
                    dismiss();
                    // You can trigger a navigation here if needed
                    Toast.makeText(getContext(), "Navigate to Record Payment", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}