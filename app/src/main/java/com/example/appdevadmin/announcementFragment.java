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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class announcementFragment extends Fragment {

    public announcementFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_announcement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Back Button
        ImageButton btnBack = view.findViewById(R.id.btnBackAnnouncement);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Setup Recipients Dropdown
        String[] recipientOptions = {"All Tenants", "Building A", "Building B", "Floor 1", "Floor 2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, recipientOptions);
        AutoCompleteTextView spinnerRecipients = view.findViewById(R.id.spinnerRecipients);
        if (spinnerRecipients != null) {
            spinnerRecipients.setAdapter(adapter);
        }

        // Send Button Logic
        Button btnSend = view.findViewById(R.id.btnSendNow);
        EditText etSubject = view.findViewById(R.id.etSubject);
        EditText etMessage = view.findViewById(R.id.etMessage);

        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                String subject = etSubject.getText().toString().trim();
                if (subject.isEmpty()) {
                    etSubject.setError("Subject is required");
                    return;
                }
                Toast.makeText(getContext(), "Announcement Sent: " + subject, Toast.LENGTH_SHORT).show();
                etSubject.setText("");
                etMessage.setText("");
            });
        }

        // Schedule Button
        Button btnSchedule = view.findViewById(R.id.btnSchedule);
        if (btnSchedule != null) {
            btnSchedule.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Scheduling feature coming soon!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}