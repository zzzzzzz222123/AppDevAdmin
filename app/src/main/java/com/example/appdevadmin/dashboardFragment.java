package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class dashboardFragment extends Fragment {

    private FirebaseFirestore db;

    // Main Values
    private TextView valIncome, valPending, valOccupancy;
    // Subtexts
    private TextView valIncomeSub, valPendingSub, valOccupancySub;
    private RecyclerView recyclerView;
    private ActivityLogAdapter adapter;
    private List<ActivityLogModel> logList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Initialize Main Value Views
        valIncome = view.findViewById(R.id.valIncome);
        valPending = view.findViewById(R.id.valPending);
        valOccupancy = view.findViewById(R.id.valOccupancy);

        // Initialize Subtext Views
        valIncomeSub = view.findViewById(R.id.valIncomeSub);
        valPendingSub = view.findViewById(R.id.valPendingSub);
        valOccupancySub = view.findViewById(R.id.valOccupancySub);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerRecentActivity);
        logList = new ArrayList<>();
        adapter = new ActivityLogAdapter(logList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Inside onViewCreated in dashboardFragment.java
        view.findViewById(R.id.txtViewAll).setOnClickListener(v -> {
            ActivityLogsFragment logFragment = new ActivityLogsFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, logFragment) // Ensure this ID matches your DashboardActivity FrameLayout
                    .addToBackStack(null)
                    .commit();
        });

        loadIncomeData();
        loadPendingData();
        loadOccupancyData();

        loadRecentActivity();

    }

    private void loadIncomeData() {
        db.collection("payments")
                .whereEqualTo("status", "Paid")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    double totalIncome = 0;
                    for (DocumentSnapshot doc : snapshots) {
                        Double amount = doc.getDouble("amount");
                        if (amount != null) totalIncome += amount;
                    }
                    valIncome.setText(String.format(Locale.getDefault(), "₱%,.0f", totalIncome));

                    // Optional: You can change "this month" to show the current month name
                    if(valIncomeSub != null) valIncomeSub.setText("Total collected");
                });
    }

    private void loadPendingData() {
        db.collection("payments")
                .whereEqualTo("status", "Pending")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    int count = snapshots.size();
                    double outstandingAmount = 0;

                    for (DocumentSnapshot doc : snapshots) {
                        Double amount = doc.getDouble("amount");
                        if (amount != null) outstandingAmount += amount;
                    }

                    valPending.setText(String.valueOf(count));

                    // Update Subtext with total amount of pending money
                    if (valPendingSub != null) {
                        valPendingSub.setText(String.format(Locale.getDefault(), "₱%,.0f outstanding", outstandingAmount));
                    }
                });
    }

    private void loadOccupancyData() {
        db.collection("rooms")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    int totalRooms = snapshots.size();
                    int occupiedRooms = 0;

                    for (DocumentSnapshot doc : snapshots) {
                        String status = doc.getString("status");
                        if ("Occupied".equalsIgnoreCase(status)) {
                            occupiedRooms++;
                        }
                    }

                    if (totalRooms > 0) {
                        double rate = ((double) occupiedRooms / totalRooms) * 100;
                        valOccupancy.setText(String.format(Locale.getDefault(), "%.0f%%", rate));

                        // Update Subtext with fraction (e.g., "17/20 units")
                        if (valOccupancySub != null) {
                            valOccupancySub.setText(occupiedRooms + "/" + totalRooms + " units");
                        }
                    } else {
                        valOccupancy.setText("0%");
                        if (valOccupancySub != null) valOccupancySub.setText("No rooms found");
                    }
                });
    }

    private void loadRecentActivity() {
        db.collection("activity_logs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(4) // Fetch exactly last 6
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        android.util.Log.e("DB_ERROR", error.getMessage());
                        return;
                    }

                    if (value != null) {
                        logList.clear();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            // Manually Map or use toObject
                            ActivityLogModel log = doc.toObject(ActivityLogModel.class);
                            logList.add(log);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}