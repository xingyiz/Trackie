package com.example.trackie.ui.home;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.database.FloorplanData;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.ui.AdminDialogFragment;
import com.example.trackie.ui.Prefs;
import com.example.trackie.ui.locations.LocationsFragment;
import com.example.trackie.ui.locations.LocationsViewModel;
import com.example.trackie.ui.mapmode.MapModeActivity;
import com.example.trackie.ui.testmode.TestModeActivity;
import com.example.trackie.ui.testmode.TestingViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    TextView currentLocationTextview;
    Button mapModeButton;
    Button testModeButton;
    Button setLocationButton;
    NavController controller;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // load locations
        LocationsViewModel locationsViewModel = new ViewModelProvider(requireActivity()).get(LocationsViewModel.class);
        // load bssids
//        TestingViewModel testingViewModel = new ViewModelProvider(requireActivity()).get(TestingViewModel.class);
//        testingViewModel.loadGoodBSSIDs(Prefs.getCurrentLocation(getContext()), getContext());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences(Utils.P_FILE, Context.MODE_PRIVATE);
        String currentLocationName = preferences.getString(Utils.CURRENT_LOCATION_KEY, "nil");
        currentLocationTextview = (TextView) view.findViewById(R.id.home_current_location_textview);
        currentLocationTextview.setText("Current Location: " + currentLocationName);

        controller = Navigation.findNavController(view);

        mapModeButton = (Button) view.findViewById(R.id.map_mode_button);
        Toolbar top_toolbar = (Toolbar) view.findViewById(R.id.top_toolbar);
        mapModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Prefs.getAdminMode(getContext())) {
                    Intent mapIntent = new Intent(getActivity(), MapModeActivity.class);
                    startActivity(mapIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                } else {
                    AdminDialogFragment admin = new AdminDialogFragment();
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    admin.show(ft, "admin");
                }
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

        setLocationButton = (Button) view.findViewById(R.id.set_location_button);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.nav_locations);
            }
        });
    }
}