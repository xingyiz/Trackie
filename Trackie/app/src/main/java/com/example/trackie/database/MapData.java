package com.example.trackie.database;

import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData implements MapRep, Parcelable {
    private static final String TAG = "MapData";

    private String id;
    private String name;                            // Name of Location
    private Map<String, List<Integer>> data;        // String = BSSID of WAP, ArrayList<Integer> = RSSI values associated with WAP
    private PointF location;                        // (x, y) location of user
    private double z;                               // z location of user, doesn't really change if on the same floor
    private String device;
    private Timestamp timestamp;

    public MapData() {}

    /**
     * Constructor for MapData, consists of all things associated with MapData
     * @param name
     * @param data
     * @param location
     * @param z
     * @param device
     * @param timestamp
     */
    public MapData(String name, Map<String, List<Integer>> data, PointF location, double z,
                   String device, Timestamp timestamp) {
        this.name = name;
        this.data = data;
        this.location = location;
        this.z = z;
        this.device = device;
        this.timestamp = timestamp;
    }

    // test function pls delete
    public MapData(String name) {
        this.name = name;
    }

    protected MapData(Parcel in) {
        id = in.readString();
        name = in.readString();
        location = in.readParcelable(PointF.class.getClassLoader());
        z = in.readDouble();
        device = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
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
                + ", timestamp = " + timestamp.toString() + " ]";
    }

    public static final Creator<MapData> CREATOR = new Creator<MapData>() {
        @Override
        public MapData createFromParcel(Parcel in) {
            return new MapData(in);
        }

        @Override
        public MapData[] newArray(int size) {
            return new MapData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(name);
        dest.writeParcelable(location, flags);
        dest.writeDouble(z);
        dest.writeString(device);
        dest.writeParcelable(timestamp, flags);
    }

    // creates a full copy of the existing mapdata class
    public MapData copy() {
        Map<String, List<Integer>> copyData = new HashMap<>();
        for (String bssid : data.keySet()) {
            List<Integer> newRSSIValues = new ArrayList<>();
            for (int rssi : data.get(bssid)) {
                newRSSIValues.add(rssi);
            }
            copyData.put(bssid, newRSSIValues);
        }
        return new MapData(name, copyData, location, z, device, timestamp);
    }

    // rescales the coordinates, among other preprocessing things that may be included in the future
    public MapData prepareForUpload(int sourceWidth, int sourceHeight) {
        MapData preparedMapData = this.copy();
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        Double norm_x = (double) this.location.x / sourceWidth;
        Double norm_y = (double) this.location.y / sourceHeight;
        preparedMapData.setLocation(new PointF(Float.parseFloat(df.format(norm_x)), Float.parseFloat(df.format(norm_y))));
        return preparedMapData;
    }
}
