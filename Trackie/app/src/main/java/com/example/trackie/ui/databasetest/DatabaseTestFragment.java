package com.example.trackie.ui.databasetest;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.trackie.R;
import com.example.trackie.database.FirestoreHelper;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.MapData;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.database.StorageDownloader;
import com.example.trackie.ui.Prefs;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTestFragment extends Fragment {
    private MaterialButton getButton;
    private MaterialButton setButton;
    private MaterialTextView textView;
    private MaterialButton removeButton;
    private List<MapData> mapDataList;
    private ImageView image;

    private Map<String, List<Integer>> data;
    private PointF location = new PointF((float) 13.0, (float) 15.0);
    private double z = 1.0;
    private String device = "Samsung Galaxy S20";
    private Timestamp timestamp = Timestamp.now();

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
        removeButton = view.findViewById(R.id.remove_button);
        textView = view.findViewById(R.id.database_display);
        image = view.findViewById(R.id.image_database);
        data = new HashMap<>();
        data.put("BSSID OF WIFI AP", Arrays.asList(-82, -84, -83, 100, -86));
        data.put("ANOTHER BSSID", Arrays.asList(-83, -84, -83, -90, -85));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 getData(false);
                getBSSIDs();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapDataList == null) {
                    getData(true);
                } else if (mapDataList.size() == 0){
                    Toast.makeText(getContext(), "Nothing to remove", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "HI NO REMOVING ALLOWED", Toast.LENGTH_LONG).show();
                    // removeData();
                }
            }
        });
    }

    public void getData(boolean remove) {
        FirestoreHelper.GetMapData getter = new FirestoreHelper.GetMapData("B2L2 ACCURATE");
        getter.execute(new OnCompleteCallback() {
            @Override
            public void onSuccess() {
                if (!remove) {
                    mapDataList = getter.getResult();
                    textView.setText(mapDataList.toString());
                    Toast.makeText(getContext(), mapDataList.size() + " datapoints", Toast.LENGTH_LONG).show();

                    if (mapDataList.size() > 0) {
                        FloorplanHelper.RetrieveFloorplan retrieveFloorplan = new FloorplanHelper.RetrieveFloorplan(mapDataList.get(0).getName(), getContext());
                        retrieveFloorplan.execute(new OnCompleteCallback() {
                            @Override
                            public void onSuccess() {
                                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(retrieveFloorplan.getFloorplanURL());
                                Glide.with(requireContext())
                                        .load(ref)
                                        .into(image);
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getContext(), "No Such Floorplan. Please upload one or choose another!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError() {

                            }
                        });

                    }
                } else {
                    if (mapDataList != null) {
                        Toast.makeText(getContext(), "HI NO REMOVING ALLOWED", Toast.LENGTH_LONG).show();
                        //removeData();
                    }
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Get Data failed.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError() {
                // error handling
            }
        });
    }

    public void setData() {
        MapData mapData = new MapData("B2L1", data, location, z, device, timestamp);
        FirestoreHelper.SetMapData setter = new FirestoreHelper.SetMapData(mapData);
        setter.execute(new OnCompleteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Data set success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Data set fail :(", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError() {
                // error handling
            }
        });
    }

    /*public void removeData() {
            FirestoreHelper.RemoveMapData remover = new FirestoreHelper.RemoveMapData(mapDataList.get(0));
            remover.execute(new OnCompleteCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Successfully removed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onError() {

                }
            });
        }*/

    public void getBSSIDs() {
        String currentLocation = Prefs.getCurrentLocation(getContext());
        String suffix = currentLocation.equals("B2L2") ? "_good_ssids2.txt" : "_good_ssids.txt";
        StorageDownloader storageDownloader = new StorageDownloader(currentLocation + suffix, "ssids", getContext());
        storageDownloader.execute(new OnCompleteCallback() {
            @Override
            public void onSuccess() {
                textView.setText(storageDownloader.getGoodBSSIDs().toString());
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Getting GOOD_BSSIDS file failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(getContext(), "Getting GOOD_BSSIDS file errored", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
