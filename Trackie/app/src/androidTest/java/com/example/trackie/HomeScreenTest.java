package com.example.trackie;

import android.view.Gravity;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.Prefs;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.contrib.DrawerActions.*;
import static androidx.test.espresso.contrib.DrawerMatchers.*;


@RunWith(AndroidJUnit4.class)
public class HomeScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> mHomeActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    @Before
    public void yourSetUpFragment(){
        Prefs.setCurrentLocation(mHomeActivityTestRule.getActivity().getApplicationContext(), "B2L2");
        mHomeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();
    }

    @Test
    public void SetupScreen() throws Exception{
        mHomeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();

    }

    @Test
    public void checkDrawer() throws Exception {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.RIGHT))) // Left Drawer should be closed.
                .perform(close()); // Close Drawer
    }

    @Test
    public void ClickNavHomeButton() throws Exception{

        TestingUtils.openDrawer();

        onView(withId(R.id.nav_home))
                .perform(click());

        onView(withId(R.id.home_fragment_container))
                .check(matches(isDisplayed()));

    }

    @Test
    public void ClickNavLocationsButton() throws Exception{
       TestingUtils.openDrawer();
        onView(withId(R.id.nav_locations))
                .perform(click());
        onView(withId(R.id.locations_recycler_view))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickNavSettingButton() throws Exception{
        TestingUtils.openDrawer();
        onView(withId(R.id.nav_settings))
                .perform(click());
        onView(withId(R.id.setting_fragment_container))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickNavDatabaseButton() throws Exception{
        TestingUtils.openDrawer();
        onView(withId(R.id.nav_database))
                .perform(click());
        onView(withId(R.id.database_display))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickNavTestRssiButton() throws Exception{
        TestingUtils.openDrawer();
        onView(withId(R.id.nav_test_rssi))
                .perform(click());
        onView(withId(R.id.rssi_listview))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickMapModeButton() throws Exception{
        onView(withId(R.id.map_mode_button))
                .perform(click());
        onView(withId(R.id.map_mode_host_fragment))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickTestModeButton() throws Exception{
        onView(withId(R.id.test_mode_button))
                .perform(click());
        onView(withId(R.id.test_mode_host_fragment))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickSetLocationsButton() throws Exception{
        onView(withId(R.id.set_location_button))
                .perform(click());
        onView(withId(R.id.locations_recycler_view))
                .check(matches(isDisplayed()));

    }


}
