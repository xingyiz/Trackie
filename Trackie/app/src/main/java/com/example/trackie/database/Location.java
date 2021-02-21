package com.example.trackie.database;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Location implements MapEntry {
    private double x;
    private double y;
    private double z;

    public Location (double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location (Object o) {
        if (! (o instanceof Map)) {
            Log.e("Location creation", "Object is not a Map");
            return;
        }
        this.x = (double) ((Map) o).get("x");
        this.y = (double) ((Map) o).get("y");
        this.z = (double) ((Map) o).get("z");
    }

    // Getters
    public double getX() { return x; }

    public double getY() { return y; }

    public double getZ() { return z; }

    // Setters
    public void setX(double x) { this.x = x; }

    public void setY(double y) { this.y = y; }

    public void setZ(double z) { this.z = z; }

    /**
     * Get String representation of this location
     * @return formatted string of location
     */
    @NonNull
    @Override
    public String toString() {
        return "Location[ x=" + this.x + " , y=" + this.y + " , z=" + this.z + " ]";
    }

    @Override
    public Map<String, Object> retrieveRepresentation() {
        Map<String, Object> map = new HashMap<>();
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        return map;
    }
}
