package com.example.trackie.ui.testmode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.Prefs;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

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

    private boolean alreadyCheckedWrongLocation = false;
    private boolean retrievedBSSID = false;
    private long startTime;

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

        startTime = System.currentTimeMillis();

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
            dataUtils.stopScanning();

            long endTimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
            int endMinutes = (int) endTimeSeconds / 60;
            int endSeconds = (int) endTimeSeconds % 60;
            String timeTakenString = endMinutes + " m " + endSeconds + " s";
            RatingDialogFragment ratingFragment = new RatingDialogFragment(testImageMapView.getErrorPoints().size(), timeTakenString);
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ratingFragment.show(ft, "rating");
        });
    }

    private class TestWiFiDataListener implements FetchWiFiDataUtils.FetchListener {
        private boolean updateMap = true;   // checker to stop background from moving when incorrect location alert dialog is shown
        @Override
        public void onScanResultsReceived(List<ScanResult> scanResults) {
            if (testImageMapView == null) return;
            try {
                if (retrievedBSSID) {
                    List<List<Double>> inputData = preprocessInputData(scanResults);
                    if (inputData == null && updateMap) {    // check if no suitable BSSIDs are found - means user not at location
                        updateMap = false;
                        if (alreadyCheckedWrongLocation) return;
                        Toast.makeText(getContext(), "Not at the right location!", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Wrong Location!")
                                .setMessage("Please change to the correct location")
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                    dataUtils.stopScanning();
                                    // open up locations selection from MainActivity
                                    // (MainActivity responsible for handling switching of fragments to LocationsFragment)
                                    Intent locationIntent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(locationIntent);
                                })
                                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                    alreadyCheckedWrongLocation = true; // no need to show dialog fragment again
                                    updateMap = true;
                                    dataUtils.scanWiFiDataIndefinitely();
                                }).show();

                    }
                    modelPrediction.getPrediction(inputData, new ModelPrediction.OnReceivePredictionResultsCallback() {
                        @Override
                        public void onReceiveResults(double[] result) {
                            if (!updateMap) return;
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
        int missingBSSIDsCount = 0;
        for (int i = 0; i < size; i++) {
            if (inputData.get(i) == 0.0) {
                missingBSSIDsCount++;
                inputData.set(i + size, -1.0);
            }
        }

        // return null if no suitable BSSIDs are found
        if (missingBSSIDsCount == size) return null;

        List<List<Double>> data = new ArrayList<>();
        data.add(inputData);

        return data;
    }

    // stop scanning when testing fragment is exited
    @Override
    public void onPause() {
        super.onPause();
        dataUtils.stopScanning();
    }
}