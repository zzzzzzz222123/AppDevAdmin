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

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class addNewUserFragment extends Fragment {

    private TextInputEditText etFullName, etEmail, etPhone, etDob,
            etRent, etRentDueDate, etLeaseStart, etLeaseEnd,
            etPassword, etConfirmPassword,
            etEmergencyName, etRelationship, etEmergencyPhone;

    private AutoCompleteTextView spinnerRoom, spinnerRole;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private final List<RoomModel> vacantRooms = new ArrayList<>();
    private RoomModel selectedRoom = null;

    public addNewUserFragment() {}

    public static addNewUserFragment newInstance() {
        return new addNewUserFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etDob = view.findViewById(R.id.etDob);
        etRent = view.findViewById(R.id.etRent);
        etRentDueDate = view.findViewById(R.id.etRentDueDate);
        etLeaseStart = view.findViewById(R.id.etLeaseStart);
        etLeaseEnd = view.findViewById(R.id.etLeaseEnd);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        etEmergencyName = view.findViewById(R.id.etEmergencyName);
        etRelationship = view.findViewById(R.id.etRelationship);
        etEmergencyPhone = view.findViewById(R.id.etEmergencyPhone);
        spinnerRoom = view.findViewById(R.id.spinnerRoom);
        spinnerRole = view.findViewById(R.id.spinnerRole);

        setupDatePicker(etDob, "Select Date of Birth");
        setupDatePicker(etLeaseStart, "Select Lease Start");
        setupDatePicker(etLeaseEnd, "Select Lease End");

        String[] roles = {"Tenant", "Admin"};
        spinnerRole.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, roles));

        loadVacantRooms();

        view.findViewById(R.id.btnBackAddUser).setOnClickListener(v -> requireActivity().onBackPressed());
        view.findViewById(R.id.btnCancelAddUser).setOnClickListener(v -> requireActivity().onBackPressed());
        view.findViewById(R.id.btnSaveUser).setOnClickListener(v -> saveUser(view));
    }

    private void setupDatePicker(TextInputEditText editText, String title) {
        editText.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(title)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                editText.setText(sdf.format(selection));
            });
        });
    }

    private void loadVacantRooms() {
        db.collection("rooms").whereEqualTo("status", "Vacant").get()
                .addOnSuccessListener(snapshots -> {
                    vacantRooms.clear();
                    List<String> roomNames = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        RoomModel room = new RoomModel(doc.getId(), doc.getString("roomNumber"),
                                doc.getString("roomType"), doc.getString("floor"),
                                doc.getDouble("monthlyRent") != null ? doc.getDouble("monthlyRent") : 0,
                                doc.getString("status"));
                        vacantRooms.add(room);
                        roomNames.add(room.getRoomNumber() + " - " + room.getRoomType());
                    }
                    spinnerRoom.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, roomNames));
                    spinnerRoom.setOnItemClickListener((parent, v, position, id) -> {
                        selectedRoom = vacantRooms.get(position);
                        etRent.setText(String.format("%,.2f", selectedRoom.getMonthlyRent()));
                    });
                });
    }

    private void saveUser(View view) {
        // 1. Extract values
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = spinnerRole.getText().toString().trim();
        String leaseStart = etLeaseStart.getText().toString().trim();
        String leaseEnd = etLeaseEnd.getText().toString().trim();
        String rentDueDate = etRentDueDate.getText().toString().trim();

        // 2. Validations (Same as before)
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Fill in required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (role.equals("Tenant") && selectedRoom == null) {
            Toast.makeText(getContext(), "Select a room first", Toast.LENGTH_SHORT).show();
            return;
        }

        view.findViewById(R.id.btnSaveUser).setEnabled(false);

        // 3. Create Firebase Auth User
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    WriteBatch batch = db.batch();

                    // 4. Main User Document
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", uid);
                    userMap.put("fullName", fullName);
                    userMap.put("email", email);
                    userMap.put("role", role);
                    userMap.put("status", "Active");
                    userMap.put("createdAt", com.google.firebase.Timestamp.now());

                    if (role.equals("Tenant")) {
                        String formattedRent = String.format("₱%,.2f", selectedRoom.getMonthlyRent());

                        userMap.put("roomId", selectedRoom.getId());
                        userMap.put("roomNumber", selectedRoom.getRoomNumber());
                        userMap.put("monthlyRent", selectedRoom.getMonthlyRent());
                        userMap.put("rentDueDate", Integer.parseInt(rentDueDate));
                        userMap.put("leaseStart", leaseStart);
                        userMap.put("leaseEnd", leaseEnd);

                        // 5. Room History Entry
                        Map<String, Object> historyMap = new HashMap<>();
                        historyMap.put("createdAt", com.google.firebase.Timestamp.now());
                        historyMap.put("leaseEnd", leaseEnd);
                        historyMap.put("leaseStart", leaseStart);
                        historyMap.put("status", "Current");
                        historyMap.put("tenantId", uid);
                        historyMap.put("tenantName", fullName);

                        // 6. LOGS -> TenantsLogs
                        // Inside saveUser method in addNewUserFragment...

// 6. LOGS -> Unified Activity Logs
                        Map<String, Object> tenantLog = new HashMap<>();
                        tenantLog.put("title", "New Lease: " + fullName);
                        tenantLog.put("details", selectedRoom.getRoomNumber() + " • " + formattedRent);
                        tenantLog.put("timestamp", com.google.firebase.Timestamp.now());
                        tenantLog.put("type", "tenant"); // TAG AS TENANT

                        Map<String, Object> roomLog = new HashMap<>();
                        roomLog.put("title", "Room " + selectedRoom.getRoomNumber() + " is now Occupied");
                        roomLog.put("details", "Floor " + selectedRoom.getFloor() + " • " + formattedRent);
                        roomLog.put("timestamp", com.google.firebase.Timestamp.now());
                        roomLog.put("type", "room"); // TAG AS ROOM

// ADD TO BATCH
                        batch.set(db.collection("activity_logs").document(), tenantLog);
                        batch.set(db.collection("activity_logs").document(), roomLog);
                        // --- BATCH OPERATIONS ---

                        // Task: Update Room status
                        batch.update(db.collection("rooms").document(selectedRoom.getId()), "status", "Occupied");

                        // Task: Add Room History
                        batch.set(db.collection("rooms").document(selectedRoom.getId())
                                .collection("roomHistory").document(), historyMap);


                    }

                    // Task: Save User Profile
                    batch.set(db.collection("users").document(uid), userMap);

                    // 8. Commit All
                    batch.commit().addOnSuccessListener(unused -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "User created and logs updated", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        }
                    }).addOnFailureListener(e -> {
                        view.findViewById(R.id.btnSaveUser).setEnabled(true);
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    view.findViewById(R.id.btnSaveUser).setEnabled(true);
                    Toast.makeText(getContext(), "Auth Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}