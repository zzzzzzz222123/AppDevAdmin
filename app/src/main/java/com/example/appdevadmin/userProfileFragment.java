package com.example.appdevadmin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class userProfileFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private static final String ARG_FULL_NAME = "full_name";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_ROOM_NUMBER = "room_number";
    private static final String ARG_LEASE_START = "lease_start";
    private static final String ARG_LEASE_END = "lease_end";
    private static final String ARG_STATUS = "status";
    private static final String ARG_MONTHLY_RENT = "monthly_rent";
    private static final String ARG_RENT_DUE_DATE = "rent_due_date";
    private static final String ARG_EMERGENCY_NAME = "emergency_name";
    private static final String ARG_RELATIONSHIP = "relationship";
    private static final String ARG_EMERGENCY_PHONE = "emergency_phone";
    private static final String ARG_ROLE = "role";
    private static final String ARG_ROOM_ID = "room_id";

    public userProfileFragment() {}

    public static userProfileFragment newInstance(UserModel user) {
        userProfileFragment fragment = new userProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, user.getUid());
        args.putString(ARG_FULL_NAME, user.getFullName());
        args.putString(ARG_EMAIL, user.getEmail());
        args.putString(ARG_PHONE, user.getPhone());
        args.putString(ARG_ROOM_NUMBER, user.getRoomNumber());
        args.putString(ARG_LEASE_START, user.getLeaseStart());
        args.putString(ARG_LEASE_END, user.getLeaseEnd());
        args.putString(ARG_STATUS, user.getStatus());
        args.putDouble(ARG_MONTHLY_RENT, user.getMonthlyRent());
        args.putInt(ARG_RENT_DUE_DATE, user.getRentDueDate());
        args.putString(ARG_EMERGENCY_NAME, user.getEmergencyName());
        args.putString(ARG_RELATIONSHIP, user.getRelationship());
        args.putString(ARG_EMERGENCY_PHONE, user.getEmergencyPhone());
        args.putString(ARG_ROLE, user.getRole());
        // Inside your userProfileFragment.newInstance(UserModel user)
        args.putString(ARG_ROOM_ID, user.getRoomId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Info grid views
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvUserRole = view.findViewById(R.id.tvUserRole);
        TextView tvActiveBadge = view.findViewById(R.id.tvActiveBadge);
        TextView tvRoomValue = view.findViewById(R.id.tvRoomValue);
        TextView tvRentDueValue = view.findViewById(R.id.tvRentDueValue);
        TextView tvRentDueBadge = view.findViewById(R.id.tvRentDueBadge);
        TextView tvRentingSinceValue = view.findViewById(R.id.tvRentingSinceValue);
        TextView tvRentValue = view.findViewById(R.id.tvRentValue);
        TextView tvLeaseEndsValue = view.findViewById(R.id.tvLeaseEndsValue);

        // Contact views — add IDs to your XML for these
        TextView tvPhoneValue = view.findViewById(R.id.tvPhoneValue);
        TextView tvEmailValue = view.findViewById(R.id.tvEmailValue);
        TextView tvEmergencyValue = view.findViewById(R.id.tvEmergencyValue);

        Bundle args = getArguments();
        if (args != null) {
            String fullName = args.getString(ARG_FULL_NAME, "");
            String status = args.getString(ARG_STATUS, "Active");
            String role = args.getString(ARG_ROLE, "Tenant");
            double rent = args.getDouble(ARG_MONTHLY_RENT, 0);
            int dueDay = args.getInt(ARG_RENT_DUE_DATE, 0);
            String emergencyName = args.getString(ARG_EMERGENCY_NAME, "");
            String emergencyPhone = args.getString(ARG_EMERGENCY_PHONE, "");
            String relationship = args.getString(ARG_RELATIONSHIP, "");

            // Name & role
            if (tvUserName != null) tvUserName.setText(fullName);
            if (tvUserRole != null) tvUserRole.setText(role);

            // Status badge
            if (tvActiveBadge != null) {
                if (status.equalsIgnoreCase("Active")) {
                    tvActiveBadge.setText("✓ Active");
                    tvActiveBadge.setBackgroundResource(R.drawable.badge_occupied);
                    tvActiveBadge.setTextColor(Color.parseColor("#2E7D32"));
                } else {
                    tvActiveBadge.setText("✗ Inactive");
                    tvActiveBadge.setBackgroundResource(R.drawable.badge_vacant);
                    tvActiveBadge.setTextColor(Color.parseColor("#C62828"));
                }
            }

            // Info grid
            if (tvRoomValue != null) tvRoomValue.setText(args.getString(ARG_ROOM_NUMBER, ""));
            if (tvRentingSinceValue != null) tvRentingSinceValue.setText(args.getString(ARG_LEASE_START, ""));
            if (tvLeaseEndsValue != null) tvLeaseEndsValue.setText(args.getString(ARG_LEASE_END, ""));
            if (tvRentValue != null) tvRentValue.setText("₱" + String.format("%,.2f", rent));
            if (tvRentDueValue != null) {
                tvRentDueValue.setText(dueDay > 0 ? "Every " + dueDay + getOrdinal(dueDay) : "—");
            }

            // Rent due badge — hardcoded to Paid for now, update when payments collection is ready
            if (tvRentDueBadge != null) {
                tvRentDueBadge.setText("Paid");
                tvRentDueBadge.setBackgroundResource(R.drawable.badge_occupied);
                tvRentDueBadge.setTextColor(Color.parseColor("#2E7D32"));
            }

            // Contact info
            if (tvPhoneValue != null) tvPhoneValue.setText(args.getString(ARG_PHONE, ""));
            if (tvEmailValue != null) tvEmailValue.setText(args.getString(ARG_EMAIL, ""));
            if (tvEmergencyValue != null) {
                tvEmergencyValue.setText(emergencyName + " — " + emergencyPhone);
            }
        }

        // Back button
        ImageButton btnBack = view.findViewById(R.id.btnBackProfile);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }



        // Edit Profile button
        Button btnEdit = view.findViewById(R.id.btnEditProfile);
        if (btnEdit != null) {
            // Inside userProfileFragment.java, find your btnEdit listener:
            btnEdit.setOnClickListener(v -> {
                if (args != null) {
                    UserModel user = new UserModel(
                            args.getString(ARG_UID),             // 1
                            args.getString(ARG_FULL_NAME),       // 2
                            args.getString(ARG_EMAIL),           // 3
                            args.getString(ARG_PHONE),           // 4
                            args.getString(ARG_ROOM_NUMBER),     // 5
                            args.getString(ARG_LEASE_START),     // 6
                            args.getString(ARG_LEASE_END),       // 7
                            args.getString(ARG_STATUS),          // 8
                            args.getDouble(ARG_MONTHLY_RENT),    // 9
                            args.getInt(ARG_RENT_DUE_DATE),      // 10
                            args.getString(ARG_EMERGENCY_NAME),  // 11
                            args.getString(ARG_RELATIONSHIP),    // 12
                            args.getString(ARG_EMERGENCY_PHONE), // 13
                            args.getString(ARG_ROOM_ID),         // 14
                            args.getString(ARG_ROLE)             // 15
                    );

                    editUserProfileFragment editFragment = editUserProfileFragment.newInstance(user);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, editFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        if (args != null) {
            loadPaymentHistory(args.getString(ARG_UID), view);
        }
    }


    private void loadPaymentHistory(String uid, View view) {
        LinearLayout historyContainer = view.findViewById(R.id.layoutPaymentHistory);
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        db.collection("payments")
                .whereEqualTo("userId", uid)
                .whereEqualTo("type", "Monthly Rent") // <-- ADD THIS LINE
                .limit(4)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyContainer.removeAllViews();
                    if (queryDocumentSnapshots.isEmpty()) {
                        TextView tvEmpty = new TextView(getContext());
                        tvEmpty.setText("No monthly rent records.");
                        tvEmpty.setTextColor(Color.GRAY);
                        historyContainer.addView(tvEmpty);
                        return;
                    }

                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String month = doc.getString("month");
                        Object yearObj = doc.get("year");
                        String year = (yearObj != null) ? yearObj.toString() : "";
                        String status = doc.getString("status");

                        addPaymentRow(historyContainer, month + " " + year, status);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("PAYMENT_DEBUG", "Query Failed: " + e.getMessage());
                    historyContainer.removeAllViews();
                    TextView tvError = new TextView(getContext());
                    tvError.setText("Error loading history. Check Index.");
                    historyContainer.addView(tvError);
                });
    }

    private void addPaymentRow(LinearLayout container, String monthYear, String status) {
        // 1. Create a View for the row (Inflating a simple row layout)
        View row = getLayoutInflater().inflate(R.layout.item_payment_history_row, container, false);

        TextView tvMonth = row.findViewById(R.id.tvHistoryMonth);
        TextView tvStatus = row.findViewById(R.id.tvHistoryStatus);

        tvMonth.setText("● " + monthYear);

        // 2. Style the row based on status
        if ("Paid".equalsIgnoreCase(status)) {
            tvMonth.setTextColor(Color.parseColor("#00C853")); // Green Dot
            tvStatus.setText("Paid");
            tvStatus.setBackgroundResource(R.drawable.badge_occupied);
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else if ("Overdue".equalsIgnoreCase(status)) {
            tvMonth.setTextColor(Color.parseColor("#D32F2F")); // Red Dot
            tvStatus.setText("Overdue");
            tvStatus.setBackgroundResource(R.drawable.badge_overdue);
            tvStatus.setTextColor(Color.parseColor("#C62828"));
        } else {
            tvMonth.setTextColor(Color.parseColor("#FFA000")); // Yellow Dot
            tvStatus.setText("Pending");
            tvStatus.setBackgroundResource(R.drawable.badge_vacant);
            tvStatus.setTextColor(Color.parseColor("#1976D2"));
        }

        container.addView(row);

        // 3. Add a small space/divider between rows
        View space = new View(getContext());
        space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30));
        container.addView(space);
    }

    // Helper to get ordinal suffix (1st, 2nd, 3rd...)
    private String getOrdinal(int day) {
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}