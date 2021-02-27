package com.example.trackie.ui.locations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.database.MapData;
import com.example.trackie.ui.home.LocationsAdapter;

import java.util.ArrayList;

public class LocationsFragment extends Fragment {

    private LocationsViewModel locationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        locationsViewModel =
                new ViewModelProvider(this).get(LocationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_locations, container, false);
        locationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
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
    }
}