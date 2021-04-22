 package com.example.trackie;

 import android.content.Context;

 import androidx.recyclerview.widget.RecyclerView;
 import androidx.test.espresso.contrib.RecyclerViewActions;
 import androidx.test.ext.junit.runners.AndroidJUnit4;
 import androidx.test.rule.ActivityTestRule;

 import com.example.trackie.ui.MainActivity;
 import com.example.trackie.ui.Prefs;
 import com.google.api.client.googleapis.testing.TestUtils;

 import org.junit.Before;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.runner.RunWith;

 import static android.os.SystemClock.sleep;
 import static androidx.test.espresso.Espresso.onView;
 import static androidx.test.espresso.action.ViewActions.click;
 import static androidx.test.espresso.action.ViewActions.longClick;
 import static androidx.test.espresso.action.ViewActions.swipeLeft;
 import static androidx.test.espresso.assertion.ViewAssertions.matches;
 import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
 import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
 import static androidx.test.espresso.matcher.ViewMatchers.withChild;
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
        Prefs.setCurrentLocation(context, "B2L2");
        mHomeActivityTestRule.getActivity()
                .getFragmentManager().beginTransaction();
        navigateToScreen();

        sleep(2000);
    }

    @Test
    public void cancelLocation() throws Exception{
        for (int i = 0; i < 3; i++) {
            onView(withId(R.id.locations_recycler_view))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));

            // button 1 is set, button 2 is cancel
            onView(withId(android.R.id.button2))
                    .perform(click());
        }

        assert Prefs.getCurrentLocation(context).equals("B2L2");

    }

    @Test
    public void setB2L1() throws Exception{
        String text = "B2L1";
        setLoc(text);
        assert text.equals(Prefs.getCurrentLocation(context));
    }

    @Test
    public void setB2L2() throws Exception{
        String text = "B2L2";
        setLoc(text);
        assert text.equals(Prefs.getCurrentLocation(context));
    }

    @Test
    public void setCCL1() throws Exception{
        String text = "CCL1";
        setLoc(text);
        assert text.equals(Prefs.getCurrentLocation(context));
    }


    @Test
    public void setLocation() throws Exception{
        String text;
        for (int i = 0; i < 3; i++) {
            setLoc(i);
            navigateToScreen();

        }
    }

    @Test
    public void clickOptions() {
        clickOptionButton(0);
        onView(withText("Edit")).check(matches(isDisplayed()));

    }

    @Test
    public void testDelete() throws Exception{
        holdLoc(0);
        onView(withText("Confirm Delete?")).check(matches(isDisplayed()));
    }

    private void setLoc(int i) {
        onView(withId(R.id.locations_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));

        // button 1 is set, button 2 is cancel
        onView(withId(android.R.id.button1))
                .perform(click());
    }

    private void setLoc(String loc) {
        onView(withId(R.id.locations_recycler_view))
                .perform(RecyclerViewActions.actionOnItem(withChild(withText(loc)), click()));
        onView(withId(android.R.id.button1))
                .perform(click());
    }

    private void clickOptionButton(int i) {
        onView(withId(R.id.locations_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(i, TestingUtils.clickViewId(R.id.location_options_button)));

    }

    private void holdLoc(int i) {
        onView(withId(R.id.locations_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(i, longClick()));

    }

    private void navigateToScreen() {
        TestingUtils.openDrawer();
        onView(withId(R.id.nav_locations))
                .perform(click());
    }
}
