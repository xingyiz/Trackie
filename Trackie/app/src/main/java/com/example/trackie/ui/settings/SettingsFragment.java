package com.example.trackie.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackie.R;

public class SettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editPrefs;
    String pFile = "com.example.trackie.ui.preferences";

    boolean buttonState;

    private SettingsViewModel settingsViewModel;
    SwitchCompat darkModeSwitch;
    EditText inputRSSIEditText;
    Button rssiButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        settingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        sharedPreferences = this.getActivity().getSharedPreferences(pFile, Context.MODE_PRIVATE);
        editPrefs = sharedPreferences.edit();
        buttonState = sharedPreferences.getBoolean("dark_mode_state", false);

        darkModeSwitch = root.findViewById(R.id.toggle_dark_mode);
        darkModeSwitch.setChecked(buttonState);
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set to dark mode if true
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    // light mode if false
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                editPrefs.putBoolean("dark_mode_state", isChecked);
                editPrefs.apply();
            }
        });

        inputRSSIEditText = root.findViewById(R.id.RSSIEditText);
        rssiButton = root.findViewById(R.id.RSSISetButton);

        rssiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputRSSI = inputRSSIEditText.getText().toString();

                try {

                    if (inputRSSI.isEmpty() || Integer.parseInt(inputRSSI) > 0) {
                        Toast.makeText(getContext(), "Invalid Input. Please enter a valid RSSI value.", Toast.LENGTH_SHORT).show();
                    } else {
                        editPrefs.putInt("measured_rssi", Integer.parseInt(inputRSSI));
                        editPrefs.apply();
                        Toast.makeText(getContext(), "Sucess. Measured RSSI is now " + inputRSSI, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Invalid Input. Please enter a number.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return root;
    }

}