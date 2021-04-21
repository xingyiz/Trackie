package com.example.trackie;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.Prefs;
import com.example.trackie.ui.mapmode.PinImageMapView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;

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

        sleep(1500);
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
    }

    @Test
    public void EndMappingButtonTest() throws Exception {
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());
        onView(withId(R.id.confirm_mapping_click_button))
                .perform(click());
        onView(withId(R.id.finish_mapping_button))
                .perform(click());

/*        sleep(100);

        MappingMainFragment current  = (MappingMainFragment) mHomeActivityTestRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.mappingMainFragment);
        List<MapData> data = current.getMapDataList();
        Assert.assertNotNull(data);*/
    }
}

