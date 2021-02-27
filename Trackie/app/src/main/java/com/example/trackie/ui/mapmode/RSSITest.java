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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.trackie.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class RSSITest extends Fragment {

    private ListView listView;

    private BroadcastReceiver wifiReceiver;
    private WifiManager wifiManager;
    private RSSIAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssitest, container, false);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getContext(), "WiFi is currently disabled. \nWe will enable it.", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        listView = view.findViewById(R.id.map_listview);

        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> scanResultList = wifiManager.getScanResults();
                    adapter = new RSSIAdapter(scanResultList, getContext());
                    listView.setAdapter(adapter);
                    requireActivity().unregisterReceiver(wifiReceiver);
                }
            }
        };

        requireActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
        }
    }
}
