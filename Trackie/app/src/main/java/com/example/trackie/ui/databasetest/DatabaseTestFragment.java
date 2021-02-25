package com.example.trackie.ui.databasetest;

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackie.R;
import com.example.trackie.database.DataReceivedCallback;
import com.example.trackie.database.FirestoreHelper;
import com.example.trackie.database.MapData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTestFragment extends Fragment {
    private MaterialButton getButton;
    private MaterialButton setButton;
    private MaterialTextView textView;
    private Map<String, List<Integer>> data;
    private Point location = new Point(13, 15);
    private double z = 1.0;
    private String device = "Samsung Galaxy S20";
    private Timestamp timestamp = Timestamp.now();
    private String floorplan = "test floorplan";

    private DatabaseTestViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DatabaseTestViewModel.class);
        View view = inflater.inflate(R.layout.fragment_databasetest, container, false);
        viewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        getButton = view.findViewById(R.id.get_button);
        setButton = view.findViewById(R.id.set_button);
        textView = view.findViewById(R.id.database_display);
        data = new HashMap<>();
        data.put("SUTD", Arrays.asList(-82, -84, -83, 100, -86));
        data.put("Hostel", Arrays.asList(-83, -84, -83, -90, -85));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                FirestoreHelper.GetMapData getter = new FirestoreHelper.GetMapData("HELLO WORLD");
                getter.getMapData(new DataReceivedCallback() {
                    @Override
                    public void onDataReceived() {
                        if (getter.isSuccessful()) {
                            List<MapData> mapData = getter.getResult();
                            textView.setText(mapData.toString());
                        } else {
                            Toast.makeText(getContext(), "No data received", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                MapData mapData = new MapData("HELLO WORLD", data, location, z, device, timestamp, null);
                FirestoreHelper.SetMapData setter = new FirestoreHelper.SetMapData(mapData);
                setter.setMapData(new DataReceivedCallback() {
                    @Override
                    public void onDataReceived() {
                        if (setter.isSuccessful()) {
                            Toast.makeText(getContext(), "Data set success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Data set fail :(", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
