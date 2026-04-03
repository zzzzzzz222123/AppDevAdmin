package com.example.appdevadmin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class addNewUserFragment extends Fragment {

    private TextInputEditText etDob, etLeaseStart, etLeaseEnd;
    private AutoCompleteTextView spinnerRoom, spinnerRole;

    public addNewUserFragment() {
        // Required empty public constructor
    }

    public static addNewUserFragment newInstance() {
        return new addNewUserFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize Date Views
        etDob = view.findViewById(R.id.etDob);
        etLeaseStart = view.findViewById(R.id.etLeaseStart);
        etLeaseEnd = view.findViewById(R.id.etLeaseEnd);

        // 2. Initialize Dropdowns
        spinnerRoom = view.findViewById(R.id.spinnerRoom);
        spinnerRole = view.findViewById(R.id.spinnerRole);

        // 3. Set up Date Pickers
        setupDatePicker(etDob, "Select Date of Birth");
        setupDatePicker(etLeaseStart, "Select Lease Start");
        setupDatePicker(etLeaseEnd, "Select Lease End");

        // 4. Set up Dropdown Adapters
        setupDropdowns();
    }

    private void setupDatePicker(TextInputEditText editText, String title) {
        editText.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(title)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                editText.setText(sdf.format(selection));
            });
        });
    }

    private void setupDropdowns() {
        // Sample Rooms
        String[] rooms = {"Room 101", "Room 102", "Room 201", "Room 202"};
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, rooms);
        spinnerRoom.setAdapter(roomAdapter);

        // Sample Roles
        String[] roles = {"Tenant", "Admin", "Landlord"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, roles);
        spinnerRole.setAdapter(roleAdapter);
    }
}