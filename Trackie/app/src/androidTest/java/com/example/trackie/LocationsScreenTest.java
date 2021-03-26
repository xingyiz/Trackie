package com.example.trackie;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.trackie.ui.Prefs;
import com.example.trackie.ui.locations.LocationsFragment;

import com.example.trackie.ui.settings.SettingsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class LocationsScreenTest {

    Context context;

    @Rule
    public FragmentTestRule<?, LocationsFragment> fragmentLocationsRule =
            FragmentTestRule.create(LocationsFragment.class);

    @Before
    public void Setup() {
        context = fragmentLocationsRule.getActivity().getApplicationContext();
    }

    @Test
    public void setLocation() throws Exception{
        for (int i = 0; i < 5; i++) {
            // get name of button to click

            onView(withId(R.id.locations_recycler_view))
                    .perform(actionOnItemAtPosition(i, click()));

        }
    }

}
