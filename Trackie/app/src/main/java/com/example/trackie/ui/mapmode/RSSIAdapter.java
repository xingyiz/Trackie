package com.example.trackie.ui.mapmode;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trackie.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class RSSIAdapter extends ArrayAdapter<ScanResult> {
    private List<ScanResult> scanResultList;
    Context context;

    private static class ViewHolder {
        MaterialTextView bssid;
        MaterialTextView ssid;
        MaterialTextView rssi;
        MaterialTextView timestamp;
    }

    public RSSIAdapter(List<ScanResult> scanResultList, Context context) {
        super(context, R.layout.rssi_listview, scanResultList);
        this.scanResultList = scanResultList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ScanResult scanResult = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rssi_listview, parent, false);
            viewHolder.bssid = convertView.findViewById(R.id.BSSID);
            viewHolder.ssid = convertView.findViewById(R.id.SSID);
            viewHolder.rssi = convertView.findViewById(R.id.RSSI);
            viewHolder.timestamp = convertView.findViewById(R.id.TIME);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.bssid.setText(scanResult.BSSID);
        viewHolder.ssid.setText(scanResult.SSID);
        viewHolder.rssi.setText(scanResult.level);
        viewHolder.timestamp.setText(String.valueOf(scanResult.timestamp));

        return convertView;
    }
}
