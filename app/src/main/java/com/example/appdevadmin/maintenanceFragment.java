package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class maintenanceFragment extends Fragment {

    public maintenanceFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View itemMaintenancePending = view.findViewById(R.id.ItemMaintenancePending);
        if (itemMaintenancePending != null) {
            itemMaintenancePending.setOnClickListener(v -> navigateToStatus(new maintenancePendingDialogFragment()));
        }

        View itemMaintenanceOngoing = view.findViewById(R.id.ItemMaintenanceOngoing);
        if (itemMaintenanceOngoing != null) {
            itemMaintenanceOngoing.setOnClickListener(v -> navigateToStatus(new maintenanceOngoingDialogFragment()));
        }

        View itemMaintenanceCompleted = view.findViewById(R.id.ItemMaintenanceCompleted);
        if (itemMaintenanceCompleted != null) {
            itemMaintenanceCompleted.setOnClickListener(v -> navigateToStatus(new maintenanceCompletedDialogFragment()));
        }
        }
    private void navigateToStatus(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    }
