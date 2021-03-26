package com.example.trackie;

import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;

import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.contrib.DrawerActions.close;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class TestingUtils {

    public static ViewAction clickXY(final int x, final int y){

       return new GeneralClickAction(Tap.SINGLE,
                new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {

                final int[] screenPos = new int[2];
                view.getLocationOnScreen(screenPos);

                final float screenX = screenPos[0] + x;
                final float screenY = screenPos[1] + y;
                float[] coordinates = {screenX, screenY};

                return coordinates;
            }
        },
        Press.FINGER);
    }

    public static void openDrawer() {
        onView(withId(R.id.drawer_layout))
                .perform(open()); // Open Drawer
    }

    public static void closeDrawer() {
        onView(withId(R.id.drawer_layout))
                .perform(close()); // Close Drawer
    }

    public static ViewAction doTaskInUIThread(final Runnable r) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                r.run();
            }
        };
    }


}
