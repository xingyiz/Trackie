package com.example.trackie.database;

import android.graphics.Point;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Map;

public class TestData extends MapData implements MapRep {
    public TestData(Map<String, ArrayList<Integer>> data, Point location, String device, Timestamp timestamp) {
        super(data, location, device, timestamp);
    }
}
