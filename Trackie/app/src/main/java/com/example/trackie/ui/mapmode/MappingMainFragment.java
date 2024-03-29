package com.example.trackie.ui.mapmode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.trackie.R;
import com.example.trackie.database.FirestoreHelper;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.MapData;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.ui.FetchWiFiDataUtils;
import com.example.trackie.ui.Prefs;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MappingMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MappingMainFragment extends Fragment implements PinImageMapView.PinOptionsController {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MAP_DATA_KEY = "MapData";
    private static String floorplanLocation;
    private static List<MapData> mapDataList;
    private MapWiFiDataListener mapWiFiDataListener;
    private PinImageMapView mappingImageView;
    private Button confirmMappingClickButton;
    private Button endMappingButton;
    private final static int WIFI_SCAN_PERMISSIONS = 123;
    private boolean isPermissionsGranted;

    private MappingMainViewModel viewModel;

    private FetchWiFiDataUtils dataUtils;

    public MappingMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MappingMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MappingMainFragment newInstance(String param1, String param2) {
        MappingMainFragment fragment = new MappingMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // get location name from parent activity
        if (getActivity() instanceof MapModeActivity) {
            floorplanLocation = ((MapModeActivity)getActivity()).getCurrentFloorplanName();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_mapping_main, container, false);

        // get viewmodel
        viewModel = new ViewModelProvider(this).get(MappingMainViewModel.class);

        final boolean alreadyOpenedMap = viewModel.isOpenedMapOnce().getValue(); // check if map is being opened for the first time

        String json_mapdata = Prefs.getSavedMapping(getContext());
        MapData[] tempMapDataArray = new Gson().fromJson(json_mapdata, MapData[].class);
        if (tempMapDataArray == null || tempMapDataArray.length == 0) return root;

        List<MapData> tempMapDataList = new ArrayList<>(Arrays.asList(tempMapDataArray));
        if (tempMapDataList.get(0).getName().equals(floorplanLocation) && !alreadyOpenedMap) {

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage("Restore last saved mapping?");
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes",
                    (dialog, which) -> viewModel.setMapDataList(tempMapDataList));
            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No, Start Fresh",
                    (dialog, which) -> {
                        Prefs.setSavedMapping(getContext(), "");
                        dialog.dismiss();
                    });

            alertDialog.show();
        }
        viewModel.setOpenedMapOnce(true);

        return root;
    }


    // TODO: fix issue for when background image does not load by the time user clicks map mode
    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // set up map view
        mappingImageView = view.findViewById(R.id.mapping_indoor_map_view);
        mappingImageView.setPinOptionsController(this);

        viewModel.getMapDataList().observe(getViewLifecycleOwner(), new Observer<List<MapData>>() {
            @Override
            public void onChanged(List<MapData> mMapDataList) {
                mapDataList = mMapDataList;
                List<PointF> mappedPoints = new ArrayList<>();
                for (MapData mapData : mMapDataList) {
                    mappedPoints.add(mapData.getLocation());
                }

                mappingImageView.setMappedPoints(mappedPoints);
            }
        });

        // get variables from parent activity
        if (getActivity() instanceof MapModeActivity) {
            floorplanLocation = ((MapModeActivity)getActivity()).getCurrentFloorplanName();
        }

        if (mapDataList == null) mapDataList = new ArrayList<>();
        if (floorplanLocation != null) {
            FloorplanHelper.RetrieveFloorplan retrieveFloorplan = new FloorplanHelper.RetrieveFloorplan(floorplanLocation, getContext());
            retrieveFloorplan.execute(new OnCompleteCallback() {
                @Override
                public void onSuccess() {
                    StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(retrieveFloorplan.getFloorplanURL());
                    Glide.with(requireContext()).asBitmap()
                            .load(ref)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mappingImageView.setImage(ImageSource.bitmap(resource));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getContext(), "Can't Retrieve Floorplan", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError() {
                }
            });
        }

        // Handle scanning of RSSI values
        mapWiFiDataListener = new MapWiFiDataListener();
        dataUtils = new FetchWiFiDataUtils(getActivity(), mapWiFiDataListener);
        isPermissionsGranted = dataUtils.getPermissionGranted();
        confirmMappingClickButton = (Button) view.findViewById(R.id.confirm_mapping_click_button);
        confirmMappingClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PointF location = mappingImageView.getUnconfirmedPoint();
                mapWiFiDataListener.setLocation(location);
                if (location == null) return;
                if (isPermissionsGranted) {
                    dataUtils.startScanWifiData();
                }
                else Toast.makeText(getContext(), "WiFi scanning permissions not granted!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // upload data to firestore backend when 'End Mapping' button is clicked
        endMappingButton = (Button) view.findViewById(R.id.finish_mapping_button);
        endMappingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapDataList.isEmpty()) {
                    Toast.makeText(getContext(), "No data to upload!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Starting upload...", Toast.LENGTH_LONG).show();
                    for (MapData data : mapDataList) {
                        MapData preparedData = data.prepareForUpload(mappingImageView.getSWidth(),
                                mappingImageView.getSHeight());
                        FirestoreHelper.SetMapData dataSetter = new FirestoreHelper.SetMapData(preparedData);
                        dataSetter.execute(new OnCompleteCallback() {
                            @Override
                            public void onSuccess() {
                                // Toast.makeText(getContext(), "Data upload success!", Toast.LENGTH_SHORT).show();
                                saveMapDataAsJSON();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getContext(), "Data upload failed :/", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError() {
                            }
                        });
                    }

                    Toast.makeText(getContext(), "Upload Complete! :)", Toast.LENGTH_LONG).show();
                    mapDataList.clear();
                }
            }
        });
    }

    private void saveMapDataAsJSON() {
        String json_mapdata = new Gson().toJson(mapDataList);
        Prefs.setSavedMapping(getContext(), json_mapdata);
    }

    private MapData convertScanResultsToMapData(List<ScanResult> scanResults,
                                                PointF location, MapData currentMapData) {
        // Check if data is currently being mapped. If not, create a new mapdata
        // Otherwise update the existing mapdata (for >= 2nd iteration of scanning)
        if (currentMapData == null) {
            String name = Prefs.getCurrentLocation(getContext());
            String device = Build.MODEL;
            Timestamp timestamp = new Timestamp(new Date());
            String id = timestamp.toString() + "-" + name;

            Map<String, List<Integer>> mappedData = new HashMap<>();

            for (ScanResult result : scanResults) {
                String bssid = result.BSSID;
                int rssi = result.level;
                List<Integer> rssiValues = new ArrayList<>();
                rssiValues.add(rssi);
                mappedData.put(bssid, rssiValues);
            }

            MapData mapData = new MapData(name, mappedData, location, 1, device, timestamp);
            return mapData;
        } else {
            currentMapData = currentMapData.copy();
            Map<String, List<Integer>> currentData = currentMapData.getData();
            for (ScanResult result : scanResults) {
                String bssid = result.BSSID;
                int rssi = result.level;
                if (currentData.containsKey(bssid)) {
                    List<Integer> currentRSSIvalues = currentData.get(bssid);
                    currentRSSIvalues.add(rssi);
                    currentData.put(bssid, currentRSSIvalues);
                } else {
                    List<Integer> rssiValues = new ArrayList<>();
                    rssiValues.add(rssi);
                    currentData.put(bssid, rssiValues);
                }
            }
            currentMapData.setData(currentData);
            currentMapData.setLocation(location);

            return currentMapData;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WIFI_SCAN_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    isPermissionsGranted = true;
                } else {
                    Toast.makeText(getContext(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            default: {
                Toast.makeText(getContext(), "Permission Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // listener subclass which saves mapdata in the list every time user confirms a mapping point
    private class MapWiFiDataListener implements FetchWiFiDataUtils.FetchListener {
        PointF location;
        MapData currentMapData;

        public MapWiFiDataListener() {
            this.currentMapData = null;
        }

        public void setLocation(PointF location) {
            this.location = location;
        }

        public PointF getLocation() {
            return location;
        }

        @Override
        public void onScanResultsReceived(List<ScanResult> scanResults) {
            currentMapData = convertScanResultsToMapData(scanResults, location, currentMapData);
        }

        @Override
        public void onError(Throwable error) {
        }

        @Override
        public void finishAllScanning() {
            System.out.println("MapDataList: " + mapDataList);
            viewModel.addMapData(currentMapData);
            mappingImageView.comfirmPoint();
            currentMapData = null;
        }
    }

    // method called when view pin data option is selected after clicking on the pin in PinImageMapView
    @Override
    public void onViewPinData(PointF selectedPoint) {
        for (MapData mapData : mapDataList) {
            if (mapData.getLocation().equals(selectedPoint)) {
                PinDataPopUp pinPopUp = new PinDataPopUp(getContext(), mapData, selectedPoint);
                pinPopUp.show();
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // convert list of map data to json string and saved it to preferences
        String json_mapdata = new Gson().toJson(mapDataList);
        Prefs.setSavedMapping(getContext(), json_mapdata);
    }

    // listener method called when delete pin option is selected in PinImageMapView
    @Override
    public void onDeletePinData(PointF selectedPoint) {
        for (MapData mapData : mapDataList) {
            if (mapData.getLocation().equals(selectedPoint)) {
                mapDataList.remove(mapData);
                viewModel.removeMapData(mapData);
                break;
            }
        }
    }

    public List<MapData> getMapDataList() {
        return mapDataList;
    }
}