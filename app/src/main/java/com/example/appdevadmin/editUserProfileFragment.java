package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class editUserProfileFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private static final String ARG_FULL_NAME = "full_name";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_ROOM_NUMBER = "room_number";
    private static final String ARG_ROOM_ID = "room_id";
    private static final String ARG_STATUS = "status";
    private static final String ARG_RENT_DUE_DATE = "rent_due_date";
    private static final String ARG_EMERGENCY_NAME = "emergency_name";
    private static final String ARG_RELATIONSHIP = "relationship";
    private static final String ARG_EMERGENCY_PHONE = "emergency_phone";

    private FirebaseFirestore db;
    private AutoCompleteTextView spinnerStatus;

    public editUserProfileFragment() {}

    public static editUserProfileFragment newInstance(UserModel user) {
        editUserProfileFragment fragment = new editUserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, user.getUid());
        args.putString(ARG_FULL_NAME, user.getFullName());
        args.putString(ARG_EMAIL, user.getEmail());
        args.putString(ARG_PHONE, user.getPhone());
        args.putString(ARG_ROOM_NUMBER, user.getRoomNumber());
        args.putString(ARG_ROOM_ID, user.getRoomId());
        args.putString(ARG_STATUS, user.getStatus());
        args.putInt(ARG_RENT_DUE_DATE, user.getRentDueDate());
        args.putString(ARG_EMERGENCY_NAME, user.getEmergencyName());
        args.putString(ARG_RELATIONSHIP, user.getRelationship());
        args.putString(ARG_EMERGENCY_PHONE, user.getEmergencyPhone());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        // Map Views
        TextInputEditText etFullName = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditFullName)).getEditText();
        TextInputEditText etEmail = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditEmail)).getEditText();
        TextInputEditText etPhone = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditPhone)).getEditText();
        TextInputEditText etRentDueDate = view.findViewById(R.id.etEditRentDueDate);
        TextInputEditText etEmergencyName = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditEmergencyName)).getEditText();
        TextInputEditText etRelationship = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditRelationship)).getEditText();
        TextInputEditText etEmergencyPhone = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditEmergencyPhone)).getEditText();
        spinnerStatus = view.findViewById(R.id.spinnerEditStatus);

        // Setup Dropdown
        String[] statusOptions = {"Active", "Inactive"};
        spinnerStatus.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, statusOptions));

        // Pre-fill data
        Bundle args = getArguments();
        if (args != null) {
            etFullName.setText(args.getString(ARG_FULL_NAME, ""));
            etEmail.setText(args.getString(ARG_EMAIL, ""));
            etPhone.setText(args.getString(ARG_PHONE, ""));
            etEmergencyName.setText(args.getString(ARG_EMERGENCY_NAME, ""));
            etRelationship.setText(args.getString(ARG_RELATIONSHIP, ""));
            etEmergencyPhone.setText(args.getString(ARG_EMERGENCY_PHONE, ""));
            int due = args.getInt(ARG_RENT_DUE_DATE, 0);
            etRentDueDate.setText(due > 0 ? String.valueOf(due) : "");
            spinnerStatus.setText(args.getString(ARG_STATUS, "Active"), false);
        }

        // Buttons
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        View.OnClickListener saveListener = v -> saveChanges(view, etFullName, etEmail, etPhone, etRentDueDate, etEmergencyName, etRelationship, etEmergencyPhone);
        view.findViewById(R.id.btnSave).setOnClickListener(saveListener);

        // Header Delete Button
        view.findViewById(R.id.btnHeaderDelete).setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Tenant")
                .setMessage("Are you sure? This will delete the profile and set the room to Vacant.")
                .setPositiveButton("Delete", (dialog, which) -> fetchHistoryAndDetailsForDelete())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchHistoryAndDetailsForDelete() {
        Bundle args = getArguments();
        if (args == null) return;

        String uid = args.getString(ARG_UID, "");
        String roomId = args.getString(ARG_ROOM_ID, "");

        // Find history first to finalize it
        db.collection("rooms").document(roomId).collection("roomHistory")
                .whereEqualTo("tenantId", uid)
                .limit(1)
                .get()
                .addOnSuccessListener(historySnapshots -> {
                    String historyDocId = historySnapshots.isEmpty() ? null : historySnapshots.getDocuments().get(0).getId();
                    executeDeleteBatch(uid, roomId, historyDocId);
                });
    }

    private void executeDeleteBatch(String uid, String roomId, String historyDocId) {
        Bundle args = getArguments();
        String fullName = args.getString(ARG_FULL_NAME, "Tenant");
        String roomNumber = args.getString(ARG_ROOM_NUMBER, "N/A");

        WriteBatch batch = db.batch();

        // 1. Delete User Profile
        batch.delete(db.collection("users").document(uid));

        // 2. IMPORTANT: Set Room to Vacant (regardless of previous status to be safe)
        batch.update(db.collection("rooms").document(roomId), "status", "Vacant");

        // 3. Update History to Past
        if (historyDocId != null) {
            batch.update(db.collection("rooms").document(roomId)
                    .collection("roomHistory").document(historyDocId), "status", "Past");
        }

        // 4. Create Activity Logs
        Map<String, Object> log = new HashMap<>();
        log.put("title", "Tenant Deleted: " + fullName);
        log.put("details", " " + roomNumber + " is now Vacant");
        log.put("timestamp", com.google.firebase.Timestamp.now());
        log.put("type", "tenant");
        batch.set(db.collection("activity_logs").document(), log);

        Map<String, Object> roomLog = new HashMap<>();
        roomLog.put("title", " " + roomNumber + " is now Vacant");
        roomLog.put("details", "Tenant removed from system");
        roomLog.put("timestamp", com.google.firebase.Timestamp.now());
        roomLog.put("type", "room");
        batch.set(db.collection("activity_logs").document(), roomLog);

        batch.commit().addOnSuccessListener(unused -> {
            if (isAdded()) {
                Toast.makeText(getContext(), "Account deleted and room vacated", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void saveChanges(View view, TextInputEditText etFullName, TextInputEditText etEmail,
                             TextInputEditText etPhone, TextInputEditText etRentDueDate,
                             TextInputEditText etEmergencyName, TextInputEditText etRelationship,
                             TextInputEditText etEmergencyPhone) {

        Bundle args = getArguments();
        if (args == null) return;

        String uid = args.getString(ARG_UID, "");
        String roomId = args.getString(ARG_ROOM_ID, "");
        String roomNumber = args.getString(ARG_ROOM_NUMBER, "");
        String oldStatus = args.getString(ARG_STATUS, "");
        String newStatus = spinnerStatus.getText().toString();
        String fullName = etFullName.getText().toString().trim();

        view.findViewById(R.id.btnSave).setEnabled(false);

        db.collection("rooms").document(roomId).collection("roomHistory")
                .whereEqualTo("tenantId", uid)
                .limit(1)
                .get()
                .addOnSuccessListener(historySnapshots -> {
                    String historyDocId = historySnapshots.isEmpty() ? null : historySnapshots.getDocuments().get(0).getId();

                    if (!oldStatus.equalsIgnoreCase(newStatus)) {
                        // Status Changed Logic
                        if (newStatus.equalsIgnoreCase("Active")) {
                            // Validation: Room must be vacant to re-activate
                            db.collection("rooms").document(roomId).get().addOnSuccessListener(roomDoc -> {
                                String currentRoomStatus = roomDoc.getString("status");
                                if (currentRoomStatus != null && !currentRoomStatus.equalsIgnoreCase("Vacant")) {
                                    Toast.makeText(getContext(), "Error: Room " + roomNumber + " is Occupied", Toast.LENGTH_LONG).show();
                                    view.findViewById(R.id.btnSave).setEnabled(true);
                                } else {
                                    runFullSyncBatch(uid, roomId, roomNumber, fullName, newStatus, historyDocId, etFullName, etEmail, etPhone, etRentDueDate, etEmergencyName, etRelationship, etEmergencyPhone);
                                }
                            });
                        } else {
                            // Active -> Inactive
                            runFullSyncBatch(uid, roomId, roomNumber, fullName, newStatus, historyDocId, etFullName, etEmail, etPhone, etRentDueDate, etEmergencyName, etRelationship, etEmergencyPhone);
                        }
                    } else {
                        // Simple info update
                        performSimpleUpdateWithHistory(uid, roomId, historyDocId, fullName, etFullName, etEmail, etPhone, etRentDueDate, etEmergencyName, etRelationship, etEmergencyPhone);
                    }
                });
    }

    private void runFullSyncBatch(String uid, String roomId, String roomNumber, String fullName, String newStatus, String historyDocId,
                                  TextInputEditText etFullName, TextInputEditText etEmail, TextInputEditText etPhone,
                                  TextInputEditText etRentDueDate, TextInputEditText etEmergencyName,
                                  TextInputEditText etRelationship, TextInputEditText etEmergencyPhone) {

        WriteBatch batch = db.batch();

        // Update User
        Map<String, Object> u = new HashMap<>();
        u.put("fullName", etFullName.getText().toString().trim());
        u.put("status", newStatus);
        u.put("email", etEmail.getText().toString().trim());
        u.put("phone", etPhone.getText().toString().trim());
        batch.update(db.collection("users").document(uid), u);

        // Update Room & History
        if (newStatus.equalsIgnoreCase("Active")) {
            batch.update(db.collection("rooms").document(roomId), "status", "Occupied");
            if (historyDocId != null) batch.update(db.collection("rooms").document(roomId).collection("roomHistory").document(historyDocId), "status", "Current");
        } else {
            batch.update(db.collection("rooms").document(roomId), "status", "Vacant");
            if (historyDocId != null) batch.update(db.collection("rooms").document(roomId).collection("roomHistory").document(historyDocId), "status", "Past");
        }

        // Logs
        Map<String, Object> log = new HashMap<>();
        log.put("title", "Profile Updated: " + fullName);
        log.put("details", "Status changed to " + newStatus);
        log.put("timestamp", com.google.firebase.Timestamp.now());
        log.put("type", "tenant");
        batch.set(db.collection("activity_logs").document(), log);

        batch.commit().addOnSuccessListener(unused -> {
            if (isAdded()) {
                Toast.makeText(getContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void performSimpleUpdateWithHistory(String uid, String roomId, String historyDocId, String fullName,
                                                TextInputEditText etFullName, TextInputEditText etEmail,
                                                TextInputEditText etPhone, TextInputEditText etRentDueDate,
                                                TextInputEditText etEmergencyName, TextInputEditText etRelationship,
                                                TextInputEditText etEmergencyPhone) {
        WriteBatch batch = db.batch();
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", etFullName.getText().toString().trim());
        updates.put("email", etEmail.getText().toString().trim());
        updates.put("phone", etPhone.getText().toString().trim());
        batch.update(db.collection("users").document(uid), updates);

        if (historyDocId != null) {
            batch.update(db.collection("rooms").document(roomId).collection("roomHistory").document(historyDocId), "tenantName", etFullName.getText().toString().trim());
        }

        batch.commit().addOnSuccessListener(unused -> getParentFragmentManager().popBackStack());
    }
}