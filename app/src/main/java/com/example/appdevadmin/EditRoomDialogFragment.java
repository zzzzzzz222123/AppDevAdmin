package com.example.appdevadmin;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class EditRoomDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_room, container, false);

        EditText etLeaseStart = view.findViewById(R.id.etLeaseStart);
        EditText etLeaseEnd = view.findViewById(R.id.etLeaseEnd);

        // Date Picker logic for Lease Start and End
        View.OnClickListener dateClickListener = v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        ((EditText)v).setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        };

        etLeaseStart.setOnClickListener(dateClickListener);
        etLeaseEnd.setOnClickListener(dateClickListener);

        // Cancel button logic
        view.findViewById(R.id.btnCancelEdit).setOnClickListener(v -> dismiss());

        // Save button logic
        view.findViewById(R.id.btnSaveChanges).setOnClickListener(v -> {
            // Logic to save changes here
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Ensure dialog has side padding and wraps content
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}