package com.example.trackie.database;

import android.graphics.PointF;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class TestData extends MapData {
    public TestData(String name, Map<String, List<Integer>> data, PointF location, double z,
                    String device, Timestamp timestamp) {
        super(name, data, location, z, device, timestamp);
    }
}
