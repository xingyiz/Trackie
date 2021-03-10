package com.example.trackie.ui.mapmode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Window;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.database.FloorplanData;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.locations.LocationsAdapter;
import com.example.trackie.ui.testmode.TestModeActivity;

public class MapModeActivity extends AppCompatActivity {
// TODO: if preferences empty (no current location) tell user to go back and select location
    String floorplanName;
    boolean darkModeEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_mode);

        SharedPreferences preferences = getSharedPreferences(Utils.P_FILE, MODE_PRIVATE);
        floorplanName = preferences.getString(Utils.CURRENT_LOCATION_KEY, "nil");
        darkModeEnabled = preferences.getBoolean("dark_mode_state", false);

        // set up action bar
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Current Location: " + floorplanName);
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