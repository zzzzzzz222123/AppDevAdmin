package com.example.appdevadmin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RenterHistoryDialog extends DialogFragment {

    private String roomNumber;
    private FirebaseFirestore db;
    private RenterHistoryAdapter adapter;
    private final List<RenterHistoryModel> historyList = new ArrayList<>();
    private LinearLayout layoutEmpty;

    public static RenterHistoryDialog newInstance(String roomNumber) {
        RenterHistoryDialog fragment = new RenterHistoryDialog();
        Bundle args = new Bundle();
        args.putString("room_number", roomNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        android.app.Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_renter_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (getArguments() != null) {
            roomNumber = getArguments().getString("room_number");
            ((TextView) view.findViewById(R.id.tvRoomSubtitle)).setText(roomNumber);
        }

        view.findViewById(R.id.btnCloseHistory).setOnClickListener(v -> dismiss());
        layoutEmpty = view.findViewById(R.id.layoutEmptyHistory);

        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerRenterHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RenterHistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        // Step 1: Find the Room Document ID using the roomNumber string
        db.collection("rooms")
                .whereEqualTo("roomNumber", roomNumber)
                .limit(1)
                .get()
                .addOnSuccessListener(roomSnapshots -> {
                    if (roomSnapshots.isEmpty()) {
                        Log.d("HISTORY_DEBUG", "Room not found: " + roomNumber);
                        layoutEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Get the unique ID of the room document
                    String roomId = roomSnapshots.getDocuments().get(0).getId();

                    // Step 2: Query the sub-collection "roomHistory" inside that room
                    db.collection("rooms")
                            .document(roomId)
                            .collection("roomHistory")
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(historySnapshots -> {
                                historyList.clear();
                                if (historySnapshots.isEmpty()) {
                                    layoutEmpty.setVisibility(View.VISIBLE);
                                } else {
                                    layoutEmpty.setVisibility(View.GONE);
                                    for (QueryDocumentSnapshot doc : historySnapshots) {
                                        // Extract the exact fields you saved earlier
                                        String name = doc.getString("tenantName");
                                        String start = doc.getString("leaseStart");
                                        String end = doc.getString("leaseEnd");
                                        String status = doc.getString("status");

                                        // Format the date range display
                                        String dateRange = (start != null ? start : "N/A") + " - " + (end != null ? end : "Present");

                                        // Add to list (Assuming RenterHistoryModel constructor: Name, Subtitle, Status)
                                        historyList.add(new RenterHistoryModel(
                                                name != null ? name : "Unknown Tenant",
                                                dateRange,
                                                status != null ? status : "Past"
                                        ));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("HISTORY_DEBUG", "Error fetching history: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("HISTORY_DEBUG", "Error finding room: " + e.getMessage());
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}