package com.example.trackie;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.trackie.ui.Prefs;
import com.example.trackie.ui.settings.SettingsFragment;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class SettingsScreenTest {

    Context context;

    @Rule
    public FragmentTestRule<?, SettingsFragment> fragmentTestRule =
            FragmentTestRule.create(SettingsFragment.class);

    @Before
    public void Setup() {
        context = fragmentTestRule.getActivity().getApplicationContext();
        Prefs.setActiveScanningEnabled(context, false);
        Prefs.setDarkModeState(context, false);
    }

    @Test
    public void TestDarkToggle() throws Exception{
        //checked
        onView(withId(R.id.toggle_dark_mode))
                .perform(click())
                .check(matches(isEnabled()))
                .check(matches(new CustomMatchers.BoolPrefsMatcher(CustomMatchers.DARK_MODE, true, context)));

        //unchecked
        onView(withId(R.id.toggle_dark_mode))
                .perform(click())
                .check(matches(isNotChecked()))
                .check(matches(new CustomMatchers.BoolPrefsMatcher(CustomMatchers.DARK_MODE, false, context)));
    }

    /*@Test
    public void TestSetActiveScanning() throws Exception{
        //checked
        onView(withId(R.id.toggle_active_scanning))
                .perform(click())
                .check(matches(isChecked()))
                .check(matches(new CustomMatchers.BoolPrefsMatcher(CustomMatchers.ACTIVE_SCANNING, true,context)));

        //unchecked
        onView(withId(R.id.toggle_active_scanning))
                .perform(click())
                .check(matches(isNotChecked()))
                .check(matches(new CustomMatchers.BoolPrefsMatcher(CustomMatchers.ACTIVE_SCANNING, false, context)));
    }
*/
    @Test
    public void TestMeasuredRSSI() throws Exception{
        // set to 55 and check prefs
        // change to -55 to pass
        String data = "-55";
        onView(withId(R.id.RSSIEditText))
                .perform(click())
                .perform(typeTextIntoFocusedView(data));

        onView(withId(R.id.RSSISetButton))
                .perform(click())
                .check(matches(new CustomMatchers.StringPrefsMatcher(CustomMatchers.MEASURED_RSSI, data, context)));
    }

    @Test
    public void TestScansPerPin() throws Exception{
        // set to 5 and check prefs
        String data = "5";
        onView(withId(R.id.number_of_scans_edittext))
                .perform(click())
                .perform(typeTextIntoFocusedView(data));

        onView(withId(R.id.number_of_scans_set_button))
                .perform(click())
                .check(matches(new CustomMatchers.StringPrefsMatcher(CustomMatchers.NUM_OF_SCANS, data, context )));

    }

    @Test (expected = NumberFormatException.class)
    public void InvalidNumScans1() throws Exception {
        String data = "abc";

      onView(withId(R.id.number_of_scans_edittext))
                .perform(click())
                .perform(typeTextIntoFocusedView(data));

      onView(withId(R.id.number_of_scans_set_button))
              .perform(click())
              .check(matches(new CustomMatchers.StringPrefsMatcher(CustomMatchers.NUM_OF_SCANS, data, context )));
    }

    @Test (expected = AssertionError.class)
    public void InvalidNumScans2() throws Exception{
        String data = "100";

        onView(withId(R.id.number_of_scans_edittext))
                .perform(click())
                .perform(typeTextIntoFocusedView(data));

        onView(withId(R.id.number_of_scans_set_button))
                .perform(click())
                .check(matches(new CustomMatchers.StringPrefsMatcher(CustomMatchers.NUM_OF_SCANS, data, context )));
    }

    @Test
    public void ModelMode() throws Exception{

        onView(withId(R.id.toggle_ML_Mode))
                .perform(click())
                .check(matches(new CustomMatchers.StringPrefsMatcher(CustomMatchers.ML_MODEL, CustomMatchers.ML_REG, context)));

        onView(withId(R.id.toggle_ML_Mode))
                .perform(click())
                .check(matches(new CustomMatchers.StringPrefsMatcher(CustomMatchers.ML_MODEL, CustomMatchers.ML_CLF, context)));

    }

}
