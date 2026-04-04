package com.example.appdevadmin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddRoomDialogFragment extends DialogFragment {

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_room, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Input fields
        TextInputEditText etRoomNumber = view.findViewById(R.id.etRoomNumber);
        TextInputEditText etRent = view.findViewById(R.id.etRent);
        AutoCompleteTextView etRoomType = view.findViewById(R.id.etRoomType);
        AutoCompleteTextView etFloor = view.findViewById(R.id.etFloor);
        AutoCompleteTextView etStatus = view.findViewById(R.id.etStatus);

        // Dropdown adapters
        String[] roomTypes = {"Studio", "1 Bedroom", "2 Bedroom", "3 Bedroom"};
        String[] floors = {"1st Floor", "2nd Floor", "3rd Floor", "4th Floor"};
        String[] statuses = {"Vacant", "Occupied", "Under Maintenance"};

        etRoomType.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, roomTypes));
        etFloor.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, floors));
        etStatus.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, statuses));

        // Close logic
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

        // Submit logic
        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String roomNumber = etRoomNumber.getText() != null ? etRoomNumber.getText().toString().trim() : "";
            String roomType = etRoomType.getText().toString().trim();
            String floor = etFloor.getText().toString().trim();
            String rentStr = etRent.getText() != null ? etRent.getText().toString().trim() : "";
            String status = etStatus.getText().toString().trim();

            // Validation
            if (roomNumber.isEmpty()) {
                etRoomNumber.setError("Room number is required");
                return;
            }
            if (rentStr.isEmpty() || rentStr.equals("0.00")) {
                etRent.setError("Please enter a valid rent amount");
                return;
            }

            double rent;
            try {
                rent = Double.parseDouble(rentStr);
            } catch (NumberFormatException e) {
                etRent.setError("Invalid rent amount");
                return;
            }

            // Build Firestore document
            Map<String, Object> room = new HashMap<>();
            room.put("roomNumber", roomNumber);
            room.put("roomType", roomType);
            room.put("floor", floor);
            room.put("monthlyRent", rent);
            room.put("status", status);
            room.put("createdAt", com.google.firebase.Timestamp.now());

            // Disable button to prevent double submit
            view.findViewById(R.id.btnSubmit).setEnabled(false);

            // Save to Firestore under "rooms" collection
            db.collection("rooms")
                    .add(room)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Room added successfully!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        view.findViewById(R.id.btnSubmit).setEnabled(true);
                    });
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}