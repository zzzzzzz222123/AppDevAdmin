package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class editAccountFragment extends Fragment {

    public editAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back Button Logic
        ImageButton btnBack = view.findViewById(R.id.btnBackAccount);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Save Changes Button Logic
        Button btnSave = view.findViewById(R.id.btnSaveAccountChanges);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Account Updated Successfully!", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Edit Profile Small Button
        Button btnEditProfile = view.findViewById(R.id.btnEditProfileSmall);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Edit Profile Photo Clicked", Toast.LENGTH_SHORT).show();
            });
        }

        // Security Sections Click Listeners
        View btnChangePass = view.findViewById(R.id.btnChangePasswordAccount);
        if (btnChangePass != null) {
            btnChangePass.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Navigate to Change Password", Toast.LENGTH_SHORT).show();
            });
        }

        View btn2FA = view.findViewById(R.id.btn2FAAccount);
        if (btn2FA != null) {
            btn2FA.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Navigate to 2FA Settings", Toast.LENGTH_SHORT).show();
            });
        }
    }
}