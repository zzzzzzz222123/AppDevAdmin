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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // Date pickers
        setupDatePicker(etDob, "Select Date of Birth");
        setupDatePicker(etLeaseStart, "Select Lease Start");
        setupDatePicker(etLeaseEnd, "Select Lease End");

        // End icon date pickers
        view.findViewById(R.id.layoutDob).setOnClickListener(v -> etDob.performClick());
        view.findViewById(R.id.layoutLeaseStart).setOnClickListener(v -> etLeaseStart.performClick());
        view.findViewById(R.id.layoutLeaseEnd).setOnClickListener(v -> etLeaseEnd.performClick());

        // Role dropdown
        String[] roles = {"Tenant", "Admin"};
        spinnerRole.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, roles));

        // Load vacant rooms from Firestore
        loadVacantRooms();

        // Back / Close buttons
        view.findViewById(R.id.btnBackAddUser).setOnClickListener(v -> requireActivity().onBackPressed());
        view.findViewById(R.id.btnCloseAddUser).setOnClickListener(v -> requireActivity().onBackPressed());
        view.findViewById(R.id.btnCancelAddUser).setOnClickListener(v -> requireActivity().onBackPressed());

        // Save button
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
        db.collection("rooms")
                .whereEqualTo("status", "Vacant")
                .get()
                .addOnSuccessListener(snapshots -> {
                    vacantRooms.clear();
                    List<String> roomNames = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshots) {
                        RoomModel room = new RoomModel(
                                doc.getId(),
                                doc.getString("roomNumber"),
                                doc.getString("roomType"),
                                doc.getString("floor"),
                                doc.getDouble("monthlyRent") != null ? doc.getDouble("monthlyRent") : 0,
                                doc.getString("status")
                        );
                        vacantRooms.add(room);
                        roomNames.add(room.getRoomNumber() + " - " + room.getRoomType());
                    }

                    spinnerRoom.setAdapter(new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, roomNames));

                    // Auto-fill rent when room is selected
                    spinnerRoom.setOnItemClickListener((parent, v, position, id) -> {
                        selectedRoom = vacantRooms.get(position);
                        etRent.setText(String.format("%,.2f", selectedRoom.getMonthlyRent()));
                        etRent.setTextColor(requireContext().getColor(android.R.color.black));
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load rooms: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void saveUser(View view) {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String dob = etDob.getText() != null ? etDob.getText().toString().trim() : "";
        String rentDueDate = etRentDueDate.getText() != null ? etRentDueDate.getText().toString().trim() : "";
        String leaseStart = etLeaseStart.getText() != null ? etLeaseStart.getText().toString().trim() : "";
        String leaseEnd = etLeaseEnd.getText() != null ? etLeaseEnd.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
        String role = spinnerRole.getText().toString().trim();
        String emergencyName = etEmergencyName.getText() != null ? etEmergencyName.getText().toString().trim() : "";
        String relationship = etRelationship.getText() != null ? etRelationship.getText().toString().trim() : "";
        String emergencyPhone = etEmergencyPhone.getText() != null ? etEmergencyPhone.getText().toString().trim() : "";

        // Validation
        if (fullName.isEmpty()) { etFullName.setError("Required"); return; }
        if (email.isEmpty()) { etEmail.setError("Required"); return; }
        if (phone.isEmpty()) { etPhone.setError("Required"); return; }
        if (selectedRoom == null) { Toast.makeText(getContext(), "Please select a room", Toast.LENGTH_SHORT).show(); return; }
        if (rentDueDate.isEmpty()) { etRentDueDate.setError("Required"); return; }
        if (leaseStart.isEmpty()) { etLeaseStart.setError("Required"); return; }
        if (leaseEnd.isEmpty()) { etLeaseEnd.setError("Required"); return; }
        if (password.isEmpty()) { etPassword.setError("Required"); return; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("Passwords do not match"); return; }

        // Validate due date range
        int dueDay;
        try {
            dueDay = Integer.parseInt(rentDueDate);
            if (dueDay < 1 || dueDay > 31) {
                etRentDueDate.setError("Enter a valid day (1-31)");
                return;
            }
        } catch (NumberFormatException e) {
            etRentDueDate.setError("Invalid day");
            return;
        }

        view.findViewById(R.id.btnSaveUser).setEnabled(false);

        // Create Firebase Auth account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Build user document
                    Map<String, Object> user = new HashMap<>();
                    user.put("uid", uid);
                    user.put("fullName", fullName);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("dob", dob);
                    user.put("role", role);
                    user.put("status", "Active");
                    user.put("roomId", selectedRoom.getId());
                    user.put("roomNumber", selectedRoom.getRoomNumber());
                    user.put("monthlyRent", selectedRoom.getMonthlyRent());
                    user.put("rentDueDate", dueDay);
                    user.put("leaseStart", leaseStart);
                    user.put("leaseEnd", leaseEnd);
                    user.put("emergencyName", emergencyName);
                    user.put("relationship", relationship);
                    user.put("emergencyPhone", emergencyPhone);
                    user.put("createdAt", com.google.firebase.Timestamp.now());
                    user.put("updatedAt", com.google.firebase.Timestamp.now());

                    // Save to Firestore
                    db.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                // Update room status to Occupied
                                db.collection("rooms").document(selectedRoom.getId())
                                        .update("status", "Occupied")
                                        .addOnSuccessListener(u -> {
                                            Toast.makeText(getContext(), "User added successfully!", Toast.LENGTH_SHORT).show();
                                            requireActivity().onBackPressed();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                view.findViewById(R.id.btnSaveUser).setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Auth failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    view.findViewById(R.id.btnSaveUser).setEnabled(true);
                });
    }
}