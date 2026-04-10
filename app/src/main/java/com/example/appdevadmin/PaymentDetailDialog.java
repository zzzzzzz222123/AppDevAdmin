package com.example.appdevadmin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class PaymentDetailDialog extends DialogFragment {

    public static PaymentDetailDialog newInstance(PaymentModel payment) {
        PaymentDetailDialog fragment = new PaymentDetailDialog();
        Bundle args = new Bundle();
        args.putString("status", payment.getStatus());
        args.putString("userId", payment.getUserId()); // Needed for auto-fill
        args.putString("name", payment.getTenantName());
        args.putString("room", payment.getRoomNumber());
        args.putDouble("amount", payment.getAmount());
        args.putString("date", payment.getMonth() + " " + payment.getDueDate() + ", " + payment.getYear());
        args.putString("method", payment.getMethod());
        args.putInt("dueDay", payment.getDueDate());
        args.putString("notes", payment.getNotes());
        args.putString("id", payment.getId());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.5f);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String status = getArguments().getString("status");
        int layoutRes = "Paid".equalsIgnoreCase(status) ? R.layout.fragment_payment_paid :
                "Pending".equalsIgnoreCase(status) ? R.layout.fragment_payment_pending :
                        R.layout.fragment_payment_overdue;

        View view = inflater.inflate(layoutRes, container, false);
        view.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;

        // --- Common Views ---
        TextView tvName = view.findViewById(R.id.tvTenantName);
        TextView tvRoom = view.findViewById(R.id.tvRoom);
        TextView tvAmount = view.findViewById(R.id.tvAmount);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvNotes = view.findViewById(R.id.tvNotes);
        View btnClose = view.findViewById(R.id.btnClose);

        if (tvName != null) tvName.setText(args.getString("name"));
        if (tvRoom != null) tvRoom.setText(args.getString("room"));
        if (tvAmount != null) tvAmount.setText("₱" + String.format("%,.2f", args.getDouble("amount")));
        if (tvDate != null) tvDate.setText(args.getString("date"));

        if (tvNotes != null) {
            String notes = args.getString("notes");
            tvNotes.setText((notes != null && !notes.trim().isEmpty()) ? notes : "No additional notes provided.");
        }

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());

        // --- Logic for Pending/Overdue (Record Payment Function) ---
        View btnRecord = view.findViewById(R.id.btnRecordPayment);
        if (btnRecord != null) {
            btnRecord.setOnClickListener(v -> {
                // 1. Prepare the data to send to the next fragment
                Bundle passArgs = new Bundle();
                passArgs.putString("userId", args.getString("userId"));
                passArgs.putString("tenantName", args.getString("name"));
                passArgs.putString("roomNumber", args.getString("room"));
                passArgs.putDouble("amount", args.getDouble("amount"));
                passArgs.putBoolean("isFromDetails", true);

                recordPaymentFragment fragment = new recordPaymentFragment();
                fragment.setArguments(passArgs);

                // 2. THE FIX: Use requireActivity().getSupportFragmentManager()
                // This tells Android to look for 'frameLayout' in the DashboardActivity
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment)
                        .addToBackStack(null)
                        .commit();

                // 3. Close the dialog
                dismiss();
            });
        }

        // --- Optional: Reminder Button ---
        View btnReminder = view.findViewById(R.id.btnSendReminder);
        if (btnReminder != null) {
            btnReminder.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Reminder sent to " + args.getString("name"), Toast.LENGTH_SHORT).show()
            );
        }

        // Paid-specific details
        if ("Paid".equalsIgnoreCase(args.getString("status"))) {
            TextView tvMethod = view.findViewById(R.id.tvMethod);
            TextView tvRef = view.findViewById(R.id.tvRef);
            if (tvMethod != null) tvMethod.setText(args.getString("method", "Cash"));
            if (tvRef != null) tvRef.setText("PAY-" + args.getString("id").substring(0, 8).toUpperCase());
        } else {
            // Days Remaining Logic
            TextView tvRemaining = view.findViewById(R.id.tvRemaining);
            if (tvRemaining != null) {
                int dueDay = args.getInt("dueDay");
                int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int diff = dueDay - currentDay;
                if (diff > 0) tvRemaining.setText(diff + " days");
                else if (diff == 0) tvRemaining.setText("Due Today");
                else tvRemaining.setText(Math.abs(diff) + " days late");
            }
        }
    }
}