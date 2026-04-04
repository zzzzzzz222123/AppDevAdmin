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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class usersFragment extends Fragment {

    private RecyclerView recyclerUsers;
    private UserAdapter adapter;
    private final List<UserModel> allUsers = new ArrayList<>();
    private FirebaseFirestore db;
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        recyclerUsers = view.findViewById(R.id.recyclerUsers);
        ImageButton btnFilter = view.findViewById(R.id.btnUserFilter);
        Button btnAdd = view.findViewById(R.id.btnAddUser);
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.userSearchView);

        // Setup RecyclerView
        adapter = new UserAdapter(requireContext(), new ArrayList<>(), user -> {
            // Pass user data to profile fragment
            userProfileFragment profileFragment = userProfileFragment.newInstance(user);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, profileFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        recyclerUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerUsers.setAdapter(adapter);

        // Load users
        loadUsers();

        // Add User
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, new addNewUserFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }

        // Search
        if (searchView != null) {
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchQuery = query.toLowerCase();
                    applySearch();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchQuery = newText.toLowerCase();
                    applySearch();
                    return true;
                }
            });
        }

        // Filter
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

    private void loadUsers() {
        db.collection("users")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    allUsers.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        UserModel user = new UserModel(
                                doc.getId(),
                                doc.getString("fullName") != null ? doc.getString("fullName") : "",
                                doc.getString("email") != null ? doc.getString("email") : "",
                                doc.getString("phone") != null ? doc.getString("phone") : "",
                                doc.getString("roomNumber") != null ? doc.getString("roomNumber") : "",
                                doc.getString("leaseStart") != null ? doc.getString("leaseStart") : "",
                                doc.getString("leaseEnd") != null ? doc.getString("leaseEnd") : "",
                                doc.getString("status") != null ? doc.getString("status") : "",
                                doc.getDouble("monthlyRent") != null ? doc.getDouble("monthlyRent") : 0,
                                doc.getLong("rentDueDate") != null ? doc.getLong("rentDueDate").intValue() : 0,
                                doc.getString("emergencyName") != null ? doc.getString("emergencyName") : "",
                                doc.getString("relationship") != null ? doc.getString("relationship") : "",
                                doc.getString("emergencyPhone") != null ? doc.getString("emergencyPhone") : "",
                                doc.getString("role") != null ? doc.getString("role") : "Tenant"
                        );
                        allUsers.add(user);
                    }
                    applySearch();
                });
    }

    private void applySearch() {
        if (searchQuery.isEmpty()) {
            adapter.updateList(new ArrayList<>(allUsers));
            return;
        }

        List<UserModel> filtered = new ArrayList<>();
        for (UserModel user : allUsers) {
            if (user.getFullName().toLowerCase().contains(searchQuery) ||
                    user.getRoomNumber().toLowerCase().contains(searchQuery) ||
                    user.getEmail().toLowerCase().contains(searchQuery)) {
                filtered.add(user);
            }
        }
        adapter.updateList(filtered);
    }
}