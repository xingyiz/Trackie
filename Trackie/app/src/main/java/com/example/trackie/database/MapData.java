package com.example.trackie.database;

import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData implements MapRep {
    private static final String TAG = "MapData";

    private String id;
    private String name;                            // Name of Location
    private Map<String, List<Integer>> data;        // String = BSSID of WAP, ArrayList<Integer> = RSSI values associated with WAP
    private PointF location;                        // (x, y) location of user
    private double z;                               // z location of user, doesn't really change if on the same floor
    private String device;
    private Timestamp timestamp;
    private String floorplan;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    public MapData() {}

    /**
     * Constructor for MapData, consists of all things associated with MapData
     * @param name
     * @param data
     * @param location
     * @param z
     * @param device
     * @param timestamp
     * @param floorplan     Uri String, set to null if already exists
     */
    public MapData(String name, Map<String, List<Integer>> data, PointF location, double z,
                   String device, Timestamp timestamp, String floorplan) {
        this.name = name;
        this.data = data;
        this.location = location;
        this.z = z;
        this.device = device;
        this.timestamp = timestamp;
        this.floorplan = floorplan;
        if (floorplan != null) {
            // uploadFloorplan();
        } else {
            // retrieve floorplan from storage
        }
    }

    // test function pls delete
    public MapData(String name) {
        this.name = name;
    }

    private void uploadFloorplan() {
        try {
            Uri filePath = Uri.parse(floorplan);
            StorageReference ref = storageReference.child(name);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            floorplan = uri.toString();
                        }
                    });
                }
            });
        } catch (Exception e) {
            // error handling
        }
    }

    private void retrieveFloorplan() {
        try {
            StorageReference ref = storageReference.child(name);
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    floorplan = uri.toString();
                }
            });
        } catch (Exception e) {
            // error handling
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public PointF getLocation() {
        return location;
    }

    public void setLocation(PointF location) {
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

    public String getFloorplan() { return floorplan; }

    public void setFloorplan(String floorplan) { this.floorplan = floorplan; }

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
        return "MapData[ id = " + id
                + ", name = " + name
                + ", location = " + location.toString()
                + ", z = " + z
                + ", device = " + device
                + ", timestamp = " + timestamp.toString()
                + ", floorplan = " + floorplan + " ]";
    }
}
