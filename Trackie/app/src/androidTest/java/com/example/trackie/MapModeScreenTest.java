package com.example.trackie;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;

import androidx.fragment.app.Fragment;
import androidx.test.espresso.Root;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.*;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.trackie.database.MapData;
import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.Prefs;
import com.example.trackie.ui.locations.LocationsFragment;
import com.example.trackie.ui.mapmode.MapModeActivity;
import com.example.trackie.ui.mapmode.MappingMainFragment;
import com.example.trackie.ui.mapmode.PinImageMapView;
import com.example.trackie.ui.settings.SettingsFragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import static android.os.SystemClock.sleep;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static com.example.trackie.TestingUtils.waitId;
import static com.example.trackie.TestingUtils.waitText;
import static com.example.trackie.TestingUtils.waitTime;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MapModeScreenTest {

    Context context;

    @Rule
    public ActivityTestRule<MainActivity> mHomeActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    @Before
    public void yourSetUpFragment() {
        context = mHomeActivityTestRule.getActivity().getApplicationContext();
        Prefs.setCurrentLocation(context, "B2L2");
        Prefs.setAdminMode(context, true);

        mHomeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();
        onView(withId(R.id.map_mode_button))
                .perform(click());

    }

    @Test
    public void DropPin() throws Exception {
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());

        onView(instanceOf(PinImageMapView.class)).check(matches(isDisplayed()));
    }

    @Test
    public void ScaleMapTest() throws Exception {
        UiDevice myDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        myDevice.findObject(new UiSelector().description("map image view")).pinchIn(50, 100); //to zoom in
        myDevice.findObject(new UiSelector().description("map image view")).pinchOut(50, 100); //to zoom out
    }

    @Test
    public void ConfirmLocationButtonTest() throws Exception {
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());
        onView(withId(R.id.confirm_mapping_click_button))
                .perform(click());

       /* sleep(10000);

        MappingMainFragment current  = (MappingMainFragment) mHomeActivityTestRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.mappingMainFragment);
        List<MapData> data = current.getMapDataList();
        Assert.assertNotNull(data);*/
    }

    @Test
    public void EndMappingButtonTest() throws Exception {
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());
        onView(withId(R.id.confirm_mapping_click_button))
                .perform(click());
        onView(withId(R.id.finish_mapping_button))
                .perform(click());

        sleep(100);

      /*  onView(withText("Starting upload..."))
                .inRoot(withDecorView(not(is(mHomeActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));*/
    }

    /*@Test
    public void FeedbackDialogTest() throws Exception{
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());
        onView(withId(R.id.confirm_mapping_click_button))
                .perform(click());
        sleep(3000);
        onView(withId(R.id.finish_mapping_button))
                .perform(click());
        sleep(3000);

        onView(withId(R.id.ratingBar)).perform(click());
        onView(withId(R.id.ratingText)).perform(typeTextIntoFocusedView("hello"));
        onView(withId(R.id.ratingSubmit)).perform(click());
    }*/
}

