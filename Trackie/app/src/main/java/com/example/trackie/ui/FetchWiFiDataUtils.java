package com.example.trackie.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class FetchWiFiDataUtils {
    private Activity activity;
    private Context context;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    private BroadcastReceiver wifiReceiver;
    private final int timesToScan;
    private int timesScanned;

    public static int WIFI_SCAN_PERMISSIONS_CODE = 123;

    public FetchWiFiDataUtils(Activity activity, boolean isPermissionsGranted, FetchListener listener, int timesToScan) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.timesToScan = timesToScan;

        timesScanned = 0;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initializeBroadcastReceiver(listener);
        results = new ArrayList<>();
    }

    // Activity will need to call onRequestPermissionsResult
    public boolean getPermissionGranted() {
        // With Android Level >= 23, have to ask user for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "ACCESS_COARSE_LOCATION permission not granted", Toast.LENGTH_SHORT).show();

                // Request permission
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE
                }, WIFI_SCAN_PERMISSIONS_CODE);
            } else {
                return true;
            }
        }
        return false;
    }

    // Helper function to create WiFiManager, scan the RSSI values and return the result
    public void initializeBroadcastReceiver(FetchListener listener) {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(context, "Please enable Wi-Fi", Toast.LENGTH_LONG).show();
        } else {
            wifiReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    if (success) {
                        results = wifiManager.getScanResults();
                        listener.onScanResultsReceived(results);
                        timesScanned++;

                        if (timesScanned == timesToScan) {
                            timesScanned = 0;
                            listener.finishAllScanning();
                        } else {
                            scanWiFiData();
                        }
                    } else {
                        Toast.makeText(context, "SCAN FAILURE :(", Toast.LENGTH_SHORT).show();
                        listener.onError(new Throwable("Could not get RSSI values :("));
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(wifiReceiver, intentFilter);
        }
    }



    public boolean scanWiFiData() {
        boolean success = wifiManager.startScan();
        if (success) {
            Toast.makeText(context, "Scanning for WiFi...", Toast.LENGTH_SHORT).show();
        }
        return success;
    }

    public boolean scanMultipleWiFiData(int times) {
        boolean success = false;
        for (int i=0; i<times; i++) {
            success = scanWiFiData();
        }
        return success;
    }

    public interface FetchListener {
        void setLocation(PointF location);
        void onScanResultsReceived(List<ScanResult> scanResults);
        void onError(Throwable error);
        void finishAllScanning();
    }
}
