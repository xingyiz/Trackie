package com.example.trackie;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.trackie.ui.mapmode.MapModeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;

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
    public void ClickAddNoteButton_opensAddNoteUi() throws Exception{
        onView().perform().check():
    }
}
