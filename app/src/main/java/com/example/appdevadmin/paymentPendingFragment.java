package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.w3c.dom.Text;

public class paymentPendingFragment extends Fragment {

    public paymentPendingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_pending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back to Payments list
        TextView closeButton = view.findViewById(R.id.btnClose);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
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
    }
}