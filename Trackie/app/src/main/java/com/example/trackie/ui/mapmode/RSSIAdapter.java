package com.example.trackie.ui.mapmode;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;

import java.util.List;

public class RSSIAdapter extends RecyclerView.Adapter {
    private List<ScanResult> scanResultList;
    Context context;
    int oneMeterRSSI;

    public RSSIAdapter(List<ScanResult> scanResultList, Context context, int standard) {
        this.scanResultList = scanResultList;
        this.context = context;
        this.oneMeterRSSI = standard;
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
        viewHolder.BSSID.setText("BSSID: " + scanResult.BSSID);
        viewHolder.SSID.setText("SSID: " + scanResult.SSID);
        viewHolder.RSSI.setText("RSSI: " + String.valueOf(scanResult.level));
        viewHolder.DISTANCE.setText("Estimated Distance: " + String.valueOf(RSSIUtils.rssiToDistance(scanResult.level, scanResult.frequency)));
        viewHolder.MEASURED_RSSI.setText("One Meter RSSI: " + String.valueOf(oneMeterRSSI));
        viewHolder.FREQUENCY.setText("Frequency: " + String.valueOf(scanResult.frequency) + "MHz");
        viewHolder.TIME.setText("Time: " + String.valueOf(scanResult.timestamp));
    }

    @Override
    public int getItemCount() {
        return scanResultList.size();
    }



    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView BSSID, SSID, RSSI,DISTANCE, MEASURED_RSSI, FREQUENCY, TIME;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            BSSID = itemView.findViewById(R.id.BSSID);
            SSID = itemView.findViewById(R.id.SSID);
            RSSI = itemView.findViewById(R.id.RSSI);
            DISTANCE = itemView.findViewById(R.id.DISTANCE);
            MEASURED_RSSI = itemView.findViewById(R.id.measuredRSSI);
            FREQUENCY = itemView.findViewById(R.id.Frequency);
            TIME = itemView.findViewById(R.id.TIME);
        }
    }
}
