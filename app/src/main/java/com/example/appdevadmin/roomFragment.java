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

public class roomFragment extends Fragment {

    private TextView btnAll, btnOccupied, btnVacant;
    private View card1, card2, card3;
    private SearchView searchView;
    private ImageButton btnFilter;
    private Button btnAdd;
    
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

        // Find filter buttons
        btnAll = view.findViewById(R.id.btnFilterAll);
        btnOccupied = view.findViewById(R.id.btnFilterOccupied);
        btnVacant = view.findViewById(R.id.btnFilterVacant);

        // Find search and filter button
        searchView = view.findViewById(R.id.searchView);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnAdd = view.findViewById(R.id.btnAdd);

        // Find room cards
        card1 = view.findViewById(R.id.cardRoom1); // Occupied, Room 101
        card2 = view.findViewById(R.id.cardRoom2); // Occupied, Room 102
        card3 = view.findViewById(R.id.cardRoom3); // Vacant, Room 103

        // Set default selection
        btnAll.setSelected(true);

        // Add Room Button Listener
        btnAdd.setOnClickListener(v -> {
            AddRoomDialogFragment dialog = new AddRoomDialogFragment();
            dialog.show(getChildFragmentManager(), "AddRoomDialog");
        });

        // Room Card Click Listeners - Open Details Dialog
        View.OnClickListener cardClickListener = v -> {
            RoomDetailsDialogFragment detailsDialog = new RoomDetailsDialogFragment();
            detailsDialog.show(getChildFragmentManager(), "RoomDetailsDialog");
        };

        card1.setOnClickListener(cardClickListener);
        card2.setOnClickListener(cardClickListener);
        card3.setOnClickListener(cardClickListener);
        
        // Status Click Listeners
        btnAll.setOnClickListener(v -> updateStatusFilter("all"));
        btnOccupied.setOnClickListener(v -> updateStatusFilter("occupied"));
        btnVacant.setOnClickListener(v -> updateStatusFilter("vacant"));

        // Search Listener
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

        // Advanced Filter Button Listener
        btnFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Advanced Filter Clicked", Toast.LENGTH_SHORT).show();
        });
        
    }

    private void updateStatusFilter(String status) {
        currentStatus = status;
        
        // Update button visual states
        btnAll.setSelected(status.equals("all"));
        btnOccupied.setSelected(status.equals("occupied"));
        btnVacant.setSelected(status.equals("vacant"));
        
        applyFilters();
    }

    private void applyFilters() {
        // Filter logic for Room 101 (Occupied)
        boolean matchesStatus1 = currentStatus.equals("all") || currentStatus.equals("occupied");
        boolean matchesSearch1 = searchQuery.isEmpty() || "room 101".contains(searchQuery) || "studio".contains(searchQuery);
        card1.setVisibility(matchesStatus1 && matchesSearch1 ? View.VISIBLE : View.GONE);

        // Filter logic for Room 102 (Occupied)
        boolean matchesStatus2 = currentStatus.equals("all") || currentStatus.equals("occupied");
        boolean matchesSearch2 = searchQuery.isEmpty() || "room 102".contains(searchQuery) || "1 bedroom".contains(searchQuery);
        card2.setVisibility(matchesStatus2 && matchesSearch2 ? View.VISIBLE : View.GONE);

        // Filter logic for Room 103 (Vacant)
        boolean matchesStatus3 = currentStatus.equals("all") || currentStatus.equals("vacant");
        boolean matchesSearch3 = searchQuery.isEmpty() || "room 103".contains(searchQuery) || "studio".contains(searchQuery);
        card3.setVisibility(matchesStatus3 && matchesSearch3 ? View.VISIBLE : View.GONE);
    }
}