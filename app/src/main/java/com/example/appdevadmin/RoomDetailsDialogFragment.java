package com.example.appdevadmin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView; // Added
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso; // Added Picasso

public class RoomDetailsDialogFragment extends DialogFragment {

    private static final String ARG_ROOM_ID = "room_id";
    private static final String ARG_ROOM_NUMBER = "room_number";
    private static final String ARG_ROOM_TYPE = "room_type";
    private static final String ARG_FLOOR = "floor";
    private static final String ARG_RENT = "monthly_rent";
    private static final String ARG_STATUS = "status";
    private static final String ARG_IMAGE_URL = "image_url"; // Added

    private RoomModel currentRoom;

    public static RoomDetailsDialogFragment newInstance(RoomModel room) {
        RoomDetailsDialogFragment fragment = new RoomDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROOM_ID, room.getId());
        args.putString(ARG_ROOM_NUMBER, room.getRoomNumber());
        args.putString(ARG_ROOM_TYPE, room.getRoomType());
        args.putString(ARG_FLOOR, room.getFloor());
        args.putDouble(ARG_RENT, room.getMonthlyRent());
        args.putString(ARG_STATUS, room.getStatus());
        args.putString(ARG_IMAGE_URL, room.getImageUrl()); // Add image to arguments
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_room_details, container, false);

        // Get views
        TextView txtRoomTitle = view.findViewById(R.id.txtRoomTitle);
        TextView txtBuildingTitle = view.findViewById(R.id.txtBuildingTitle);
        TextView badgeStatus = view.findViewById(R.id.badgeStatus);
        TextView valType = view.findViewById(R.id.valType);
        TextView valRent = view.findViewById(R.id.valRent);
        TextView valFloor = view.findViewById(R.id.valFloor);
        ImageView imgRoomDetail = view.findViewById(R.id.imgRoomDetail); // Added ImageView ID
        Button btnViewHistory = view.findViewById(R.id.btnViewHistory);

        // Get arguments and build currentRoom
        Bundle args = getArguments();
        if (args != null) {
            // Re-creating the RoomModel with the Image URL
            currentRoom = new RoomModel(
                    args.getString(ARG_ROOM_ID),
                    args.getString(ARG_ROOM_NUMBER),
                    args.getString(ARG_ROOM_TYPE),
                    args.getString(ARG_FLOOR),
                    args.getDouble(ARG_RENT),
                    args.getString(ARG_STATUS)
            );
            currentRoom.setImageUrl(args.getString(ARG_IMAGE_URL)); // Manually set the image URL

            // Populate text views
            txtRoomTitle.setText(currentRoom.getRoomNumber() + " - " + currentRoom.getRoomType());
            txtBuildingTitle.setText("Sunrise Residences"); // Or keep as floor if preferred
            valType.setText(currentRoom.getRoomType());
            valFloor.setText(currentRoom.getFloor());
            valRent.setText("₱" + String.format("%,.2f", currentRoom.getMonthlyRent()) + "/mo");
            badgeStatus.setText(currentRoom.getStatus());

            // --- LOAD IMAGE WITH PICASSO ---
            if (currentRoom.getImageUrl() != null && !currentRoom.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(currentRoom.getImageUrl())
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .error(android.R.drawable.ic_menu_gallery)
                        .fit()
                        .centerCrop()
                        .into(imgRoomDetail);
            } else {
                imgRoomDetail.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Status badge styling
            if (currentRoom.getStatus().equalsIgnoreCase("Occupied")) {
                badgeStatus.setBackgroundResource(R.drawable.badge_occupied);
                badgeStatus.setTextColor(Color.parseColor("#2E7D32"));
            } else if (currentRoom.getStatus().equalsIgnoreCase("Vacant")) {
                badgeStatus.setBackgroundResource(R.drawable.badge_vacant);
                badgeStatus.setTextColor(Color.parseColor("#1976D2"));
            } else {
                badgeStatus.setBackgroundResource(R.drawable.badge_vacant);
                badgeStatus.setTextColor(Color.parseColor("#F57C00"));
            }
        }

        // --- BUTTON LISTENERS ---
        if (btnViewHistory != null) {
            btnViewHistory.setOnClickListener(v -> {
                RenterHistoryDialog historyDialog = RenterHistoryDialog.newInstance(currentRoom.getRoomNumber());
                historyDialog.show(getParentFragmentManager(), "RenterHistoryDialog");
            });
        }

        view.findViewById(R.id.btnCloseDetails).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnEditFromDetails).setOnClickListener(v -> {
            dismiss();
            EditRoomDialogFragment editDialog = EditRoomDialogFragment.newInstance(currentRoom);
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
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}