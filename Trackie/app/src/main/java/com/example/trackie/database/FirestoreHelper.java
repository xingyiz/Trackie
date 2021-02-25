package com.example.trackie.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static class GetMapData {
        private String mapName;
        private List<MapData> mapData = new ArrayList<>();

        public GetMapData(String mapName) {
            this.mapName = mapName;
        }

        public void getMapData(DataReceivedCallback callback) {
            db.collection("MapData")
                    .whereEqualTo("name", mapName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    MapData data = snapshot.toObject(MapData.class);
                                    mapData.add(data);
                                    callback.onDataReceived();
                                }
                            } else {
                                Log.d(TAG, "GetMapData unsuccessful");
                            }
                        }
                    });
        }

        public List<MapData> getResult() {return this.mapData;}

        public boolean isSuccessful() {return this.mapData != null;}
    }

    public static class SetMapData{
        private MapData mapData;
        private boolean successful;

        public SetMapData(MapData mapData) {
            this.mapData = mapData;
        }

        public void setMapData(DataReceivedCallback callback) {
            db.collection("MapData")
                    .add(mapData)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                successful = true;
                                Log.i(TAG, "SetMapData successful");
                                callback.onDataReceived();
                            } else {
                                Log.e(TAG, "SetMapData unsuccessful");
                            }
                        }
                    });
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    public static void UpdateMapData() {
        // TODO
    }

    public static class GetImage {

    }
}
