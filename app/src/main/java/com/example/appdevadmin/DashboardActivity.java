package com.example.appdevadmin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appdevadmin.databinding.DashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private DashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding only for the main container
        binding = DashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default Fragment
        if (savedInstanceState == null) {
            replaceFragment(new dashboardFragment());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                replaceFragment(new dashboardFragment());
            } else if (itemId == R.id.nav_rooms) {
                replaceFragment(new roomFragment());
            } else if (itemId == R.id.nav_payments) {
                replaceFragment(new paymentFragment());
            } else if (itemId == R.id.nav_maintenance) {
                replaceFragment(new maintenanceFragment());
            } else if (itemId == R.id.nav_users) {
                replaceFragment(new usersFragment());
            } else if (itemId == R.id.nav_more) {
                replaceFragment(new moreFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}