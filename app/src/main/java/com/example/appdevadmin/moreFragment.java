package com.example.appdevadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class moreFragment extends Fragment {

    public moreFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Account Button
        view.findViewById(R.id.btnAccount).setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new editAccountFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Notify Tenants Button
        view.findViewById(R.id.btnNotifyTenants).setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new announcementFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Settings Button
        view.findViewById(R.id.btnSettings).setOnClickListener(v ->
                Toast.makeText(getContext(), "Settings Clicked", Toast.LENGTH_SHORT).show());

        // CORRECTED LOGOUT BUTTON
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // 1. Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

            // 2. Redirect to Login Screen (MainActivity)
            Intent intent = new Intent(getActivity(), MainActivity.class);

            // 3. Clear the Activity stack so they can't go back
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            // 4. Finish the current Activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}