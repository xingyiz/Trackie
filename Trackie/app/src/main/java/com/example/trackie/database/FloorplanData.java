package com.example.trackie.database;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class FloorplanData implements MapRep {
    private String name;
    private String floorplan;

    public FloorplanData() {

    }

    public FloorplanData(String name, String floorplan) {
        this.name = name;
        this.floorplan = floorplan;
    }

    public String getName() {
        return name;
    }

    public String getFloorplan() {
        return floorplan;
    }

    @Override
    public Map<String, Object> retrieveRepresentation() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("floorplan", floorplan);
        return map;
    }

    @NonNull
    @Override
    public String toString() {
        return "[FloorplanData: name = " + name
                + ", floorplan = " + floorplan + " ]";
    }
}
