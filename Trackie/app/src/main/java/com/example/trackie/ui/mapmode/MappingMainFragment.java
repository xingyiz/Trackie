package com.example.trackie.ui.mapmode;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.database.FirestoreHelper;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.MapData;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.ui.FetchWiFiDataUtils;
import com.example.trackie.ui.PinImageMapView;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MappingMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MappingMainFragment extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MAP_DATA_KEY = "MapData";
    private static List<MapData> mapDataList;
    private PinImageMapView mappingImageView;
    private Button confirmMappingClickButton;
    private Button endMappingButton;
    private final static int WIFI_SCAN_PERMISSIONS = 123;
    private boolean isPermissionsGranted;

    private FetchWiFiDataUtils dataUtils;
    private FetchWiFiDataUtils.FetchListener dataListener;

    SharedPreferences sharedPreferences;
    String pFile = "com.example.trackie.ui.preferences";
    boolean darkModeEnabled;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mapDataList = getArguments().getParcelable(MAP_DATA_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_mapping_main, container, false);
        return root;

    }

    // TODO: fix issue for when backgroudn image does not load by the time user clicks map mode
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mappingImageView = (PinImageMapView) view.findViewById(R.id.mapping_indoor_map_view);
        String floorplanName = "";
        if (getActivity() instanceof MapModeActivity) {
            floorplanName = ((MapModeActivity)getActivity()).getCurrentFloorplanName();
        }

        if (mapDataList == null) mapDataList = new ArrayList<>();
        // TODO: change image loaded according to whether its dark mode or light mode
        if (!floorplanName.equals("")) {
            FloorplanHelper.RetrieveFloorplan retrieveFloorplan = new FloorplanHelper.RetrieveFloorplan(floorplanName);
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

        final int maxedScannedTimes = 5;
        MapWiFiDataListener listener = new MapWiFiDataListener(maxedScannedTimes);
        dataUtils = new FetchWiFiDataUtils(getActivity(), isPermissionsGranted, listener, 5);
        isPermissionsGranted = dataUtils.getPermissionGranted();


        confirmMappingClickButton = (Button) view.findViewById(R.id.confirm_mapping_click_button);
        confirmMappingClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PointF location = mappingImageView.getUnconfirmedPoint();
                if (location == null) return;
                listener.setLocation(location);
                boolean success = dataUtils.scanWiFiData();
            }
        });

        endMappingButton = (Button) view.findViewById(R.id.finish_mapping_button);
        endMappingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (MapData data : mapDataList) {
                    FirestoreHelper.SetMapData dataSetter = new FirestoreHelper.SetMapData(data);
                    dataSetter.execute(new OnCompleteCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Data upload success!", Toast.LENGTH_SHORT).show();
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
            }
        });

    }

    private MapData convertScanResultsToMapData(List<ScanResult> scanResults,
                                                PointF location, MapData currentMapData) {
        if (currentMapData == null) {
            SharedPreferences preferences = getContext().getSharedPreferences(Utils.P_FILE, Context.MODE_PRIVATE);
            String name = preferences.getString(Utils.CURRENT_LOCATION_KEY, "nil");
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

    // listener subclass which saves mapdata in a list every time user confirms a mapaing point
    private class MapWiFiDataListener implements FetchWiFiDataUtils.FetchListener {
        PointF location;
        MapData currentMapData;

        public MapWiFiDataListener(int maxScanTimes) {
            this.currentMapData = null;
        }

        public void setLocation(PointF location) {
            this.location = location;
        }

        @Override
        public void onScanResultsReceived(List<ScanResult> scanResults) {
            MapData mapData = convertScanResultsToMapData(scanResults, location, currentMapData);
            currentMapData = mapData;
            Toast.makeText(getContext(), scanResults.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable error) {
        }

        @Override
        public void finishScanning() {
            mapDataList.add(currentMapData);
            mappingImageView.comfirmPoint();
        }
    }

}