package com.example.trackie.ui.mapmode;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.MapData;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.ui.PinImageMapView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private Button confirmMappingClickButton;
    private Button endMappingButton;

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

        // set shared preferences and theme
        sharedPreferences = this.getActivity().getSharedPreferences(pFile, Context.MODE_PRIVATE);
        darkModeEnabled = sharedPreferences.getBoolean("dark_mode_state", false);

        return root;

    }

    // TODO: fix issue for when backgroudn image does not load by the time user clicks map mode
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SubsamplingScaleImageView mapping_image = (SubsamplingScaleImageView) view.findViewById(R.id.mapping_indoor_map_view);
        Bitmap mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b2_l1_light);

        if (!darkModeEnabled) {
            mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b2_l1);
        }

        TouchMapView mapView = new TouchMapView(getActivity(), TouchMapView.MAP_MODE, mapping_image, mapBitmap.copy(mapBitmap.getConfig(), false));


        mappingImageView = (PinImageMapView) view.findViewById(R.id.mapping_indoor_map_view);
        String floorplanName = "";
        if (getActivity() instanceof MapModeActivity) {
            floorplanName = ((MapModeActivity)getActivity()).getCurrentFloorplanName();
        }

        // TODO: change image loaded according to whether its dark mode or light mode
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

        confirmMappingClickButton = (Button) view.findViewById(R.id.confirm_mapping_click_button);
        confirmMappingClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mappingImageView.comfirmPoint();
                // map RSSI values here
            }
        });

        endMappingButton = (Button) view.findViewById(R.id.finish_mapping_button);
        endMappingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

}