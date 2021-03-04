package com.example.trackie.ui.mapmode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.database.MapData;
import com.example.trackie.ui.PinImageMapView;
import com.example.trackie.ui.TouchMapView;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MappingMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MappingMainFragment extends Fragment implements Observer {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MAP_DATA_KEY = "MapData";
    private static MapData mapData;

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
        args.putParcelable(MAP_DATA_KEY, mapData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mapData = getArguments().getParcelable(MAP_DATA_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapping_main, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        SubsamplingScaleImageView mapping_image = (SubsamplingScaleImageView) view.findViewById(R.id.mapping_indoor_map_view);
//        Bitmap mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b2_l1);
//        TouchMapView mapView = new TouchMapView(getActivity(), TouchMapView.MAP_MODE, mapping_image, mapBitmap.copy(mapBitmap.getConfig(), false));

//        Button confirmMappingClickButton = (Button) view.findViewById(R.id.confirm_mapping_click_button);
//        confirmMappingClickButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mapView.setHasUnconfirmedPoint(false);
//            }
//        });

        PinImageMapView mapping_image = (PinImageMapView) view.findViewById(R.id.mapping_indoor_map_view);
        Bitmap mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b2_l1);
        mapping_image.setImage(ImageSource.bitmap(mapBitmap));

    }

    // Observer method to detect when current coordinates in the TouchMapView has changed
    // Reads the RSSI values and updates the current data
    @Override
    public void update(Observable o, Object arg) {

    }
}