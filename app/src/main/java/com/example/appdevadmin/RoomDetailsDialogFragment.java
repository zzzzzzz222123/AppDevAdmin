package com.example.appdevadmin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RoomDetailsDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_room_details, container, false);

        // Close button logic
        view.findViewById(R.id.btnCloseDetails).setOnClickListener(v -> dismiss());

        // Edit button logic - opens the EditRoomDialogFragment
        view.findViewById(R.id.btnEditFromDetails).setOnClickListener(v -> {
            dismiss(); // Close details first
            EditRoomDialogFragment editDialog = new EditRoomDialogFragment();
            editDialog.show(getParentFragmentManager(), "EditRoomDialog");
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}