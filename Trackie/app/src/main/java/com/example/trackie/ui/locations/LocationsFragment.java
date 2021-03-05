package com.example.trackie.ui.locations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.database.MapData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class LocationsFragment extends Fragment {

    private LocationsViewModel locationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        locationsViewModel =
                new ViewModelProvider(this).get(LocationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_locations, container, false);
        locationsViewModel.getLocationsData().observe(getViewLifecycleOwner(), new Observer<ArrayList<MapData>>() {
            @Override
            public void onChanged(ArrayList<MapData> mapData) {

            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView locationsRecyclerView = (RecyclerView) view.findViewById(R.id.locations_recycler_view);
        ArrayList<MapData> mapDatas = new ArrayList<>();
        mapDatas.add(new MapData("Bedroom"));
        mapDatas.add(new MapData("Living Room"));
        mapDatas.add(new MapData("Study Room"));
        RecyclerView.Adapter locationsAdapter = new LocationsAdapter(getActivity(), mapDatas);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        locationsRecyclerView.setAdapter(locationsAdapter);

        FloatingActionButton addLocationButton = view.findViewById(R.id.upload_location_button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().
                        beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
                fragmentTransaction.add(view.getId(), new AddLocationFragment());
                fragmentTransaction.addToBackStack("Locations").commit();
            }
        });
    }
}