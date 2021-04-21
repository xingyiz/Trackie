package com.example.trackie.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trackie.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

// Utility class which scans for WiFI data
public class FetchWiFiDataUtils {
    private Activity activity;
    private Context context;

    private WifiManager wifiManager;
    private List<ScanResult> results;
    private BroadcastReceiver wifiReceiver;

    private boolean showProgressWindow;
    private int timesToScan;
    private int timesScanned;
    private boolean stopScanning = false;

    private FetchListener dataListener;
    private PopupWindow progressPopup;
    private View progressPopupView;


    public static int WIFI_SCAN_PERMISSIONS_CODE = 123;

    public FetchWiFiDataUtils(Activity activity, FetchListener listener) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.timesToScan = Prefs.getNumberOfScans(context);
        this.dataListener = listener;

        this.showProgressWindow = true;
        timesScanned = 0;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        results = new ArrayList<>();
    }

    public FetchWiFiDataUtils(Activity activity, FetchListener dataListener, boolean showProgressWindow) {
        this(activity, dataListener);
        this.showProgressWindow = showProgressWindow;
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

    // Helper function to create broadcast receiver which will
    // 1. Run on a seperate HandlerThread
    // 2. Receive results from wifi manager while scanning
    // 3. Stop scanning and null itself when the total number of scans required is reachedm
    private void initializeBroadcastReceiver() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(context, "Please enable Wi-Fi", Toast.LENGTH_LONG).show();
        } else {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            wifiReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    if (success) {
                        results = wifiManager.getScanResults();
                        dataListener.onScanResultsReceived(results);
                        timesScanned++;

                        if (timesScanned == timesToScan || stopScanning) {

                            // stop all scanning, perform steps to unregister receiver and null it
                            dataListener.finishAllScanning();
                            wifiReceiver.abortBroadcast();
                            context.unregisterReceiver(wifiReceiver);
                            wifiReceiver = null;
                            timesScanned = 0;

                            if (progressPopup != null) {
                                progressPopup.dismiss();
                                progressPopup = null;
                            }

                            // enable touch again after touch was disabled when scanning process screen shows
                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        } else {
                            if (!stopScanning) startScanWifiData();
                            else {
                                wifiReceiver.abortBroadcast();
                                wifiReceiver = null;
                            }
                        }
                    } else {
                        Toast.makeText(context, "SCAN FAILURE :(", Toast.LENGTH_SHORT).show();
                        dataListener.onError(new Throwable("Could not get RSSI values :("));
                    }
                }
            };

            context.registerReceiver(wifiReceiver, intentFilter);
        }
    }

    // scans wifi data
    public boolean startScanWifiData() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        // initialize wifireceiver if null
        if (wifiReceiver == null) {
            initializeBroadcastReceiver();
        }

        // check if call to stop scanning was made before
        if (stopScanning) stopScanning = false;
        boolean success = false;
        if (Prefs.getActiveScanningEnabled(context)) {
            try {
                Method startScanActiveMethod = WifiManager.class.getMethod("startScanActive");
                startScanActiveMethod.invoke(wifiManager);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Unable to get active scan method. Set to normal scan...", Toast.LENGTH_SHORT).show();
                Prefs.setActiveScanningEnabled(context, false);
                success = wifiManager.startScan();
            }
        } else success = wifiManager.startScan();
        if (success && showProgressWindow) openProgressWindow();
        return success;
    }

    public void scanWiFiDataIndefinitely() {
        this.timesToScan = Integer.MAX_VALUE;
        startScanWifiData();
    }

    public void openProgressWindow() {
        if (progressPopup != null && progressPopupView != null) {
            TextView timesScannedTextview = progressPopupView.findViewById(R.id.scanning_times_scanned_textview);
            timesScannedTextview.setText(context.getString(R.string.times_scanned) + ": " + timesScanned);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(activity);
        progressPopupView = inflater.inflate(R.layout.collect_wifi_data_progress_layout, null);
        ProgressBar scanProgressBar = progressPopupView.findViewById(R.id.wifi_scanning_progress_indicator);
        scanProgressBar.setMax(timesToScan);
        scanProgressBar.setProgress(timesScanned);

        TextView timesScannedTextview = progressPopupView.findViewById(R.id.scanning_times_scanned_textview);
        timesScannedTextview.setText(context.getString(R.string.times_scanned) + ": " + timesScanned);
        progressPopup = new PopupWindow(progressPopupView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, false);
        // disable touch in case user clicks back while scanning
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressPopup.setOutsideTouchable(false);
        progressPopup.showAtLocation(progressPopupView, Gravity.CENTER, 0, 0);
    }

    public void stopScanning() {
        try {
            context.unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        stopScanning = true;
        if (progressPopup != null && progressPopup.isShowing()) progressPopup.dismiss();
    }

    public interface FetchListener {
        void onScanResultsReceived(List<ScanResult> scanResults);
        void onError(Throwable error);
        void finishAllScanning();
    }
}