package com.example.trackie;

import android.app.Activity;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.*;

import com.example.trackie.ui.MainActivity;
import com.example.trackie.ui.Prefs;
import com.example.trackie.ui.mapmode.MapModeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MapModeScreenTest {

    Context context;

    @Rule
    /*public ActivityTestRule<MapModeActivity> mMapModeActivityTestRule =
            new ActivityTestRule<MapModeActivity>(MapModeActivity.class);
*/
    public ActivityTestRule<MainActivity> mHomeActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);



    @Before
    public void yourSetUpFragment(){

        Prefs.setCurrentLocation(mHomeActivityTestRule.getActivity().getApplicationContext(), "B2L2");
        mHomeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();
        onView(withId(R.id.map_mode_button))
                .perform(click());

       /* context = mMapModeActivityTestRule.getActivity().getApplicationContext();
        Prefs.setCurrentLocation(context,"B2L1");
        mMapModeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();
*/
   /*   PinImageMapView imageView = mMapModeActivityTestRule.getActivity().findViewById(R.id.mapping_indoor_map_view);
        imageView.setImage(ImageSource.resource(R.drawable.b2_l1));*/

    }

    @Test
    public void Login() throws Exception{

    }

    @Test public void FailLogin() throws Exception{

    }

    @Test
    public void DropPin() throws Exception{
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());

        // check pin is displayed

    }

    @Test
    public void ScaleMapTest() throws Exception{
        UiDevice myDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        myDevice.findObject(new UiSelector().description("map image view")).pinchIn(50, 100); //to zoom in
        myDevice.findObject(new UiSelector().description("map image view")).pinchOut(50, 100); //to zoom out
    }

    @Test
    public void ConfirmLocationButtonTest() throws Exception{
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());
        onView(withId(R.id.confirm_mapping_click_button))
            .perform(click());

        onView(withText("Scanning..."))
                .inRoot(withDecorView(not(is(mHomeActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void EndMappingButtonTest() throws Exception{
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());
        onView(withId(R.id.confirm_mapping_click_button))
                .perform(click());
        onView(withId(R.id.finish_mapping_button))
                .perform(click());

        onView(withText("Starting upload..."))
                .inRoot(withDecorView(not(is(mHomeActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}
