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

public class maintenanceOngoingDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Inflate the view first and store it in a variable
        View view = inflater.inflate(R.layout.fragment_maintenance_ongoing_dialog, container, false);

        // 2. Now you can find the button using that view
        TextView closeButton = view.findViewById(R.id.btnClose);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack()); // Use dismiss() for Dialogs
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // This makes the background transparent so your rounded card looks correct
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}