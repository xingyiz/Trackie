package com.example.trackie.ui.testmode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Window;

import com.example.trackie.R;
import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.mapmode.MapModeActivity;

public class TestModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_mode);

        // set up action bar
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Current Location");
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent homeIntent = new Intent(TestModeActivity.this, MainActivity.class);
        startActivity(homeIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
        return true;
    }
}