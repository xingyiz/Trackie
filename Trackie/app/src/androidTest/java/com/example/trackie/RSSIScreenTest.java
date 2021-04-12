package com.example.trackie;

import android.content.Context;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.trackie.ui.Prefs;
import com.example.trackie.ui.mapmode.RSSITestFragment;
import com.example.trackie.ui.settings.SettingsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.trackie.TestingUtils.waitId;

public class RSSIScreenTest {
    Context context;

    @Rule
    public FragmentTestRule<?, RSSITestFragment> fragmentTestRule =
            FragmentTestRule.create(RSSITestFragment.class);

    @Before
    public void Setup() {
        context = fragmentTestRule.getActivity().getApplicationContext();
        Prefs.setActiveScanningEnabled(context, true);
        Prefs.setDarkModeState(context, false);
    }

    @Test
    public void ScanTest() throws Exception{
        onView(withId(R.id.rssi_button)).perform(click());
        onView(isRoot()).perform(waitId(R.id.rssi_listview, TimeUnit.SECONDS.toMillis(1)));
        onView(withId(R.id.rssi_listview)).check(matches(isDisplayed()));
    }
}
