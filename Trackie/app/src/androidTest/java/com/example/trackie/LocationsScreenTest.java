package com.example.trackie;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.trackie.ui.locations.LocationsFragment;

import com.example.trackie.ui.settings.SettingsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class LocationsScreenTest {
    @Rule
    public FragmentTestRule<?, LocationsFragment> fragmentLocationsRule =
            FragmentTestRule.create(LocationsFragment.class);

    @Test
    public void addLocation() throws Exception{
        onView(withId(R.id.upload_location_button)).perform(click()).check(matches(isDisplayed()));

        //inside add locations page

    }

    @Test
    public void setLocation() throws Exception{
        for (int i = 0; i < 5; i++) {
            onView(withId(R.id.locations_recycler_view))
                    .perform(actionOnItemAtPosition(i, click()));

        }
    }

}
