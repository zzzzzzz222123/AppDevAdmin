package com.example.appdevadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class recordPaymentFragment extends Fragment {

    private FirebaseFirestore db;
    private final List<UserModel> tenantList = new ArrayList<>();
    private UserModel selectedTenant = null;
    private boolean isInvoiceMode = false;

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

        // 1. Get Arguments
        if (getArguments() != null) {
            isInvoiceMode = getArguments().getBoolean("isInvoice", false);
        }

        // 2. Initialize Views
        AutoCompleteTextView etTenantSearch = view.findViewById(R.id.etTenantSearch);
        TextInputEditText etAmount = view.findViewById(R.id.etAmount);
        TextInputEditText etExtraFees = view.findViewById(R.id.etExtraFees);
        TextInputEditText etPaymentDate = view.findViewById(R.id.etPaymentDate);
        AutoCompleteTextView etPaymentMethod = view.findViewById(R.id.etPaymentMethod);
        EditText etNotes = view.findViewById(R.id.etPaymentNotes);

        // 3. Mode Specific UI Changes
        if (isInvoiceMode) {
            // Hide Method
            view.findViewById(R.id.lblMethod).setVisibility(View.GONE);
            view.findViewById(R.id.layoutMethod).setVisibility(View.GONE);
            // Hide Extra Fees on Invoice
            view.findViewById(R.id.lblExtraFees).setVisibility(View.GONE);
            view.findViewById(R.id.layoutExtraFees).setVisibility(View.GONE);

            ((TextView) view.findViewById(R.id.lblPaymentDate)).setText("Payment Due Date *");
            ((TextView) view.findViewById(R.id.tvRecordPaymentTitle)).setText("Create New Invoice");
        }

        // 4. Handle "From Details" (Lock Tenant Selection)
        if (getArguments() != null && getArguments().getBoolean("isFromDetails", false)) {
            String name = getArguments().getString("tenantName");
            String room = getArguments().getString("roomNumber");

            etTenantSearch.setText(name + " — " + room);
            etAmount.setText(String.format("%.2f", getArguments().getDouble("amount")));

            // Lock the selection
            etTenantSearch.setEnabled(false);
            etTenantSearch.setFocusable(false);
            view.findViewById(R.id.layoutTenantSearch).setEnabled(false);

            selectedTenant = new UserModel();
            selectedTenant.setUid(getArguments().getString("userId"));
            selectedTenant.setFullName(name);
            selectedTenant.setRoomNumber(room);
            selectedTenant.setRoomId(getArguments().getString("roomId"));
        }

        // Dropdowns & Pickers
        setupDropdowns(etPaymentMethod, etPaymentDate);

        // Back/Cancel
        view.findViewById(R.id.btnBackRecordPayment).setOnClickListener(v -> requireActivity().onBackPressed());
        view.findViewById(R.id.btnCancelPayment).setOnClickListener(v -> requireActivity().onBackPressed());

        // 5. Submit Logic
        view.findViewById(R.id.btnSubmitPayment).setOnClickListener(v -> {
            submitPayment(view, etAmount, etExtraFees, etPaymentDate, etPaymentMethod, etNotes);
        });

        loadTenants(etTenantSearch, etAmount);
    }

    private void submitPayment(View view, TextInputEditText etAmount, TextInputEditText etExtra,
                               TextInputEditText etDate, AutoCompleteTextView etMethod, EditText etNotes) {

        String amountStr = etAmount.getText().toString().trim();
        String extraStr = etExtra.getText().toString().trim();
        String dateVal = etDate.getText().toString().trim();
        String method = etMethod.getText().toString().trim();

        if (selectedTenant == null) { Toast.makeText(getContext(), "Select a tenant", Toast.LENGTH_SHORT).show(); return; }
        if (amountStr.isEmpty() || dateVal.isEmpty()) { Toast.makeText(getContext(), "Fill required fields", Toast.LENGTH_SHORT).show(); return; }

        double baseAmount = Double.parseDouble(amountStr.replace(",", ""));
        double extraAmount = isInvoiceMode ? 0 : (extraStr.isEmpty() ? 0 : Double.parseDouble(extraStr.replace(",", "")));
        double totalAmount = baseAmount + extraAmount;

        view.findViewById(R.id.btnSubmitPayment).setEnabled(false);

        // Date Info
        Calendar cal = Calendar.getInstance();
        String currentMonth = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"}[cal.get(Calendar.MONTH)];

        WriteBatch batch = db.batch();

        // Task A: Create Payment Document
        Map<String, Object> p = new HashMap<>();
        p.put("userId", selectedTenant.getUid());
        p.put("tenantName", selectedTenant.getFullName());
        p.put("roomNumber", selectedTenant.getRoomNumber());
        p.put("amount", totalAmount);
        p.put("month", currentMonth);
        p.put("year", cal.get(Calendar.YEAR));
        p.put("status", isInvoiceMode ? "Pending" : "Paid");
        p.put("type", isInvoiceMode ? "Invoice" : "Monthly Rent");
        p.put("method", isInvoiceMode ? "N/A" : method);
        p.put("paymentDate", isInvoiceMode ? null : dateVal);
        p.put("dueDate", isInvoiceMode ? dateVal : null);
        p.put("notes", etNotes.getText().toString().trim());
        p.put("createdAt", Timestamp.now());

        batch.set(db.collection("payments").document(), p);

        // Task B: Add to Activity Logs
        Map<String, Object> log = new HashMap<>();
        String logTitle = isInvoiceMode ? "New Invoice: " : "Payment Paid: ";
        log.put("title", logTitle + selectedTenant.getRoomNumber() + " " + selectedTenant.getFullName());
        log.put("details", String.format("₱%,.2f — ", totalAmount) + (isInvoiceMode ? "Invoice" : "Monthly Rent"));
        log.put("timestamp", Timestamp.now());
        log.put("type", "payment");

        batch.set(db.collection("activity_logs").document(), log);

        // Commit Batch
        batch.commit().addOnSuccessListener(unused -> {
            Toast.makeText(getContext(), "Recorded successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }).addOnFailureListener(e -> {
            view.findViewById(R.id.btnSubmitPayment).setEnabled(true);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupDropdowns(AutoCompleteTextView method, TextInputEditText date) {
        String[] methods = {"Cash", "GCash", "Bank Transfer", "Check"};
        method.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, methods));

        date.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (dp, y, m, d) -> date.setText((m + 1) + "/" + d + "/" + y),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void loadTenants(AutoCompleteTextView etSearch, TextInputEditText etAmount) {
        if (!etSearch.isEnabled()) return; // Don't load if locked

        db.collection("users").whereEqualTo("role", "Tenant").get().addOnSuccessListener(snapshots -> {
            tenantList.clear();
            List<String> names = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                UserModel user = doc.toObject(UserModel.class);
                user.setUid(doc.getId());
                tenantList.add(user);
                names.add(user.getFullName() + " — " + user.getRoomNumber());
            }
            etSearch.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names));
            etSearch.setOnItemClickListener((parent, v, pos, id) -> {
                selectedTenant = tenantList.get(pos);
                etAmount.setText(String.format("%.2f", selectedTenant.getMonthlyRent()));
            });
        });
    }
}