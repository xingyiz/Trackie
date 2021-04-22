package com.example.trackie.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.example.trackie.ui.Prefs;

public class SettingsFragment extends Fragment {

    Context context;

    boolean buttonState;

    private SettingsViewModel settingsViewModel;
    SwitchCompat darkModeSwitch;

    EditText inputRSSIEditText;
    Button rssiButton;
    SwitchCompat activeScanningSwitch;
    EditText numberOfScansEditText;
    Button numberOfScansButton;

    // Test Mode Models
    SwitchCompat MLModeToggle;
    SwitchCompat regressionToggle;

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

        context = root.getContext();
        buttonState = Prefs.getDarkModeState(getContext());

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
                Prefs.setDarkModeState(getContext(), isChecked);
            }
        });

        activeScanningSwitch = root.findViewById(R.id.toggle_active_scanning);
        activeScanningSwitch.setChecked(Prefs.getActiveScanningEnabled(getContext()));
        activeScanningSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.setActiveScanningEnabled(getContext(), isChecked);
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
                        Prefs.setMeasuredRSSI(getContext(), Integer.parseInt(inputRSSI));
                        Toast.makeText(getContext(), "Sucess. Measured RSSI is now " + inputRSSI, Toast.LENGTH_SHORT).show();
                        hideKeyboard(inputRSSIEditText);
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Invalid Input. Please enter a number.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        numberOfScansEditText = root.findViewById(R.id.number_of_scans_edittext);
        numberOfScansEditText.setHint(String.valueOf(Prefs.getNumberOfScans(getContext())));
        numberOfScansButton = root.findViewById(R.id.number_of_scans_set_button);
        numberOfScansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberOfScans = numberOfScansEditText.getText().toString();

                try {
                    if (numberOfScans.isEmpty() || Integer.parseInt(numberOfScans) <= 0 || Integer.parseInt(numberOfScans) > 10) {
                        Toast.makeText(getContext(), "Invalid Input. Please enter a valid number less than 10", Toast.LENGTH_SHORT).show();
                    } else {
                        Prefs.setNumberOfScans(getContext(), Integer.parseInt(numberOfScans));
                        Toast.makeText(getContext(), "Success. Number of times to scan is now " + numberOfScans, Toast.LENGTH_SHORT).show();
                        hideKeyboard(numberOfScansEditText);
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Invalid Input. Please enter a number.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MLModeToggle = root.findViewById(R.id.toggle_ML_Mode);
        String model = Prefs.getModelType(context);

        // Create view on instantiated
        // reg is true, clf is false
            switch (model) {
            case ("reg"):
                MLModeToggle.setText("Regression");
                MLModeToggle.setChecked(true);
                break;
            case ("clf"):
                MLModeToggle.setText("Classificaton");
                MLModeToggle.setChecked(false);
                break;
        }

        MLModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MLModeToggle.setText("Regression");
                    MLModeToggle.setChecked(true);
                    Prefs.setModelType(context, "reg");

                } else {
                    MLModeToggle.setText("Classificaton");
                    MLModeToggle.setChecked(false);
                    Prefs.setModelType(context, "clf");
                }
            }
        });


        return root;
    }

    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

}