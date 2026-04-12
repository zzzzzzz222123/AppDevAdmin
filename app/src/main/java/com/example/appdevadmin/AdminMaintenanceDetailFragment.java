package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminMaintenanceDetailFragment extends BottomSheetDialogFragment {

    private static final String ARG_REQUEST_ID = "request_id";

    // ── Factory ────────────────────────────────────────────────────────────
    public static AdminMaintenanceDetailFragment newInstance(String requestId) {
        AdminMaintenanceDetailFragment f = new AdminMaintenanceDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REQUEST_ID, requestId);
        f.setArguments(args);
        return f;
    }

    // ── UI ─────────────────────────────────────────────────────────────────
    private TextView tvTitle, tvUnit, tvCategory, tvPriority,
                     tvDate, tvDescription, tvCurrentStatus;
    private TextInputEditText etAdminNotes;
    private MaterialButton btnMarkPending, btnMarkOngoing, btnMarkCompleted, btnSave;
    private LinearLayout llPhotos;

    // ── State ──────────────────────────────────────────────────────────────
    private String requestId;
    private String selectedStatus;
    private FirebaseFirestore db;

    // ──────────────────────────────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        requestId = getArguments() != null ? getArguments().getString(ARG_REQUEST_ID) : null;

        bindViews(view);
        setupStatusButtons();
        setupSaveButton();
        loadRequest();
    }

    // ── Bind ───────────────────────────────────────────────────────────────
    private void bindViews(View view) {
        tvTitle         = view.findViewById(R.id.tvDetailTitle);
        tvUnit          = view.findViewById(R.id.tvDetailUnit);
        tvCategory      = view.findViewById(R.id.tvDetailCategory);
        tvPriority      = view.findViewById(R.id.tvDetailPriority);
        tvDate          = view.findViewById(R.id.tvDetailDate);
        tvDescription   = view.findViewById(R.id.tvDetailDescription);
        tvCurrentStatus = view.findViewById(R.id.tvDetailCurrentStatus);
        etAdminNotes    = view.findViewById(R.id.etAdminNotes);
        btnMarkPending  = view.findViewById(R.id.btnMarkPending);
        btnMarkOngoing  = view.findViewById(R.id.btnMarkOngoing);
        btnMarkCompleted = view.findViewById(R.id.btnMarkCompleted);
        btnSave         = view.findViewById(R.id.btnSaveStatus);
        llPhotos        = view.findViewById(R.id.llPhotos);
    }

    // ── Load from Firestore ────────────────────────────────────────────────
    private void loadRequest() {
        if (requestId == null) return;

        db.collection("maintenance_requests").document(requestId)
            .get()
            .addOnSuccessListener(doc -> {
                if (!doc.exists()) return;
                AdminMaintenanceRequest req = doc.toObject(AdminMaintenanceRequest.class);
                if (req == null) return;
                req.id = doc.getId();

                selectedStatus = req.status;

                tvTitle.setText(req.title != null ? req.title : "—");
                tvUnit.setText(req.unit != null ? req.unit : "—");
                tvCategory.setText("Category: " + (req.category != null ? req.category : "—"));
                tvPriority.setText("Priority: " + (req.priority != null ? req.priority : "—"));
                tvDate.setText(req.formattedDate());
                tvDescription.setText(req.description != null && !req.description.isEmpty()
                    ? req.description : "No description provided.");
                tvCurrentStatus.setText(capitalize(req.status));
                applyStatusColor(tvCurrentStatus, req.status);

                if (req.adminNotes != null) etAdminNotes.setText(req.adminNotes);

                highlightSelectedButton(selectedStatus);
                loadPhotos(req.photoUrls);
            })
            .addOnFailureListener(e ->
                Toast.makeText(requireContext(), "Failed to load request", Toast.LENGTH_SHORT).show());
    }

    // ── Photos ─────────────────────────────────────────────────────────────
    private void loadPhotos(List<String> urls) {
        if (llPhotos == null || urls == null || urls.isEmpty()) return;
        llPhotos.removeAllViews();

        for (String url : urls) {
            ImageView iv = new ImageView(requireContext());
            int size = (int) (120 * requireContext().getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMarginEnd(8);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setClipToOutline(true);
            Picasso.get().load(url).into(iv);
            llPhotos.addView(iv);
        }
    }

    // ── Status buttons ─────────────────────────────────────────────────────
    private void setupStatusButtons() {
        btnMarkPending.setOnClickListener(v  -> selectStatus("pending"));
        btnMarkOngoing.setOnClickListener(v  -> selectStatus("ongoing"));
        btnMarkCompleted.setOnClickListener(v -> selectStatus("completed"));
    }

    private void selectStatus(String status) {
        selectedStatus = status;
        highlightSelectedButton(status);
    }

    private void highlightSelectedButton(String status) {
        // Reset all
        btnMarkPending.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(0xFFF5F5F5));
        btnMarkOngoing.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(0xFFF5F5F5));
        btnMarkCompleted.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(0xFFF5F5F5));
        btnMarkPending.setTextColor(0xFF757575);
        btnMarkOngoing.setTextColor(0xFF757575);
        btnMarkCompleted.setTextColor(0xFF757575);

        // Highlight active
        if (status == null) return;
        switch (status) {
            case "pending":
                btnMarkPending.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFFFF3E0));
                btnMarkPending.setTextColor(0xFFEF6C00);
                break;
            case "ongoing":
                btnMarkOngoing.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFE3F2FD));
                btnMarkOngoing.setTextColor(0xFF1976D2);
                break;
            case "completed":
                btnMarkCompleted.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFE8F5E9));
                btnMarkCompleted.setTextColor(0xFF2E7D32);
                break;
        }
    }

    // ── Save ───────────────────────────────────────────────────────────────
    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (requestId == null) return;
            String notes = etAdminNotes.getText() != null
                ? etAdminNotes.getText().toString().trim() : "";

            btnSave.setEnabled(false);
            btnSave.setText("Saving…");

            db.collection("maintenance_requests").document(requestId)
                .update(
                    "status",     selectedStatus,
                    "adminNotes", notes,
                    "updatedAt",  Timestamp.now()
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(),
                        "Request updated successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Save changes");
                    Toast.makeText(requireContext(),
                        "Failed to update: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
        });
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "Pending";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void applyStatusColor(TextView tv, String status) {
        if (status == null) return;
        switch (status) {
            case "ongoing":
                tv.setTextColor(0xFF1976D2);
                tv.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFE3F2FD));
                break;
            case "completed":
                tv.setTextColor(0xFF2E7D32);
                tv.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFE8F5E9));
                break;
            default:
                tv.setTextColor(0xFFEF6C00);
                tv.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFFFF3E0));
                break;
        }
    }
}
