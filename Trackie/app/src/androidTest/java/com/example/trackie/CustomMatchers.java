package com.example.trackie;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatButton;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.PreferenceMatchers;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.trackie.ui.Prefs;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CustomMatchers {

    public static final String MEASURED_RSSI = "MeasuredRSSI";
    public static final String NUM_OF_SCANS = "NumberOfScans";
    public static final String LOCATION = "Location";
    public static final String DARK_MODE = "DarkMode";
    public static final String ACTIVE_SCANNING = "ActiveScanning";


    public static class DrawableMatcher extends TypeSafeMatcher<View> {

        private @DrawableRes
        final int expectedId;
        String resourceName;

        public DrawableMatcher(@DrawableRes int expectedId) {
            super(View.class);
            this.expectedId = expectedId;
        }

        @Override
        protected boolean matchesSafely(View target) {
            //Type check we need to do in TypeSafeMatcher
            if (!(target instanceof ImageView)) {
                return false;
            }
            //We fetch the image view from the focused view
            ImageView imageView = (ImageView) target;
            if (expectedId < 0) {
                return imageView.getDrawable() == null;
            }
            //We get the drawable from the resources that we are going to compare with image view source
            Resources resources = target.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(expectedId);
            resourceName = resources.getResourceEntryName(expectedId);

            if (expectedDrawable == null) {
                return false;
            }
            //comparing the bitmaps should give results of the matcher if they are equal
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Bitmap otherBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
            return bitmap.sameAs(otherBitmap);

        }


        @Override
        public void describeTo(Description description) {
            description.appendText("with drawable from resource id: ");
            description.appendValue(expectedId);
            if (resourceName != null) {
                description.appendText("[");
                description.appendText(resourceName);
                description.appendText("]");
            }
        }

        public static Matcher<View> withDrawable(final int resourceId) {
            return new DrawableMatcher(resourceId);
        }

    }

    public static class StringPrefsMatcher extends TypeSafeMatcher<View> {

        String toSearch;
        String dataStored;
        Context context;

        public StringPrefsMatcher(String toSearch, String dataStored, Context context) {
            this.toSearch = toSearch;
            this.dataStored = dataStored;
            this.context = context;
        }

        @Override
        protected boolean matchesSafely(View item) {

            //Type check we need to do in TypeSafeMatcher
            if (!(item instanceof TextView)) {
                return false;
            }

           switch (toSearch) {
               case "MeasuredRSSI":
                   int rssi = Integer.valueOf(dataStored);
                   return rssi == (Prefs.getMeasuredRSSI(context));
               case "NumberOfScans":
                   int scans = Integer.valueOf(dataStored);
                   return scans == Prefs.getNumberOfScans(context);
               case "Location":
                   return dataStored.equals(Prefs.getCurrentLocation(context));
               default:
                   return false;
           }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Checking preferences of: ");
            description.appendText(toSearch);
        }
    }

    public static class BoolPrefsMatcher extends TypeSafeMatcher<View> {

        String toSearch;
        Boolean dataStored;
        Context context;

        public BoolPrefsMatcher(String toSearch, Boolean dataStored, Context context) {
            this.toSearch = toSearch;
            this.dataStored = dataStored;
            this.context = context;
        }

        @Override
        protected boolean matchesSafely(View item) {

            //Type check we need to do in TypeSafeMatcher
            if (!(item instanceof Button)) {
                return false;
            }

            switch (toSearch) {
                case "DarkMode":
                    return dataStored.equals(Prefs.getDarkModeState(context));
                case "ActiveScanning":
                    return dataStored.equals(Prefs.getActiveScanningEnabled(context));
                default:
                    return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Checking preferences of: ");
            description.appendText(toSearch);
        }
    }
}
