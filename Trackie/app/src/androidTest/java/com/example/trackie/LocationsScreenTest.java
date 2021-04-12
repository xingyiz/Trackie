 package com.example.trackie;

 import android.content.Context;

 import androidx.test.espresso.contrib.RecyclerViewActions;
 import androidx.test.ext.junit.runners.AndroidJUnit4;
 import androidx.test.rule.ActivityTestRule;

 import com.example.trackie.ui.MainActivity;
 import com.example.trackie.ui.Prefs;

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
 import static androidx.test.espresso.matcher.ViewMatchers.withText;

 @RunWith(AndroidJUnit4.class)
public class LocationsScreenTest {

    Context context;

    @Rule
    public ActivityTestRule<MainActivity> mHomeActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void Setup() {
        context = mHomeActivityTestRule.getActivity().getApplicationContext();
        mHomeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();
        TestingUtils.openDrawer();
        onView(withId(R.id.nav_locations))
                .perform(click());
    }

    @Test
    public void setLocation() throws Exception{
        for (int i = 0; i < 3; i++) {
            clickItem(i);
        }
    }

    @Test
    private void clickOptions() throws Exception{
        onView(withId(R.id.location_options_button))
                .perform(click());
        onView(withText("Edit")).check(matches(isDisplayed()));
    }

    private void clickItem(int i) {
        // get name of button to click
        onView(withId(R.id.locations_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
        // button 1 is set, button 2 is cancel
        onView(withId(android.R.id.button2)).perform(click());
    }
}
