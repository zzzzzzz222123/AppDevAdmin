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
import java.util.Calendar;

public class recordPaymentFragment extends Fragment {

    public recordPaymentFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Back Button
        ImageButton btnBack = view.findViewById(R.id.btnBackRecordPayment);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Setup Tenant Dropdown (Mock Data)
        String[] tenants = {"Maria Santos", "Juan Reyes", "Ana Cruz", "Roberto Lim", "Carmen Garcia"};
        ArrayAdapter<String> tenantAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tenants);
        AutoCompleteTextView etTenantSearch = view.findViewById(R.id.etTenantSearch);
        if (etTenantSearch != null) {
            etTenantSearch.setAdapter(tenantAdapter);
        }

        // Setup Payment Method Dropdown
        String[] methods = {"Cash", "GCash", "Bank Transfer", "Check"};
        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, methods);
        AutoCompleteTextView etPaymentMethod = view.findViewById(R.id.etPaymentMethod);
        if (etPaymentMethod != null) {
            etPaymentMethod.setAdapter(methodAdapter);
        }

        // Date Picker logic
        EditText etPaymentDate = view.findViewById(R.id.etPaymentDate);
        if (etPaymentDate != null) {
            etPaymentDate.setOnClickListener(v -> {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        (view1, year1, monthOfYear, dayOfMonth) -> {
                            String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                            etPaymentDate.setText(date);
                        }, year, month, day);
                datePickerDialog.show();
            });
        }

        // Submit Button Logic
        Button btnSubmit = view.findViewById(R.id.btnSubmitPayment);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Payment Recorded Successfully!", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Cancel Button
        Button btnCancel = view.findViewById(R.id.btnCancelPayment);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }
}