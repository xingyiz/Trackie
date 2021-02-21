package com.example.trackie.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackie.R;
import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.mapmode.MapModeActivity;
import com.example.trackie.ui.testmode.TestModeActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button map_mode_button = (Button) view.findViewById(R.id.map_mode_button);
        Toolbar top_toolbar = (Toolbar) view.findViewById(R.id.top_toolbar);
        map_mode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getActivity(), MapModeActivity.class);
                startActivity(mapIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        Button test_mode_button = (Button) view.findViewById(R.id.test_mode_button);
        test_mode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testIntent = new Intent(getActivity(), TestModeActivity.class);
                startActivity(testIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
    }
}