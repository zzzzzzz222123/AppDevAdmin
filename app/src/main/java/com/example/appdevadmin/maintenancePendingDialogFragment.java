package com.example.appdevadmin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class maintenancePendingDialogFragment extends DialogFragment {

    public maintenancePendingDialogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout and store it in a variable
        View view = inflater.inflate(R.layout.fragment_maintenance_pending_dialog, container, false);

        // Setup the Close button logic
        TextView closeButton = view.findViewById(R.id.btnClose);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set dialog dimensions and make background transparent for rounded corners
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}