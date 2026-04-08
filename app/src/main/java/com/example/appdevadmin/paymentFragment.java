package com.example.appdevadmin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class paymentFragment extends Fragment {

    private TextView btnFilterAll, btnFilterPaid, btnFilterPending, btnFilterOverdue;
    private RecyclerView recyclerPayments;
    private PaymentAdapter adapter;
    private final List<PaymentModel> allPayments = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentFilter = "all";

    // Stats TextViews
    private TextView tvCollectedAmount, tvPendingAmount, tvOverdueAmount;

    public paymentFragment() {}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        tvCollectedAmount = view.findViewById(R.id.tvCollectedAmount);
        tvPendingAmount = view.findViewById(R.id.tvPendingAmount);
        tvOverdueAmount = view.findViewById(R.id.tvOverdueAmount);
        // Filter buttons
        btnFilterAll = view.findViewById(R.id.btnPayFilterAll);
        btnFilterPaid = view.findViewById(R.id.btnPayFilterPaid);
        btnFilterPending = view.findViewById(R.id.btnPayFilterPending);
        btnFilterOverdue = view.findViewById(R.id.btnPayFilterOverdue);

        // RecyclerView
        recyclerPayments = view.findViewById(R.id.recyclerPayments);
        // Inside onViewCreated in paymentFragment.java
        adapter = new PaymentAdapter(requireContext(), new ArrayList<>(), payment -> {
            // This code runs when a row is clicked
            PaymentDetailDialog dialog = PaymentDetailDialog.newInstance(payment);
            dialog.show(getChildFragmentManager(), "PaymentDetail");
        });
        recyclerPayments.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerPayments.setAdapter(adapter);

        // Default filter
        btnFilterAll.setSelected(true);

        // Filter listeners
        btnFilterAll.setOnClickListener(v -> updateFilter("all"));
        btnFilterPaid.setOnClickListener(v -> updateFilter("Paid"));
        btnFilterPending.setOnClickListener(v -> updateFilter("Pending"));
        btnFilterOverdue.setOnClickListener(v -> updateFilter("Overdue"));

        // Add Invoice button
        view.findViewById(R.id.btnRecordPayment).setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new recordPaymentFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Load payments
        loadPayments();
    }

    private void loadPayments() {
        db.collection("payments")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    allPayments.clear();
                    double totalCollected = 0;
                    double totalPending = 0;
                    double totalOverdue = 0;

                    for (QueryDocumentSnapshot doc : snapshots) {
                        PaymentModel payment = new PaymentModel(
                                doc.getId(),
                                doc.getString("userId") != null ? doc.getString("userId") : "",
                                doc.getString("tenantName") != null ? doc.getString("tenantName") : "",
                                doc.getString("roomNumber") != null ? doc.getString("roomNumber") : "",
                                doc.getDouble("amount") != null ? doc.getDouble("amount") : 0,
                                doc.getString("month") != null ? doc.getString("month") : "",
                                (doc.get("year") instanceof Long) ? ((Long) doc.get("year")).intValue() : 0,
                                (doc.get("dueDate") instanceof Long) ? ((Long) doc.get("dueDate")).intValue() : 0,
                                doc.getString("paymentDate") != null ? doc.getString("paymentDate") : "",
                                doc.getString("method") != null ? doc.getString("method") : "",
                                doc.getString("notes") != null ? doc.getString("notes") : "",
                                doc.getString("status") != null ? doc.getString("status") : "",
                                doc.getString("type") != null ? doc.getString("type") : "Invoice"
                        );

                        allPayments.add(payment);

                        // Tally stats based on status (matching your filter strings)
                        String status = payment.getStatus();
                        if ("Paid".equalsIgnoreCase(status)) {
                            totalCollected += payment.getAmount();
                        } else if ("Pending".equalsIgnoreCase(status)) {
                            totalPending += payment.getAmount();
                        } else if ("Overdue".equalsIgnoreCase(status)) {
                            totalOverdue += payment.getAmount();
                        }
                    }

                    updateStats(totalCollected, totalPending, totalOverdue);
                    applyFilter();
                });
    }

    private void updateStats(double collected, double pending, double overdue) {
        // Formatting with commas and currency symbol
        if (tvCollectedAmount != null)
            tvCollectedAmount.setText("₱" + String.format("%,.0f", collected));

        if (tvPendingAmount != null)
            tvPendingAmount.setText("₱" + String.format("%,.0f", pending));

        if (tvOverdueAmount != null)
            tvOverdueAmount.setText("₱" + String.format("%,.0f", overdue));
    }

    private void updateFilter(String filter) {
        currentFilter = filter;
        btnFilterAll.setSelected(filter.equals("all"));
        btnFilterPaid.setSelected(filter.equals("Paid"));
        btnFilterPending.setSelected(filter.equals("Pending"));
        btnFilterOverdue.setSelected(filter.equals("Overdue"));
        applyFilter();
    }

    private void applyFilter() {
        if (currentFilter.equals("all")) {
            adapter.updateList(new ArrayList<>(allPayments));
            return;
        }
        List<PaymentModel> filtered = new ArrayList<>();
        for (PaymentModel p : allPayments) {
            if (p.getStatus().equalsIgnoreCase(currentFilter)) {
                filtered.add(p);
            }
        }
        adapter.updateList(filtered);
    }
}