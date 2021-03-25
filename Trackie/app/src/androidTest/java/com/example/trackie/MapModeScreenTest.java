package com.example.trackie;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.trackie.ui.mapmode.MapModeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

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


//    @Test
//    public void ClickAddNoteButton_opensAddNoteUi() throws Exception{
//        onView(withId(R.id.top_toolbar))
//                .perform(click());
//        onView(withId(R.id.map_mode_host_fragment))
//                .check(matches(isDisplayed()));
//    }

    @Test
    public void SetupScreen() throws Exception{
        mMapModeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();

    }

    @Test
    public void ClickConfirmLocationButton() throws Exception {
        onView(withId(R.id.confirm_mapping_click_button))
                .perform(click());
        onView(withId(R.id.add_location_fragment_location))
                .check(matches(isDisplayed()));
    }

    @Test
    public void ClickEndMappingButton() throws Exception {
        onView(withId(R.id.finish_mapping_button))
                .perform(click());
        //onView(withId(R.id.mappingCompleteFragment))
               // .check(matches(isDisplayed()));
        onView(withText(R.string.no_data)).inRoot(withDecorView(not(mMapModeActivityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }
}
