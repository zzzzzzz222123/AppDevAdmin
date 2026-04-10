package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogsFragment extends Fragment {

    private RecyclerView recyclerLogs;
    private ActivityLogAdapter adapter;
    private List<ActivityLogModel> allLogsList = new ArrayList<>();
    private FirebaseFirestore db;

    private TextView btnAll, btnTenants, btnRooms, btnPayments, lblStats;
    private String currentCategory = "all";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // 1. Initialize Views
        btnAll = view.findViewById(R.id.btnLogFilterAll);
        btnTenants = view.findViewById(R.id.btnLogFilterTenant);
        btnRooms = view.findViewById(R.id.btnLogFilterRoom);
        btnPayments = view.findViewById(R.id.btnLogFilterPayment);
        lblStats = view.findViewById(R.id.lblLogStats);
        SearchView searchView = view.findViewById(R.id.logSearchView);
        ImageButton btnBack = view.findViewById(R.id.btnActivityLogs);

        // 2. Setup RecyclerView
        recyclerLogs = view.findViewById(R.id.recyclerActivityLogs);
        recyclerLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActivityLogAdapter(new ArrayList<>()); // Start empty
        recyclerLogs.setAdapter(adapter);

        // 3. Back Button
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // 4. Filter Listeners
        btnAll.setOnClickListener(v -> updateCategory("all"));
        btnTenants.setOnClickListener(v -> updateCategory("tenant"));
        btnRooms.setOnClickListener(v -> updateCategory("room"));
        btnPayments.setOnClickListener(v -> updateCategory("payment"));

        // 5. Search Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query.toLowerCase();
                applyFilters();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText.toLowerCase();
                applyFilters();
                return true;
            }
        });

        loadAllLogs();
    }

    private void loadAllLogs() {
        // Fetch ALL logs from Firestore
        db.collection("activity_logs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    allLogsList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        allLogsList.add(doc.toObject(ActivityLogModel.class));
                    }
                    applyFilters();
                });
    }

    private void updateCategory(String category) {
        this.currentCategory = category;

        // Update UI selection states
        btnAll.setSelected(category.equals("all"));
        btnTenants.setSelected(category.equals("tenant"));
        btnRooms.setSelected(category.equals("room"));
        btnPayments.setSelected(category.equals("payment"));

        applyFilters();
    }

    private void applyFilters() {
        List<ActivityLogModel> filteredList = new ArrayList<>();

        for (ActivityLogModel log : allLogsList) {
            // Category Check
            boolean matchesCategory = currentCategory.equals("all") ||
                    (log.getType() != null && log.getType().equalsIgnoreCase(currentCategory));

            // Search Check
            boolean matchesSearch = searchQuery.isEmpty() ||
                    log.getTitle().toLowerCase().contains(searchQuery) ||
                    log.getDetails().toLowerCase().contains(searchQuery);

            if (matchesCategory && matchesSearch) {
                filteredList.add(log);
            }
        }

        adapter.updateList(filteredList); // Make sure your adapter has this method
        lblStats.setText("Showing " + filteredList.size() + " activities");
    }
}