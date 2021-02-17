package com.example.trackie.database;

import java.util.Map;

public class MapData implements MapEntry {
    private String name;
    private Location location;
    private WiFi wiFi;

    @Override
    public Map<String, Object> retrieveRepresentation() {
        return null;
    }
}
