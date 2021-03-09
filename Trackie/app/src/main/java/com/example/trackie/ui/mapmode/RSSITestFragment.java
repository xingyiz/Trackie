package com.example.trackie.ui.mapmode;

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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class RSSITestFragment extends Fragment {

    private RecyclerView recyclerView;

    private WifiManager wifiManager;
    private List<ScanResult> results = new ArrayList<>();
    private RSSIAdapter adapter;
    BroadcastReceiver wifiReceiver;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssitest, container, false);

        MaterialButton scanButton = view.findViewById(R.id.rssi_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });

        recyclerView = view.findViewById(R.id.rssi_listview);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        /* DEPRECATED FOR ANDROID 10
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getContext(), "WiFi is disabled... we will enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }*/

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getContext(), "Please enable Wi-Fi", Toast.LENGTH_LONG).show();
        } else {

            wifiReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Toast.makeText(getContext(), "onReceive called", Toast.LENGTH_SHORT).show();
                    boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

                    if (success) {
                        scanSuccess();
                    } else {
                        scanFailure("onReceive");
                    }
                }
            };

            adapter = new RSSIAdapter(results, getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            requireActivity().registerReceiver(wifiReceiver, intentFilter);

            scanWifi();
            adapter.notifyDataSetChanged();
        }

        return view;
    }

    private void scanWifi() {

        // With Android Level >= 23, have to ask user for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission1 = ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permission1 != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "ACCESS_COARSE_LOCATION permission not granted", Toast.LENGTH_SHORT).show();

                // Request permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE
                }, 123);
                return;
            } else {
                // Toast.makeText(getContext(), "ACCESS_COARSE_LOCATION permission granted", Toast.LENGTH_SHORT).show();
            }
        }

        startScan();
    }

    private void startScan() {
        boolean success = wifiManager.startScan();
        if (success) {
            Toast.makeText(getContext(), "Scanning for WiFi...", Toast.LENGTH_SHORT).show();
        } else {
            scanFailure("scanWifi");
        }
    }

    private void scanFailure(String s) {
        Toast.makeText(getContext(), "SCAN FAILURE :(" + s, Toast.LENGTH_SHORT).show();
    }

    private void scanSuccess() {
        Toast.makeText(getContext(), "Scan Complete", Toast.LENGTH_SHORT).show();
        results = wifiManager.getScanResults();

        adapter = new RSSIAdapter(results, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        // Toast.makeText(getContext(), "Scan Results: " + results.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    startScan();
                } else {
                    Toast.makeText(getContext(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            default: {
                Toast.makeText(getContext(), "Permission Error", Toast.LENGTH_SHORT).show();
            }

        }
    }
}

/*
    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_WIFI_STATE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Change WiFi State Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Change WiFi State Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
*/
