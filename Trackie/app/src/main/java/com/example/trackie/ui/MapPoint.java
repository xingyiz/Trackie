package com.example.trackie.ui;

import android.graphics.PointF;

import java.util.List;
import java.util.Map;

public class MapPoint {
    private PointF location;
    private boolean isConfirmed;
    private Map<String, List<Integer>> data;        // String = BSSID of WAP, ArrayList<Integer> = RSSI values associated with WAP

    public MapPoint(PointF location) {
        this.location = location;
        this.isConfirmed = false;
    }

    public PointF getLocation() {
        return location;
    }

    public void setLocation(PointF location) {
        this.location = location;
    }

    public void confirm() {
        isConfirmed = true;
    }

    public Map<String, List<Integer>> getData() {
        return data;
    }

    public void setData(Map<String, List<Integer>> data) {
        this.data = data;
    }
}
