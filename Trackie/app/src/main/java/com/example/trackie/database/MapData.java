package com.example.trackie.database;

import android.graphics.Point;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapData implements MapRep {
    private static final String TAG = "MapData";

    private Map<String, ArrayList<Integer>> data;   // String = BSSID of WAP, ArrayList<Integer> = RSSI values associated with WAP
    private Point location;                         // (x, y) location of user
    private String device;
    private Timestamp timestamp;

    public MapData() {}

    public MapData(Map<String, ArrayList<Integer>> data, Point location, String device, Timestamp timestamp) {
        this.data = data;
        this.location = location;
        this.device = device;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Map<String, ArrayList<Integer>> getData() {
        return data;
    }

    public void setData(Map<String, ArrayList<Integer>> data) {
        this.data = data;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Map<String, Object> retrieveRepresentation() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", data);
        map.put("location", location);
        map.put("device", device);
        map.put("timestamp", timestamp);
        return map;
    }

    @NonNull
    @Override
    public String toString() {
        return "MapData[ location = " + location.toString() +
                ", device = " + device + ", timestamp = " +
                timestamp.toString() + " ]";
    }
}
