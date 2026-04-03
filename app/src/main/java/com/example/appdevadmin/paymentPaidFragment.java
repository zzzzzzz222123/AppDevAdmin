package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class paymentPaidFragment extends Fragment {

    public paymentPaidFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_paid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back to Payments
        TextView closeButton = view.findViewById(R.id.btnClose);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }


        // Send Receipt
        view.findViewById(R.id.btnSendReceipt).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Receipt sent to tenant!", Toast.LENGTH_SHORT).show());

        // View History
        view.findViewById(R.id.btnViewHistory).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Opening payment history...", Toast.LENGTH_SHORT).show());
    }
}