package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class announcementFragment extends Fragment {

    private FirebaseFirestore db;
    private EditText etSubject, etMessage;
    private AutoCompleteTextView spinnerRecipients;
    private Button btnSend;
    private TextView tvSentCount;

    private AnnouncementAdapter adapter;
    private List<AnnouncementModel> announcementList;

    public announcementFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_announcement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // 1. Initialize Views
        etSubject = view.findViewById(R.id.etSubject);
        etMessage = view.findViewById(R.id.etMessage);
        spinnerRecipients = view.findViewById(R.id.spinnerRecipients);
        btnSend = view.findViewById(R.id.btnSendNow);
        tvSentCount = view.findViewById(R.id.tvSentCount);
        ImageButton btnBack = view.findViewById(R.id.btnBackAnnouncement);

        // 2. Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerRecentAnnouncements);
        announcementList = new ArrayList<>();
        adapter = new AnnouncementAdapter(announcementList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // 3. Setup Recipients Dropdown
        String[] recipientOptions = {"All Tenants", "1st Floor", "2nd Floor", "3rd Floor", "4th Floor"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, recipientOptions);
        spinnerRecipients.setAdapter(spinnerAdapter);
        spinnerRecipients.setText(recipientOptions[0], false);

        // 4. Listeners
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        btnSend.setOnClickListener(v -> sendAnnouncement());



        // 5. Load Data
        loadRecentAnnouncements();
    }

    private void loadRecentAnnouncements() {
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null && isAdded()) {
                        announcementList.clear();
                        announcementList.addAll(value.toObjects(AnnouncementModel.class));
                        adapter.notifyDataSetChanged();

                        if (tvSentCount != null) {
                            tvSentCount.setText(announcementList.size() + " Sent");
                        }
                    }
                });
    }

    private void sendAnnouncement() {
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();
        String recipient = spinnerRecipients.getText().toString();

        if (subject.isEmpty()) {
            etSubject.setError("Subject is required");
            return;
        }
        if (message.isEmpty()) {
            etMessage.setError("Message is required");
            return;
        }

        btnSend.setEnabled(false);
        btnSend.setText("Sending...");

        // Prepare Data
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", subject);
        announcement.put("content", message);
        announcement.put("recipient", recipient);
        announcement.put("timestamp", Timestamp.now());
        announcement.put("sender", "Management");

        // Save to Firestore
        db.collection("announcements")
                .add(announcement)
                .addOnSuccessListener(documentReference -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Announcement sent!", Toast.LENGTH_SHORT).show();
                        etSubject.setText("");
                        etMessage.setText("");
                        btnSend.setEnabled(true);
                        btnSend.setText("Send Now");
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSend.setEnabled(true);
                        btnSend.setText("Send Now");
                    }
                });
    }
}