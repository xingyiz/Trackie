package com.example.trackie.ui.mapmode;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.database.MapData;
import com.example.trackie.ui.PinImageMapView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MappingMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MappingMainFragment extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MAP_DATA_KEY = "MapData";
    private static MapData mapData;
    private PinImageMapView mappingImageView;

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
        View root = inflater.inflate(R.layout.fragment_mapping_main, container, false);


        // TODO: Code for getting and loading in correct map + correct colour
        SubsamplingScaleImageView map = root.findViewById(R.id.mapping_indoor_map_view);
        // set shared preferences and theme
        sharedPreferences = this.getActivity().getSharedPreferences(pFile, Context.MODE_PRIVATE);
        darkModeEnabled = sharedPreferences.getBoolean("dark_mode_state", false);

        if (darkModeEnabled) {
            // get white image

        } else {
            // get dark image
        }

        return root;

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mappingImageView = (PinImageMapView) view.findViewById(R.id.mapping_indoor_map_view);
        Bitmap mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b2_l1);
        mappingImageView.setImage(ImageSource.bitmap(mapBitmap));

        Button confirmMappingClickButton = (Button) view.findViewById(R.id.confirm_mapping_click_button);
        confirmMappingClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mappingImageView.setConfirmedPoint(true);
                // map RSSI values here
            }
        });

    }

}