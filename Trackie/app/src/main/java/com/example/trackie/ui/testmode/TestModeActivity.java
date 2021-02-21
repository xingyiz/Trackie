package com.example.trackie.ui.testmode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Window;

import com.example.trackie.R;

public class TestModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set entry slide animation
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Transition fadeAnim = new Fade();
        fadeAnim.excludeTarget(R.id.use_mode_toolbar, true);
        fadeAnim.excludeTarget(android.R.id.statusBarBackground, true);
        fadeAnim.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fadeAnim);

        setContentView(R.layout.activity_test_mode);

    }
}