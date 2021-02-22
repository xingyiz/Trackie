package com.example.trackie.database;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MapData implements Parcelable, MapEntry {
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

    protected MapData(Parcel in) {
        name = in.readString();
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
        dest.writeString(name);
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


    public abstract static class GetMapData extends AsyncGetter {
        private static final String LOG = "MapData.GetMapData";
        private static final int SLEEP = 10;
        private MapData result = null;
        private boolean timeOut = false;
        private boolean exists = true;

        private String mapID;
        private Integer timeout;

        public GetMapData(String mapID, Integer timeout) {
            this.mapID = mapID;
            this.timeout = timeout;
        }

        @Override
        public void runMainBody() {
            MapData map[] = {null};
            final boolean[] finish = {false};

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference documentReference = db.collection("MapData").document(mapID);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            map[0] = documentSnapshot.toObject(MapData.class);
                            finish[0] = true;
                        } else {
                            Log.d(LOG, "MapID not found in database");
                            finish[0] = true;
                            exists = false;
                        }
                    } else {
                        Log.d(LOG, "Document not successful");
                        finish[0] = true;
                    }
                }
            });
        }
    }
}
