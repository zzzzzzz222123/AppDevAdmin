package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class paymentFragment extends Fragment {

    private TextView btnFilterAll, btnFilterPaid, btnFilterPending, btnFilterOverdue;

    public paymentFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Back Button


        // Filter Buttons
        btnFilterAll = view.findViewById(R.id.btnPayFilterAll);
        btnFilterPaid = view.findViewById(R.id.btnPayFilterPaid);
        btnFilterPending = view.findViewById(R.id.btnPayFilterPending);
        btnFilterOverdue = view.findViewById(R.id.btnPayFilterOverdue);

        // Set "All" as default selected
        btnFilterAll.setSelected(true);

        View.OnClickListener filterListener = v -> {
            btnFilterAll.setSelected(false);
            btnFilterPaid.setSelected(false);
            btnFilterPending.setSelected(false);
            btnFilterOverdue.setSelected(false);
            v.setSelected(true);
        };

        btnFilterAll.setOnClickListener(filterListener);
        btnFilterPaid.setOnClickListener(filterListener);
        btnFilterPending.setOnClickListener(filterListener);
        btnFilterOverdue.setOnClickListener(filterListener);

        // Record Payment Button
        view.findViewById(R.id.btnRecordPayment).setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new recordPaymentFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Navigation to Status Details Fragments
        View itemPaid = view.findViewById(R.id.itemPaidExample);
        if (itemPaid != null) {
            itemPaid.setOnClickListener(v -> navigateToStatus(new paymentPaidFragment()));
        }

        View itemPending = view.findViewById(R.id.itemPendingExample);
        if (itemPending != null) {
            itemPending.setOnClickListener(v -> navigateToStatus(new paymentPendingFragment()));
        }

        View itemOverdue = view.findViewById(R.id.itemOverdueExample);
        if (itemOverdue != null) {
            itemOverdue.setOnClickListener(v -> navigateToStatus(new paymentOverdueFragment()));
        }
    }

    private void navigateToStatus(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}