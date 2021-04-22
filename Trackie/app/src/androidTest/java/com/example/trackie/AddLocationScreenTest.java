package com.example.trackie;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.EspressoKey;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.trackie.ui.locations.AddLocationFragment;
import com.example.trackie.ui.locations.LocationsFragment;

import com.example.trackie.ui.settings.SettingsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import static android.os.SystemClock.sleep;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static com.google.android.material.internal.ContextUtils.getActivity;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class AddLocationScreenTest {

    Context context;

    @Rule
    public FragmentTestRule<?, AddLocationFragment> fragmentLocationsRule =
            FragmentTestRule.create(AddLocationFragment.class);

    @Before
    public void setupFragment() {
        context = fragmentLocationsRule.getFragment().getContext();
    }

    @Test
    public void EnterName() throws Exception{
        onView(allOf(supportsInputMethods(), isDescendantOfA(withId(R.id.upload_location_input_layout))))
                .perform(click())
                .perform(typeText("Testing Function"))
                .check(matches(isDisplayed()));
    }


    @Test //(expected = IllegalArgumentException.class)
    public void TestLongName() throws Exception{
        onView(allOf(supportsInputMethods(), isDescendantOfA(withId(R.id.upload_location_input_layout))))
                .perform(click())
                .perform(typeText("abcdefghijklmnopqrstuv"))
                .perform(pressKey(KeyEvent.KEYCODE_ENTER));
}

    /*@Test
    public void TestCheckbox() throws Exception{
        onView(withId(R.id.upload_floorplan_checkbox))
                .perform(click())
                .check(matches(isChecked()));
    }*/

    @Test
    public void TestCheckUncheck() throws Exception{
        for (int i = 0; i < 3 ; i++) {

            onView(withId(R.id.upload_floorplan_checkbox))
                    .perform(click())
                    .check(matches(isChecked()));

            onView(withId(R.id.upload_floorplan_checkbox))
                    .perform(click())
                    .check(matches(isNotChecked()));
        }
    }

}
