package com.example.trackie.ui.mapmode;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.trackie.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class RSSITestFragment extends Fragment {

    private ListView listView;
    private MaterialButton scanButton;

    private WifiManager wifiManager;
    private List<ScanResult> results = new ArrayList<>();
    private RSSIAdapter adapter;

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (success) {
                scanSuccess();
            } else {
                scanFailure("onReceive");
            }
            Toast.makeText(getContext(), "Scan Complete", Toast.LENGTH_SHORT).show();
            results = wifiManager.getScanResults();
            requireActivity().unregisterReceiver(this);

            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssitest, container, false);

        scanButton = view.findViewById(R.id.rssi_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });

        listView = view.findViewById(R.id.rssi_listview);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getContext(), "WiFi is disabled... we will enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new RSSIAdapter(results, getContext());
        listView.setAdapter(adapter);

        requireActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        return view;
    }

    private void scanWifi() {
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
        results = wifiManager.getScanResults();
        adapter.notifyDataSetChanged();
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
