package com.example.trackie.database;

import android.graphics.Point;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class TestData extends MapData implements MapRep {
    public TestData(String name, Map<String, List<Integer>> data, Point location, double z, String device, Timestamp timestamp) {
        super(name, data, location, z, device, timestamp);
    }
}
