package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class usersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnFilter = view.findViewById(R.id.btnUserFilter);
        Button btnAdd = view.findViewById(R.id.btnAddUser);
        View cardUser1 = view.findViewById(R.id.cardUser1);

        // Click to open User Profile
        if (cardUser1 != null) {
            cardUser1.setOnClickListener(v -> {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, new userProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }

        // Add User Button
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, new addNewUserFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }

        // Filter Popup
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(requireContext(), v);
                popup.getMenu().add("Name");
                popup.getMenu().add("Unit");
                popup.getMenu().add("Status");
                popup.show();
            });
        }
    }
}