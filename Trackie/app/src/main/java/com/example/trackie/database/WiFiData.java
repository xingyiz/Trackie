package com.example.trackie.database;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class WiFiData implements MapEntry {
    String BSSID;
    double RSSI;

    public WiFiData (String BSSID, double RSSI) {
        this.BSSID = BSSID;
        this.RSSI = RSSI;
    }

    public WiFiData (Object o) {
        if (! (o instanceof Map)) {
            Log.e("WiFiData creation", "Object is not a Map");
            return;
        }
        this.BSSID = (String) ((Map) o).get("BSSID");
        this.RSSI = (double) ((Map) o).get("RSSI");
    }

    // Getters and Setters
    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public double getRSSI() {
        return RSSI;
    }

    public void setRSSI(double RSSI) {
        this.RSSI = RSSI;
    }

    @NonNull
    @Override
    public String toString() {
        return "WiFiData[ BSSID=" + this.BSSID + ", RSSI=" + this.RSSI + " ]";
    }

    @Override
    public Map<String, Object> retrieveRepresentation() {
        Map<String, Object> map = new HashMap<>();
        map.put("BSSID", this.BSSID);
        map.put("RSSI", this.RSSI);
        return map;
    }
}
