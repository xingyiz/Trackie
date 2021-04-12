package com.example.trackie.ui.testmode;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.database.StorageDownloader;
import com.example.trackie.ui.FetchWiFiDataUtils;
import com.example.trackie.ui.Prefs;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestingMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

// TODO: take care of landscape orientation changes
public class TestingMainFragment extends Fragment {

    private TestImageMapView testImageMapView;
    private Button alertTestingDiscrepencyButton;
    private Button endTestingButton;

    private FetchWiFiDataUtils dataUtils;
    private TestWiFiDataListener testWiFiDataListener;

    private ModelPrediction modelPrediction;
    private ArrayList<String> goodBSSIDs;
    private int size;
    private PointF currentPoint;

    private boolean retrievedBSSID = false;

    public TestingMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestingMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestingMainFragment newInstance(String param1, String param2) {
        TestingMainFragment fragment = new TestingMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TestingViewModel testingViewModel = new ViewModelProvider(requireActivity()).get(TestingViewModel.class);

        // long way first :(
        StorageDownloader storageDownloader = new StorageDownloader(Prefs.getCurrentLocation(getContext()), getContext());
        storageDownloader.execute(new OnCompleteCallback() {
            @Override
            public void onSuccess() {
                goodBSSIDs = storageDownloader.getGoodBSSIDs();
                retrievedBSSID = true;
                String credentials = requireContext().getResources().getString(R.string.credentials_key);
                modelPrediction = new ModelPrediction(credentials);
                size = storageDownloader.getSize();
                Toast.makeText(getContext(), "GOOD_BSSIDS file retrieved :)", Toast.LENGTH_SHORT).show();
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

        return inflater.inflate(R.layout.fragment_testing_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up map view
        testImageMapView = view.findViewById(R.id.testing_indoor_map_view);
        SharedPreferences preferences = getContext().getSharedPreferences(Utils.P_FILE, MODE_PRIVATE);
        String floorplanName = preferences.getString(Utils.CURRENT_LOCATION_KEY, "nil");
        if (!floorplanName.equals(null)) {
            FloorplanHelper.RetrieveFloorplan retrieveFloorplan = new FloorplanHelper.RetrieveFloorplan(floorplanName, getContext());
            retrieveFloorplan.execute(new OnCompleteCallback() {
                @Override
                public void onSuccess() {
                    StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(retrieveFloorplan.getFloorplanURL());
                    Glide.with(requireContext()).asBitmap()
                            .load(ref)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    testImageMapView.setImage(ImageSource.bitmap(resource));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getContext(), "Can't Retrieve Floorplan", Toast.LENGTH_SHORT).show();
                    testImageMapView = null;
                }

                @Override
                public void onError() {
                    Toast.makeText(getContext(), "Error Retrieving Floorplan", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Handle scanning of RSSI values
        testWiFiDataListener = new TestWiFiDataListener();
        dataUtils = new FetchWiFiDataUtils(getActivity(), testWiFiDataListener, false);
        dataUtils.scanWiFiDataIndefinitely();

        alertTestingDiscrepencyButton = view.findViewById(R.id.testing_discrepency_button);
        alertTestingDiscrepencyButton.setOnClickListener(v -> {
            System.out.println("WHAT WHAT WHAT");
            if (currentPoint != null) {
                testImageMapView.indicatePositionError(currentPoint);
            }
        });
        endTestingButton = view.findViewById(R.id.end_testing_button);
        endTestingButton.setOnClickListener(v -> {
            RatingDialogFragment rating = new RatingDialogFragment();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            rating.show(ft, "rating");
        });
    }

    private class TestWiFiDataListener implements FetchWiFiDataUtils.FetchListener {

        @Override
        public void onScanResultsReceived(List<ScanResult> scanResults) {
            if (testImageMapView == null) return;
            System.out.println("Received");

            try {
                if (retrievedBSSID) {
                    modelPrediction.getPrediction(preprocessInputData(scanResults), new ModelPrediction.OnReceivePredictionResultsCallback() {
                        @Override
                        public void onReceiveResults(double[] result) {
                            PointF predictedPoint = new PointF((float) result[0] * testImageMapView.getSWidth(),
                                    (float) result[1] * testImageMapView.getSHeight());
                            testImageMapView.updateCurrentUserLocation(predictedPoint);
                            currentPoint = predictedPoint;
                        }

                        @Override
                        public void onError() {
                            System.out.println("Failed to parse JSON prediction string. Check code under ModelPrediction.parsePredictionJSONForResult()");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Toast.makeText(getContext(), "Location has no saved BSSID values", Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    System.out.println("Could not make \"Location has no saved BSSID values\" toast!");
                }
            }
        }


        @Override
        public void onError(Throwable error) {
        }

        @Override
        public void finishAllScanning() {

        }
    }

    // preprocessing step which converts scanResults to list of rssi values
    private List<List<Double>> preprocessInputData(List<ScanResult> scanResults) {
        List<Double> inputData = new ArrayList<>(size * 2);
        for (int i = 0; i < size * 2; i++) {
            inputData.add(i, 0.0);
        }

        // get index from topBSSIDs, place RSSI in correct place
        for (ScanResult scanResult : scanResults) {
            for (int i = 0; i < size; i++) {
                if (goodBSSIDs.get(i).equals(scanResult.BSSID)) {
                    int index = goodBSSIDs.indexOf(scanResult.BSSID);
//                    Toast.makeText(getContext(), goodBSSIDs.get(index), Toast.LENGTH_SHORT).show();
                    inputData.set(index, 1.0);
                    inputData.set(index + size, (double) scanResult.level / -100.0);
                }
            }
        }

        // for BSSIDs that are not found in scanResults, put -1 as RSSI
        for (int i = 0; i < size; i++) {
            if (inputData.get(i) == 0.0) {
                inputData.set(i + size, -1.0);
            }
        }

        List<List<Double>> data = new ArrayList<>();
        data.add(inputData);

//        Toast.makeText(getContext(), data.toString(), Toast.LENGTH_SHORT).show();

        return data;
    }

    // stop scanning when testing fragment is exited
    @Override
    public void onPause() {
        super.onPause();
        dataUtils.stopScanning();
    }
}