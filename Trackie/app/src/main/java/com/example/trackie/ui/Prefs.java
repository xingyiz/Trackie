package com.example.trackie.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.location.DetectedActivity;

// Wrapper class for shared preferences
public class Prefs {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    public static String P_FILE = "com.example.trackie.ui.preferences";
    public static String DARK_MODE_STATE_KEY = "dark_mode_state";
    public static String CURRENT_LOCATION_KEY = "current_location_key";
    public static String MEASURED_RSSI_KEY = "measured_rssi";
    public static String ACTIVE_SCANNING_KEY = "active_scanning_enabled";
    public static String NUMBER_OF_SCANS_KEY = "number_of_scans";
    public static String SAVED_MAPPING_KEY = "saved_mapping_key";
    public static String ADMIN_MODE_KEY = "admin_mode";

    public static String MODEL_TYPE_KEY = "model_type_key";
    public static String MODEL_REGRESSION = "reg";
    public static String MODEL_CLASSIFICATION = "clf";

    public static SharedPreferences intializeSharedPref(Context context) {
        preferences = context.getSharedPreferences(P_FILE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        return preferences;
    }

    public static boolean getDarkModeState(Context context) {
        if (preferences == null) intializeSharedPref(context);
        return preferences.getBoolean(DARK_MODE_STATE_KEY, false);
    }

    public static void setDarkModeState(Context context, boolean isDarkMode) {
        if (preferences == null) intializeSharedPref(context);
        editor.putBoolean(DARK_MODE_STATE_KEY, isDarkMode);
        editor.apply();
    }

    public static boolean getActiveScanningEnabled(Context context) {
        if (preferences == null) intializeSharedPref(context);
        return preferences.getBoolean(ACTIVE_SCANNING_KEY, false);
    }

    public static void setActiveScanningEnabled(Context context, boolean isActiveScanningEnabled) {
        if (preferences == null) intializeSharedPref(context);
        editor.putBoolean(ACTIVE_SCANNING_KEY, isActiveScanningEnabled);
        editor.apply();
    }

    public static String getCurrentLocation(Context context) {
        if (preferences == null) intializeSharedPref(context);
        return preferences.getString(CURRENT_LOCATION_KEY, "null");
    }

    public static void setCurrentLocation(Context context, String currentLocation) {
        if (preferences == null) intializeSharedPref(context);
        editor.putString(CURRENT_LOCATION_KEY, currentLocation);
        editor.apply();
    }

    public static boolean getAdminMode(Context context) {
        if (preferences == null) intializeSharedPref(context);
        return preferences.getBoolean(ADMIN_MODE_KEY, false);
    }

    public static void setAdminMode(Context context, boolean adminMode) {
        if (preferences == null) intializeSharedPref(context);
        editor.putBoolean(ADMIN_MODE_KEY, adminMode);
        editor.apply();
    }

    public static int getMeasuredRSSI(Context context) {
        if (preferences == null) intializeSharedPref(context);
        return preferences.getInt(MEASURED_RSSI_KEY, -100);
    }

    public static void setMeasuredRSSI(Context context, int measuredRSSI) {
        if (preferences == null) intializeSharedPref(context);
        editor.putInt(MEASURED_RSSI_KEY, measuredRSSI);
        editor.apply();
    }

    public static int getNumberOfScans(Context context) {
        if (preferences == null) intializeSharedPref(context);
        return preferences.getInt(NUMBER_OF_SCANS_KEY, 5);
    }

    public static void setNumberOfScans(Context context, int numberOfScans) {
        if (preferences == null) intializeSharedPref(context);
        editor.putInt(NUMBER_OF_SCANS_KEY, numberOfScans);
        editor.apply();
    }

    public static String getSavedMapping(Context context) {
        if (preferences == null) intializeSharedPref(context);
        return preferences.getString(SAVED_MAPPING_KEY, "");
    }

    public static void setSavedMapping(Context context, String json) {
        if (preferences == null) intializeSharedPref(context);
        editor.putString(SAVED_MAPPING_KEY, json);
        editor.apply();
    }

    public static void clearSavedMapping(Context context) {
        if (preferences == null) intializeSharedPref(context);
        editor.putString(SAVED_MAPPING_KEY, "");
        editor.apply();
    }

    public static String getModelType(Context context) {
        if (preferences == null) intializeSharedPref(context);
        return preferences.getString(MODEL_TYPE_KEY, MODEL_CLASSIFICATION);
    }

    public static void setModelType(Context context, String model) {
        if (preferences == null) intializeSharedPref(context);
        editor.putString(MODEL_TYPE_KEY, model);
        editor.apply();
    }
}
