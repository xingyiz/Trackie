 package com.example.trackie;

 import android.content.Context;

 import androidx.test.espresso.contrib.RecyclerViewActions;
 import androidx.test.ext.junit.runners.AndroidJUnit4;
 import androidx.test.rule.ActivityTestRule;

 import com.example.trackie.ui.MainActivity;

 import org.junit.Before;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.runner.RunWith;

 import static android.os.SystemClock.sleep;
 import static androidx.test.espresso.Espresso.onView;
 import static androidx.test.espresso.action.ViewActions.click;
 import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LocationsScreenTest {

    Context context;

    @Rule
    public ActivityTestRule<MainActivity> mHomeActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

//    @Rule
//    public FragmentTestRule<?, LocationsFragment> fragmentLocationsRule =
//            FragmentTestRule.create(LocationsFragment.class);

    @Before
    public void Setup() {
        context = mHomeActivityTestRule.getActivity().getApplicationContext();
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

    private void clickItem(int i) {
        // get name of button to click
        onView(withId(R.id.locations_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
        // button 2 is cancel
        // button 1 is set
        onView(withId(android.R.id.button2)).perform(click());
    }

}
