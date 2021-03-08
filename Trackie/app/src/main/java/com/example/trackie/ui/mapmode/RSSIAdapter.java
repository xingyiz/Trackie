package com.example.trackie.ui.mapmode;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class RSSIAdapter extends RecyclerView.Adapter {
    private List<ScanResult> scanResultList;
    Context context;

    public RSSIAdapter(List<ScanResult> scanResultList, Context context) {
        this.scanResultList = scanResultList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.rssi_listview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        ScanResult scanResult = scanResultList.get(position);
        System.out.println("ScanResult: " + scanResult);
        viewHolder.BSSID.setText(scanResult.BSSID);
        viewHolder.SSID.setText(scanResult.SSID);
        viewHolder.RSSI.setText(scanResult.level);
        viewHolder.TIME.setText(String.valueOf(scanResult.timestamp));
    }

    @Override
    public int getItemCount() {
        return scanResultList.size();
    }



    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView BSSID, SSID, RSSI, TIME;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            BSSID = itemView.findViewById(R.id.BSSID);
            SSID = itemView.findViewById(R.id.SSID);
            RSSI = itemView.findViewById(R.id.RSSI);
            TIME = itemView.findViewById(R.id.TIME);
        }
    }
}
