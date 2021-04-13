package com.example.trackie.ui.mapmode;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.trackie.R;
import com.example.trackie.ui.Prefs;

public class MapModeActivity extends AppCompatActivity {
// TODO: if preferences empty (no current location) tell user to go back and select location
    String floorplanName;
    boolean darkModeEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_mode);

        System.out.println("OnCreate is called");
        floorplanName = Prefs.getCurrentLocation(getApplicationContext());
        darkModeEnabled = Prefs.getDarkModeState(getApplicationContext());


        // set up action bar
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Current Location: " + floorplanName);

        // if orientation is landscape, hide status and title bar
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                final WindowInsetsController insetsController = getWindow().getInsetsController();
                if (insetsController != null) {
                    insetsController.hide(WindowInsets.Type.statusBars());
                }
            } else {
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        NavController navController = Navigation.findNavController(this, R.id.map_mode_host_fragment);
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    public String getCurrentFloorplanName() {
        return floorplanName;
    }

    public boolean isDarkModeEnabled() {
        return darkModeEnabled;
    }
}