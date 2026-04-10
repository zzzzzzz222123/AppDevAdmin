package com.example.appdevadmin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class AddRoomDialogFragment extends DialogFragment {

    private FirebaseFirestore db;
    private Uri imageUri = null;
    private ImageView imgRoomPreview;
    private LinearLayout layoutPlaceholder;

    // Cloudinary Config (Replace with yours)
    private static final String CLOUD_NAME = "doxkv8vmu";
    private static final String UNSIGNED_PRESET = "room_preset";

    // Modern way to pick images
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            imageUri = result.getData().getData();
                            imgRoomPreview.setImageURI(imageUri);
                            imgRoomPreview.setImageTintList(null); // Remove the gray tint
                            layoutPlaceholder.setVisibility(View.GONE);
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_room, container, false);

        db = FirebaseFirestore.getInstance();
        initCloudinary();

        // Initialize Views
        imgRoomPreview = view.findViewById(R.id.imgRoomPreview);
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder);
        MaterialCardView cardSelectImage = view.findViewById(R.id.cardSelectRoomImage);

        TextInputEditText etRoomNumber = view.findViewById(R.id.etRoomNumber);
        TextInputEditText etRent = view.findViewById(R.id.etRent);
        AutoCompleteTextView etRoomType = view.findViewById(R.id.etRoomType);
        AutoCompleteTextView etFloor = view.findViewById(R.id.etFloor);
        AutoCompleteTextView etStatus = view.findViewById(R.id.etStatus);

        // Dropdowns
        setupDropdowns(etRoomType, etFloor, etStatus);

        // Image Selection Logic
        cardSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

        // Submit Logic
        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            validateAndUpload(view, etRoomNumber, etRent, etRoomType, etFloor, etStatus);
        });

        return view;
    }

    private void initCloudinary() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            MediaManager.init(requireContext(), config);
        } catch (Exception ignored) {
            // Already initialized
        }
    }

    private void setupDropdowns(AutoCompleteTextView type, AutoCompleteTextView floor, AutoCompleteTextView status) {
        String[] roomTypes = {"Studio", "1 Bedroom", "2 Bedroom"};
        String[] floors = {"1st Floor", "2nd Floor", "3rd Floor", "4th Floor"};
        String[] statuses = {"Vacant", "Under Maintenance"};

        type.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, roomTypes));
        floor.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, floors));
        status.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, statuses));
    }

    private void validateAndUpload(View view, TextInputEditText etNum, TextInputEditText etRent,
                                   AutoCompleteTextView etType, AutoCompleteTextView etFloor, AutoCompleteTextView etStatus) {

        String roomNum = etNum.getText().toString().trim();
        String rentStr = etRent.getText().toString().trim();

        if (roomNum.isEmpty()) { etNum.setError("Required"); return; }
        if (rentStr.isEmpty()) { etRent.setError("Required"); return; }

        double rent = Double.parseDouble(rentStr);
        view.findViewById(R.id.btnSubmit).setEnabled(false);

        if (imageUri != null) {
            // Upload to Cloudinary first
            MediaManager.get().upload(imageUri)
                    .unsigned(UNSIGNED_PRESET)
                    .callback(new UploadCallback() {
                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String url = (String) resultData.get("secure_url");
                            saveToFirestore(roomNum, etType.getText().toString(), etFloor.getText().toString(), rent, etStatus.getText().toString(), url);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(getContext(), "Upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                            view.findViewById(R.id.btnSubmit).setEnabled(true);
                        }

                        @Override public void onStart(String requestId) {}
                        @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                        @Override public void onReschedule(String requestId, ErrorInfo error) {}
                    }).dispatch();
        } else {
            // Save without image
            saveToFirestore(roomNum, etType.getText().toString(), etFloor.getText().toString(), rent, etStatus.getText().toString(), "");
        }
    }

    private void saveToFirestore(String num, String type, String floor, double rent, String status, String imageUrl) {
        WriteBatch batch = db.batch();
        DocumentReference roomRef = db.collection("rooms").document();
        String roomId = roomRef.getId();

        // 1. Prepare Room Data
        Map<String, Object> room = new HashMap<>();
        room.put("id", roomId);
        room.put("roomNumber", num);
        room.put("roomType", type);
        room.put("floor", floor);
        room.put("monthlyRent", rent);
        room.put("status", status);
        room.put("imageUrl", imageUrl);
        room.put("createdAt", com.google.firebase.Timestamp.now());

        batch.set(roomRef, room);

        // 2. Add to Activity Logs
        Map<String, Object> log = new HashMap<>();
        log.put("title", "New Room Added: " + num);
        log.put("details", floor + " • ₱" + String.format("%,.2f", rent));
        log.put("timestamp", com.google.firebase.Timestamp.now());
        log.put("type", "room");

        batch.set(db.collection("activity_logs").document(), log);

        // 3. Commit
        batch.commit().addOnSuccessListener(unused -> {
            if (isAdded()) {
                Toast.makeText(getContext(), "Room and Logs updated!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}