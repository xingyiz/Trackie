package com.example.trackie.database;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MapData implements MapEntry {
    private String name;
    private Location location;
    private WiFiData wiFiData;

    public MapData (String name, Location location, WiFiData wiFiData) {
        this.name = name;
        this.location = location;
        this.wiFiData = wiFiData;
    }

    public MapData (Object o) {
        if (! (o instanceof Map)) {
            Log.e("MapData creation", "Object is not a Map");
            return;
        }
        this.name = (String) ((Map) o).get("name");
        this.location = (Location) ((Map) o).get("location");
        this.wiFiData = (WiFiData) ((Map) o).get("wiFiData");
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public WiFiData getWiFi() {
        return wiFiData;
    }

    public void setWiFi(WiFiData wiFi) {
        this.wiFiData = wiFi;
    }

    // Returns a HashMap representation of the object
    @Override
    public Map<String, Object> retrieveRepresentation() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", this.name);
        map.put("location", this.location);
        map.put("wifiData", this.wiFiData);
        return map;
    }
}
