package com.example.appdevadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class roomFragment extends Fragment {

    private TextView btnAll, btnOccupied, btnVacant, lblStats;
    private SearchView searchView;
    private ImageButton btnFilter;
    private Button btnAdd;
    private RecyclerView recyclerRooms;

    private RoomAdapter adapter;
    private List<RoomModel> allRooms = new ArrayList<>();
    private FirebaseFirestore db;

    private String currentStatus = "all";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Views
        btnAll = view.findViewById(R.id.btnFilterAll);
        btnOccupied = view.findViewById(R.id.btnFilterOccupied);
        btnVacant = view.findViewById(R.id.btnFilterVacant);
        lblStats = view.findViewById(R.id.lblStats);
        searchView = view.findViewById(R.id.searchView);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnAdd = view.findViewById(R.id.btnAdd);
        recyclerRooms = view.findViewById(R.id.recyclerRooms);

        // RecyclerView setup
        adapter = new RoomAdapter(requireContext(), new ArrayList<>(), room -> {
            RoomDetailsDialogFragment detailsDialog = RoomDetailsDialogFragment.newInstance(room);
            detailsDialog.show(getChildFragmentManager(), "RoomDetailsDialog");
        });
        recyclerRooms.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRooms.setAdapter(adapter);

        // Default filter
        btnAll.setSelected(true);

        // Load rooms from Firestore
        loadRooms();

        // Add Room
        btnAdd.setOnClickListener(v -> {
            AddRoomDialogFragment dialog = new AddRoomDialogFragment();
            dialog.show(getChildFragmentManager(), "AddRoomDialog");
        });

        // Filter buttons
        btnAll.setOnClickListener(v -> updateStatusFilter("all"));
        btnOccupied.setOnClickListener(v -> updateStatusFilter("occupied"));
        btnVacant.setOnClickListener(v -> updateStatusFilter("vacant"));

        // Search
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

        btnFilter.setOnClickListener(v ->
                Toast.makeText(getContext(), "Advanced Filter Clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadRooms() {
        db.collection("rooms")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    allRooms.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        RoomModel room = new RoomModel(
                                doc.getId(),
                                doc.getString("roomNumber"),
                                doc.getString("roomType"),
                                doc.getString("floor"),
                                doc.getDouble("monthlyRent") != null ? doc.getDouble("monthlyRent") : 0,
                                doc.getString("status")
                        );
                        allRooms.add(room);
                    }
                    applyFilters();
                });
    }

    private void applyFilters() {
        List<RoomModel> filtered = new ArrayList<>();
        int occupied = 0, vacant = 0;

        for (RoomModel room : allRooms) {
            boolean matchesStatus = currentStatus.equals("all")
                    || room.getStatus().equalsIgnoreCase(currentStatus);
            boolean matchesSearch = searchQuery.isEmpty()
                    || room.getRoomNumber().toLowerCase().contains(searchQuery)
                    || room.getRoomType().toLowerCase().contains(searchQuery)
                    || room.getFloor().toLowerCase().contains(searchQuery);

            if (matchesStatus && matchesSearch) filtered.add(room);

            // Count all rooms regardless of filter
            if (room.getStatus().equalsIgnoreCase("Occupied")) occupied++;
            else if (room.getStatus().equalsIgnoreCase("Vacant")) vacant++;
        }

        adapter.updateList(filtered);
        lblStats.setText("Total: " + allRooms.size() + " rooms · "
                + occupied + " Occupied · " + vacant + " Vacant");
    }

    private void updateStatusFilter(String status) {
        currentStatus = status;
        btnAll.setSelected(status.equals("all"));
        btnOccupied.setSelected(status.equals("occupied"));
        btnVacant.setSelected(status.equals("vacant"));
        applyFilters();
    }
}