package com.example.trackie;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trackie.database.MapData;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String P_FILE = "com.example.trackie.ui.preferences";
    public static String DARK_MODE_STATE_KEY = "dark_mode_state";
    public static String CURRENT_LOCATION_KEY = "current_location_key";

    private static List<ScanResult> results = new ArrayList<>();

    // Convert Drawable into Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    // Helper function to create WiFiManager, scan the RSSI values and return the result
    public static List<ScanResult> getWiFiScanResults(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(context, "Please enable Wi-Fi", Toast.LENGTH_LONG).show();
        } else {
            BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Toast.makeText(getContext(), "onReceive called", Toast.LENGTH_SHORT).show();
                    boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    if (success) {
//                        Toast.makeText(context, "Scan Complete", Toast.LENGTH_SHORT).show();
                        results = wifiManager.getScanResults();
                        Toast.makeText(context, results.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "SCAN FAILURE :(", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(wifiReceiver, intentFilter);

            boolean success = wifiManager.startScan();
            if (success) Toast.makeText(context, "Scanning for WiFi...", Toast.LENGTH_SHORT).show();
        }

        return results;
    }

}
