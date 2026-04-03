package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class moreFragment extends Fragment {

    public moreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Link to Account (Edit Account Fragment)
        view.findViewById(R.id.btnAccount).setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new editAccountFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Link to Notify Tenants (Announcement Fragment)
        view.findViewById(R.id.btnNotifyTenants).setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new announcementFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Other buttons
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Settings Clicked", Toast.LENGTH_SHORT).show());
            
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Logout Clicked", Toast.LENGTH_SHORT).show());
    }
}