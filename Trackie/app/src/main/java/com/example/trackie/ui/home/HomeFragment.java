package com.example.trackie.ui.home;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.ui.mapmode.MapModeActivity;
import com.example.trackie.ui.testmode.TestModeActivity;

public class HomeFragment extends Fragment {

    TextView currentLocationTextview;
    Button mapModeButton;
    Button testModeButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences(Utils.P_FILE, Context.MODE_PRIVATE);
        String currentLocationName = preferences.getString(Utils.CURRENT_LOCATION_KEY, "nil");
        currentLocationTextview = (TextView) view.findViewById(R.id.home_current_location_textview);
        currentLocationTextview.setText("Current Location: " + currentLocationName);

        mapModeButton = (Button) view.findViewById(R.id.map_mode_button);
        Toolbar top_toolbar = (Toolbar) view.findViewById(R.id.top_toolbar);
        mapModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getActivity(), MapModeActivity.class);
                startActivity(mapIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        testModeButton = (Button) view.findViewById(R.id.test_mode_button);
        testModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testIntent = new Intent(getActivity(), TestModeActivity.class);
                startActivity(testIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
    }
}