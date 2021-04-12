 package com.example.trackie;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.Prefs;
import com.example.trackie.ui.locations.LocationsFragment;

import com.example.trackie.ui.settings.SettingsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class LocationsScreenTest {

    Context context;

    @Rule
    public ActivityTestRule<MainActivity> mHomeActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

//    @Rule
//    public FragmentTestRule<?, LocationsFragment> fragmentLocationsRule =
//            FragmentTestRule.create(LocationsFragment.class);

    @Before
    public void Setup() {
        context = mHomeActivityTestRule.getActivity().getApplicationContext();
        TestingUtils.openDrawer();
        onView(withId(R.id.nav_locations))
                .perform(click());
    }

    @Test
    public void setLocation() throws Exception{
        for (int i = 0; i < 5; i++) {
            // get name of button to click
            onView(withId(R.id.locations_recycler_view))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
            // button 2 is cancel
            // button 1 is set
            onView(withId(android.R.id.button2)).perform(click());

        }
    }

}
