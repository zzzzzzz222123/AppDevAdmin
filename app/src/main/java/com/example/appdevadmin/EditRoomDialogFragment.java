package com.example.appdevadmin;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditRoomDialogFragment extends DialogFragment {

    private static final String ARG_ROOM_ID = "room_id";
    private static final String ARG_ROOM_NUMBER = "room_number";
    private static final String ARG_ROOM_TYPE = "room_type";
    private static final String ARG_FLOOR = "floor";
    private static final String ARG_RENT = "monthly_rent";
    private static final String ARG_STATUS = "status";

    private FirebaseFirestore db;

    public static EditRoomDialogFragment newInstance(RoomModel room) {
        EditRoomDialogFragment fragment = new EditRoomDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROOM_ID, room.getId());
        args.putString(ARG_ROOM_NUMBER, room.getRoomNumber());
        args.putString(ARG_ROOM_TYPE, room.getRoomType());
        args.putString(ARG_FLOOR, room.getFloor());
        args.putDouble(ARG_RENT, room.getMonthlyRent());
        args.putString(ARG_STATUS, room.getStatus());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_room, container, false);

        db = FirebaseFirestore.getInstance();

        // Views
        TextInputEditText etRoomNumber = view.findViewById(R.id.etRoomNumber);
        TextInputEditText etRent = view.findViewById(R.id.etRent);
        AutoCompleteTextView etRoomType = view.findViewById(R.id.etRoomType);
        AutoCompleteTextView etFloor = view.findViewById(R.id.etFloor);
        AutoCompleteTextView etStatus = view.findViewById(R.id.etStatus);

        // Dropdowns
        String[] roomTypes = {"Studio", "1 Bedroom", "2 Bedroom", "3 Bedroom"};
        String[] floors = {"1st Floor", "2nd Floor", "3rd Floor", "4th Floor"};
        String[] statuses = {"Vacant", "Occupied", "Under Maintenance"};

        etRoomType.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, roomTypes));
        etFloor.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, floors));
        etStatus.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, statuses));

        // Pre-fill fields
        Bundle args = getArguments();
        String roomId = "";
        if (args != null) {
            roomId = args.getString(ARG_ROOM_ID);
            etRoomNumber.setText(args.getString(ARG_ROOM_NUMBER));
            etRoomType.setText(args.getString(ARG_ROOM_TYPE), false);
            etFloor.setText(args.getString(ARG_FLOOR), false);
            etRent.setText(String.valueOf(args.getDouble(ARG_RENT)));
            etStatus.setText(args.getString(ARG_STATUS), false);
        }

        final String finalRoomId = roomId;

        // Cancel
        view.findViewById(R.id.btnCancelEdit).setOnClickListener(v -> dismiss());

        // Save
        view.findViewById(R.id.btnSaveChanges).setOnClickListener(v -> {
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
            if (rentStr.isEmpty()) {
                etRent.setError("Rent is required");
                return;
            }

            double rent;
            try {
                rent = Double.parseDouble(rentStr.replace(",", ""));
            } catch (NumberFormatException e) {
                etRent.setError("Invalid rent amount");
                return;
            }

            // Build update map
            Map<String, Object> updates = new HashMap<>();
            updates.put("roomNumber", roomNumber);
            updates.put("roomType", roomType);
            updates.put("floor", floor);
            updates.put("monthlyRent", rent);
            updates.put("status", status);

            view.findViewById(R.id.btnSaveChanges).setEnabled(false);

            // Update Firestore
            db.collection("rooms").document(finalRoomId)
                    .update(updates)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Room updated successfully!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        view.findViewById(R.id.btnSaveChanges).setEnabled(true);
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