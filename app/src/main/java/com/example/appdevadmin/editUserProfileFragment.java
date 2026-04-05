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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class editUserProfileFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private static final String ARG_FULL_NAME = "full_name";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_ROOM_NUMBER = "room_number";
    private static final String ARG_ROOM_ID = "room_id"; // Important for room updates
    private static final String ARG_LEASE_START = "lease_start";
    private static final String ARG_LEASE_END = "lease_end";
    private static final String ARG_STATUS = "status";
    private static final String ARG_MONTHLY_RENT = "monthly_rent";
    private static final String ARG_RENT_DUE_DATE = "rent_due_date";
    private static final String ARG_EMERGENCY_NAME = "emergency_name";
    private static final String ARG_RELATIONSHIP = "relationship";
    private static final String ARG_EMERGENCY_PHONE = "emergency_phone";
    private static final String ARG_ROLE = "role";

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
        args.putString(ARG_ROOM_ID, user.getRoomId()); // Passed from UserModel
        args.putString(ARG_LEASE_START, user.getLeaseStart());
        args.putString(ARG_LEASE_END, user.getLeaseEnd());
        args.putString(ARG_STATUS, user.getStatus());
        args.putDouble(ARG_MONTHLY_RENT, user.getMonthlyRent());
        args.putInt(ARG_RENT_DUE_DATE, user.getRentDueDate());
        args.putString(ARG_EMERGENCY_NAME, user.getEmergencyName());
        args.putString(ARG_RELATIONSHIP, user.getRelationship());
        args.putString(ARG_EMERGENCY_PHONE, user.getEmergencyPhone());
        args.putString(ARG_ROLE, user.getRole());
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

        // 1. Map Views
        TextInputEditText etFullName = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditFullName)).getEditText();
        TextInputEditText etEmail = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditEmail)).getEditText();
        TextInputEditText etPhone = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditPhone)).getEditText();
        TextInputEditText etDob = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditDob)).getEditText();

        TextInputEditText etRentDueDate = view.findViewById(R.id.etEditRentDueDate);
        TextInputEditText etEmergencyName = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditEmergencyName)).getEditText();
        TextInputEditText etRelationship = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditRelationship)).getEditText();
        TextInputEditText etEmergencyPhone = (TextInputEditText) ((TextInputLayout) view.findViewById(R.id.etEditEmergencyPhone)).getEditText();

        spinnerStatus = view.findViewById(R.id.spinnerEditStatus);

        // 2. Setup Status Dropdown
        String[] statusOptions = {"Active", "Inactive"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, statusOptions);
        spinnerStatus.setAdapter(adapter);

        // 3. Pre-fill data
        Bundle args = getArguments();
        if (args != null) {
            if (etFullName != null) etFullName.setText(args.getString(ARG_FULL_NAME, ""));
            if (etEmail != null) etEmail.setText(args.getString(ARG_EMAIL, ""));
            if (etPhone != null) etPhone.setText(args.getString(ARG_PHONE, ""));
            if (etEmergencyName != null) etEmergencyName.setText(args.getString(ARG_EMERGENCY_NAME, ""));
            if (etRelationship != null) etRelationship.setText(args.getString(ARG_RELATIONSHIP, ""));
            if (etEmergencyPhone != null) etEmergencyPhone.setText(args.getString(ARG_EMERGENCY_PHONE, ""));

            int due = args.getInt(ARG_RENT_DUE_DATE, 0);
            if (etRentDueDate != null) etRentDueDate.setText(due > 0 ? String.valueOf(due) : "");

            if (spinnerStatus != null) spinnerStatus.setText(args.getString(ARG_STATUS, "Active"), false);
        }

        // 4. Date Picker
        if (etDob != null) {
            etDob.setFocusable(false);
            etDob.setOnClickListener(v -> showDatePicker(etDob));
        }

        // 5. Buttons
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        View.OnClickListener saveListener = v -> saveChanges(view, etFullName, etEmail, etPhone, etRentDueDate, etEmergencyName, etRelationship, etEmergencyPhone);
        view.findViewById(R.id.btnSave).setOnClickListener(saveListener);
        view.findViewById(R.id.btnHeaderSave).setOnClickListener(saveListener);
    }

    private void showDatePicker(TextInputEditText target) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .build();
        datePicker.show(getParentFragmentManager(), "DOB_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            target.setText(sdf.format(selection));
        });
    }

    private void saveChanges(View view, TextInputEditText etFullName, TextInputEditText etEmail,
                             TextInputEditText etPhone, TextInputEditText etRentDueDate,
                             TextInputEditText etEmergencyName, TextInputEditText etRelationship,
                             TextInputEditText etEmergencyPhone) {

        Bundle args = getArguments();
        if (args == null) return;

        String uid = args.getString(ARG_UID, "");
        String roomId = args.getString(ARG_ROOM_ID, ""); // Needed to update room status
        String newStatus = spinnerStatus.getText().toString();

        // Disable UI to prevent multiple clicks
        view.findViewById(R.id.btnSave).setEnabled(false);
        view.findViewById(R.id.btnHeaderSave).setEnabled(false);

        // 1. Prepare User Updates Map
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("fullName", etFullName.getText().toString().trim());
        userUpdates.put("email", etEmail.getText().toString().trim());
        userUpdates.put("phone", etPhone.getText().toString().trim());
        userUpdates.put("status", newStatus);
        userUpdates.put("emergencyName", etEmergencyName.getText().toString().trim());
        userUpdates.put("relationship", etRelationship.getText().toString().trim());
        userUpdates.put("emergencyPhone", etEmergencyPhone.getText().toString().trim());

        try {
            String dueStr = etRentDueDate.getText().toString().trim();
            if (!dueStr.isEmpty()) {
                userUpdates.put("rentDueDate", Integer.parseInt(dueStr));
            }
        } catch (Exception e) { /* Handle parse error if necessary */ }

        // 2. Initialize Firestore Batch
        WriteBatch batch = db.batch();

        // Task A: Update the User document
        batch.update(db.collection("users").document(uid), userUpdates);

        // Task B: If status is Inactive, update the Room document to Vacant
        if ("Inactive".equalsIgnoreCase(newStatus) && roomId != null && !roomId.isEmpty()) {
            batch.update(db.collection("rooms").document(roomId), "status", "Vacant");
        }

        // 3. Commit the batch
        batch.commit().addOnSuccessListener(unused -> {
            if (isAdded()) {
                Toast.makeText(getContext(), "Profile updated and room set to vacant", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        }).addOnFailureListener(e -> {
            if (isAdded()) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                view.findViewById(R.id.btnSave).setEnabled(true);
                view.findViewById(R.id.btnHeaderSave).setEnabled(true);
            }
        });
    }
}