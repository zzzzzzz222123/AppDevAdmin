package com.example.appdevadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class maintenanceFragment extends Fragment {

    // ── UI ─────────────────────────────────────────────────────────────────
    private RecyclerView rvMaintenance;
    private TextView btnFilterAll, btnFilterPending, btnFilterOngoing, btnFilterCompleted;
    private TextView tvPendingCount, tvOngoingCount, tvCompletedCount;
    private SearchView searchView;

    // ── Adapter & data ─────────────────────────────────────────────────────
    private AdminMaintenanceAdapter adapter;
    private final List<AdminMaintenanceRequest> allRequests      = new ArrayList<>();
    private final List<AdminMaintenanceRequest> filteredRequests = new ArrayList<>();
    private String currentFilter = "All";
    private String currentSearch = "";

    // ── Firestore ──────────────────────────────────────────────────────────
    private FirebaseFirestore db;
    private ListenerRegistration listenerReg;

    public maintenanceFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        bindViews(view);
        setupRecyclerView();
        setupFilters();
        setupSearch();
        listenToAllRequests();
    }

    // ── Bind views ─────────────────────────────────────────────────────────
    private void bindViews(View view) {
        rvMaintenance       = view.findViewById(R.id.rvMaintenance);
        btnFilterAll        = view.findViewById(R.id.btnFilterAll);
        btnFilterPending    = view.findViewById(R.id.btnFilterPending);
        btnFilterOngoing    = view.findViewById(R.id.btnFilterOngoing);
        btnFilterCompleted  = view.findViewById(R.id.btnFilterCompleted);
        searchView          = view.findViewById(R.id.maintenanceSearchView);

        // The 3 status counter TextViews — grab by id from the XML badges
        tvPendingCount   = view.findViewById(R.id.tvCountPending);
        tvOngoingCount   = view.findViewById(R.id.tvCountOngoing);
        tvCompletedCount = view.findViewById(R.id.tvCountCompleted);
    }

    // ── RecyclerView ───────────────────────────────────────────────────────
    private void setupRecyclerView() {
        adapter = new AdminMaintenanceAdapter(filteredRequests, request -> {
            // Open detail/update dialog
            AdminMaintenanceDetailFragment detail = AdminMaintenanceDetailFragment.newInstance(request.id);
            detail.show(getParentFragmentManager(), "detail");
        });
        rvMaintenance.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMaintenance.setAdapter(adapter);
    }

    // ── Filter chips ───────────────────────────────────────────────────────
    private void setupFilters() {
        btnFilterAll.setOnClickListener(v       -> applyFilter("All"));
        btnFilterPending.setOnClickListener(v   -> applyFilter("pending"));
        btnFilterOngoing.setOnClickListener(v   -> applyFilter("ongoing"));
        btnFilterCompleted.setOnClickListener(v -> applyFilter("completed"));

        // set All as default selected
        setFilterSelected(btnFilterAll);
    }

    private void applyFilter(String filter) {
        currentFilter = filter;
        switch (filter) {
            case "pending":   setFilterSelected(btnFilterPending);  break;
            case "ongoing":   setFilterSelected(btnFilterOngoing);  break;
            case "completed": setFilterSelected(btnFilterCompleted); break;
            default:          setFilterSelected(btnFilterAll);      break;
        }
        rebuildList();
    }

    private void setFilterSelected(TextView selected) {
        TextView[] all = {btnFilterAll, btnFilterPending, btnFilterOngoing, btnFilterCompleted};
        for (TextView btn : all) {
            if (btn == null) continue;
            boolean active = btn == selected;
            btn.setSelected(active);
            // filter_btn_selector and filter_text_selector drawables handle the color via state_selected
        }
    }

    // ── Search ─────────────────────────────────────────────────────────────
    private void setupSearch() {
        if (searchView == null) return;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearch = newText == null ? "" : newText.trim().toLowerCase();
                rebuildList();
                return true;
            }
        });
    }

    // ── Firestore listener — reads ALL requests (admin sees everything) ─────
    private void listenToAllRequests() {
        listenerReg = db.collection("maintenance_requests")
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    allRequests.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        AdminMaintenanceRequest req = doc.toObject(AdminMaintenanceRequest.class);
                        req.id = doc.getId();
                        allRequests.add(req);
                    }

                    updateCounters();
                    rebuildList();
                });
    }

    // ── Status counters ────────────────────────────────────────────────────
    private void updateCounters() {
        int pending = 0, ongoing = 0, completed = 0;
        for (AdminMaintenanceRequest r : allRequests) {
            if ("pending".equals(r.status))   pending++;
            if ("ongoing".equals(r.status))   ongoing++;
            if ("completed".equals(r.status)) completed++;
        }
        if (tvPendingCount   != null) tvPendingCount.setText("● Pending: "   + pending);
        if (tvOngoingCount   != null) tvOngoingCount.setText("● Ongoing: "   + ongoing);
        if (tvCompletedCount != null) tvCompletedCount.setText("● Completed: " + completed);
    }

    // ── Rebuild filtered + searched list ───────────────────────────────────
    private void rebuildList() {
        filteredRequests.clear();
        for (AdminMaintenanceRequest r : allRequests) {
            // filter
            if (!"All".equals(currentFilter) && !currentFilter.equals(r.status)) continue;
            // search
            if (!TextUtils.isEmpty(currentSearch)) {
                String hay = ((r.title       != null ? r.title       : "") + " " +
                        (r.unit        != null ? r.unit        : "") + " " +
                        (r.category    != null ? r.category    : "") + " " +
                        (r.tenantId    != null ? r.tenantId    : "")).toLowerCase();
                if (!hay.contains(currentSearch)) continue;
            }
            filteredRequests.add(r);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerReg != null) listenerReg.remove();
    }
}