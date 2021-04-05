package com.example.trackie.ui.testmode;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestingMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

// TODO: take care of landscape orientation changes
public class TestingMainFragment extends Fragment {

    private TestImageMapView testImageMapView;
    private FetchWiFiDataUtils dataUtils;
    private TestWiFiDataListener testWiFiDataListener;

    private ModelPrediction modelPrediction;
    private ArrayList<String> goodBSSIDs;
    private int size;

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
//        modelPrediction = new ModelPrediction(testingViewModel.getGoodBSSIDs(), testingViewModel.getSize());

        // long way first :(
        StorageDownloader storageDownloader = new StorageDownloader(Prefs.getCurrentLocation(getContext()), getContext());
        storageDownloader.execute(new OnCompleteCallback() {
            @Override
            public void onSuccess() {
                goodBSSIDs = storageDownloader.getGoodBSSIDs();
                modelPrediction = new ModelPrediction(goodBSSIDs, size);
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
    }

    private class TestWiFiDataListener implements FetchWiFiDataUtils.FetchListener {

        @Override
        public void onScanResultsReceived(List<ScanResult> scanResults) {
            if (testImageMapView == null) return;
            // TODO: function which uses the data to get the location estimated by the algorithm
            Random random = new Random();

            PointF testPoint = new PointF(random.nextFloat() * testImageMapView.getSWidth(),
                                          random.nextFloat() * testImageMapView.getSHeight());
            testImageMapView.updateCurrentUserLocation(testPoint);
            try {
                modelPrediction.getPrediction(scanResults);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Location has no saved BSSID values", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public void onError(Throwable error) {
        }

        @Override
        public void finishAllScanning() {

        }
    }
}