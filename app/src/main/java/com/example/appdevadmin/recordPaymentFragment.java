package com.example.appdevadmin;

import android.app.DatePickerDialog;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class recordPaymentFragment extends Fragment {

    private FirebaseFirestore db;
    private final List<UserModel> tenantList = new ArrayList<>();
    private UserModel selectedTenant = null;

    public recordPaymentFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Views
        AutoCompleteTextView etTenantSearch = view.findViewById(R.id.etTenantSearch);
        TextInputEditText etAmount = view.findViewById(R.id.etAmount);
        TextInputEditText etPaymentDate = view.findViewById(R.id.etPaymentDate);
        AutoCompleteTextView etPaymentMethod = view.findViewById(R.id.etPaymentMethod);
        EditText etNotes = view.findViewById(R.id.etPaymentNotes);

        // Back button
        ImageButton btnBack = view.findViewById(R.id.btnBackRecordPayment);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        // Payment method dropdown
        String[] methods = {"Cash", "GCash", "Bank Transfer", "Check"};
        etPaymentMethod.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, methods));

        // Date picker
        if (etPaymentDate != null) {
            etPaymentDate.setOnClickListener(v -> {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(requireContext(),
                        (dp, year, month, day) -> {
                            String date = (month + 1) + "/" + day + "/" + year;
                            etPaymentDate.setText(date);
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
            });
        }

        // Load tenants from Firestore
        loadTenants(etTenantSearch, etAmount);

        // Cancel
        view.findViewById(R.id.btnCancelPayment).setOnClickListener(v ->
                requireActivity().onBackPressed());

        // Submit
        // Submit
        view.findViewById(R.id.btnSubmitPayment).setOnClickListener(v -> {
            String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
            String paymentDate = etPaymentDate.getText() != null ? etPaymentDate.getText().toString().trim() : "";
            String method = etPaymentMethod.getText().toString().trim();
            String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

            // Validation
            if (selectedTenant == null) {
                Toast.makeText(getContext(), "Please select a tenant", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amountStr.isEmpty()) {
                etAmount.setError("Required");
                return;
            }
            if (paymentDate.isEmpty()) {
                etPaymentDate.setError("Required");
                return;
            }
            if (method.isEmpty()) {
                Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr.replace(",", ""));
            } catch (NumberFormatException e) {
                etAmount.setError("Invalid amount");
                return;
            }

            final double finalAmount = amount;

            view.findViewById(R.id.btnSubmitPayment).setEnabled(false);

            // Get current month and year for metadata
            Calendar cal = Calendar.getInstance();
            String[] monthNames = {"January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};
            String currentMonth = monthNames[cal.get(Calendar.MONTH)];
            int currentYear = cal.get(Calendar.YEAR);

            // Prepare the new payment map
            Map<String, Object> payment = new HashMap<>();
            payment.put("userId", selectedTenant.getUid());
            payment.put("tenantName", selectedTenant.getFullName());
            payment.put("roomId", selectedTenant.getRoomId());
            payment.put("roomNumber", selectedTenant.getRoomNumber());
            payment.put("amount", finalAmount);
            payment.put("month", currentMonth);
            payment.put("year", currentYear);
            payment.put("dueDate", selectedTenant.getRentDueDate());
            payment.put("paymentDate", paymentDate);
            payment.put("method", method);
            payment.put("notes", notes);
            payment.put("status", "Pending"); // Changed from Pending to Paid since it's a recorded payment
            payment.put("paidAt", Timestamp.now());
            payment.put("createdAt", Timestamp.now());

            // ALWAYS CREATE A NEW DOCUMENT
            db.collection("payments")
                    .add(payment)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(getContext(), "New payment recorded!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        view.findViewById(R.id.btnSubmitPayment).setEnabled(true);
                    });
        });
    }

    private void loadTenants(AutoCompleteTextView etTenantSearch, TextInputEditText etAmount) {
        db.collection("users")
                .whereEqualTo("role", "Tenant")
                .get()
                .addOnSuccessListener(snapshots -> {
                    tenantList.clear();
                    List<String> tenantNames = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshots) {
                        UserModel user = new UserModel(
                                doc.getId(),
                                doc.getString("fullName") != null ? doc.getString("fullName") : "",
                                doc.getString("email") != null ? doc.getString("email") : "",
                                doc.getString("phone") != null ? doc.getString("phone") : "",
                                doc.getString("roomNumber") != null ? doc.getString("roomNumber") : "",
                                doc.getString("leaseStart") != null ? doc.getString("leaseStart") : "",
                                doc.getString("leaseEnd") != null ? doc.getString("leaseEnd") : "",
                                doc.getString("status") != null ? doc.getString("status") : "",
                                doc.getDouble("monthlyRent") != null ? doc.getDouble("monthlyRent") : 0,
                                doc.getLong("rentDueDate") != null ? doc.getLong("rentDueDate").intValue() : 0,
                                doc.getString("emergencyName") != null ? doc.getString("emergencyName") : "",
                                doc.getString("relationship") != null ? doc.getString("relationship") : "",
                                doc.getString("emergencyPhone") != null ? doc.getString("emergencyPhone") : "",
                                doc.getString("roomId") != null ? doc.getString("roomId") : "",       // roomId 14th
                                doc.getString("role") != null ? doc.getString("role") : "Tenant"      // role 15th
                        );
                        tenantList.add(user);
                        tenantNames.add(user.getFullName() + " — " + user.getRoomNumber());
                    }

                    etTenantSearch.setAdapter(new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, tenantNames));

                    // Auto-fill amount when tenant is selected
                    etTenantSearch.setOnItemClickListener((parent, v, position, id) -> {
                        selectedTenant = tenantList.get(position);
                        etAmount.setText(String.format("%,.2f", selectedTenant.getMonthlyRent()));
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load tenants: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}