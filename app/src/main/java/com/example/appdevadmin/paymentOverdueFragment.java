package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class paymentOverdueFragment extends Fragment {

    public paymentOverdueFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_overdue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Close/Back logic
        TextView closeButton = view.findViewById(R.id.btnClose);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Send Notice Logic
        Button btnSendNotice = view.findViewById(R.id.btnSendNotice);
        if (btnSendNotice != null) {
            btnSendNotice.setOnClickListener(v -> 
                Toast.makeText(getContext(), "Overdue notice sent!", Toast.LENGTH_SHORT).show());
        }

        // Record Payment Button
        Button btnRecord = view.findViewById(R.id.btnRecordPayment);
        if (btnRecord != null) {
            btnRecord.setOnClickListener(v -> {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, new recordPaymentFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }

        // Waive Late Fee Logic
        Button btnWaive = view.findViewById(R.id.btnWaiveFee);
        if (btnWaive != null) {
            btnWaive.setOnClickListener(v -> 
                Toast.makeText(getContext(), "Late fee waived.", Toast.LENGTH_SHORT).show());
        }
    }
}