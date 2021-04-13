package com.example.trackie.ui.locations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.database.FloorplanData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LocationsFragment extends Fragment {
    private LocationsViewModel locationsViewModel;
    private List<FloorplanData> floorplanDataList = new ArrayList<>();
    RecyclerView locationsRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        locationsViewModel =
                new ViewModelProvider(requireActivity()).get(LocationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_locations, container, false);

        locationsRecyclerView = (RecyclerView) root.findViewById(R.id.locations_recycler_view);
        floorplanDataList = (List<FloorplanData>) locationsViewModel.getLocationsData();
        RecyclerView.Adapter locationsAdapter = new LocationsAdapter(getContext(), getActivity(), floorplanDataList);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        locationsRecyclerView.setAdapter(locationsAdapter);

        FloatingActionButton addLocationButton = root.findViewById(R.id.upload_location_button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().
                        beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
                fragmentTransaction.replace(root.getId(), new AddLocationFragment());
                fragmentTransaction.addToBackStack("Locations").commit();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
        }
    }
}