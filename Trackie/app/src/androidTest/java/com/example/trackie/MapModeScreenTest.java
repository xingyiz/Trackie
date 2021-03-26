package com.example.trackie;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.*;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.ui.mapmode.MapModeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;

@RunWith(AndroidJUnit4.class)
public class MapModeScreenTest {

    @Rule
    public ActivityTestRule<MapModeActivity> mMapModeActivityTestRule =
            new ActivityTestRule<MapModeActivity>(MapModeActivity.class);



    @Before
    public void yourSetUpFragment(){
        mMapModeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();

    }

    @Test
    public void displayScreen() throws Exception{
        SubsamplingScaleImageView imageView = mMapModeActivityTestRule.getActivity().findViewById(R.id.mapping_indoor_map_view);
        imageView.setImage(ImageSource.resource(R.drawable.b2_l1));
        mMapModeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();

    }


    @Test
    public void DropPin() throws Exception{
        onView(withId(R.id.mapping_indoor_map_view))
                .perform(click());

    }

    @Test
    public void ScaleMapTest() throws Exception{
        UiDevice myDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        myDevice.findObject(new UiSelector().description("map image view")).pinchIn(50, 100); //to zoom in
        myDevice.findObject(new UiSelector().description("map image view")).pinchOut(50, 100); //to zoom out
    }

    @Test
    public void ConfirmLocationButtonTest() throws Exception{

    }

    @Test
    public void EndMappingButtonTest() throws Exception{

    }
}
