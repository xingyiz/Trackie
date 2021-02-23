package com.example.trackie.database;

import android.graphics.Point;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData implements MapRep {
    private static final String TAG = "MapData";

    private String name;
    private Map<String, List<Integer>> data;   // String = BSSID of WAP, ArrayList<Integer> = RSSI values associated with WAP
    private Point location;                         // (x, y) location of user
    private double z;                               // z location of user, doesn't really change if on the same floor
    private String device;
    private Timestamp timestamp;

    public MapData() {}

    public MapData(String name, Map<String, List<Integer>> data, Point location, double z, String device, Timestamp timestamp) {
        this.name = name;
        this.data = data;
        this.location = location;
        this.z = z;
        this.device = device;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<Integer>> getData() {
        return data;
    }

    public void setData(Map<String, List<Integer>> data) {
        this.data = data;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
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
        return "MapData[ location = " + location.toString()
                + ", z = " + z
                + ", device = " + device + ", timestamp = "
                + timestamp.toString() + " ]";
    }
}
