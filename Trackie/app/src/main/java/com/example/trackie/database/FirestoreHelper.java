package com.example.trackie.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static ArrayList<MapData> GetMapData(String mapID) {

        ArrayList<MapData> mapData = new ArrayList<>();

        db.collection("MapData")
                .whereEqualTo("name", mapID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                MapData data = snapshot.toObject(MapData.class);
                                mapData.add(data);
                            }
                        } else {
                            Log.d(TAG, "GetMapData unsuccessful");
                        }
                    }
                });

        return mapData;
    }

    public static void SetMapData(MapData mapData, String mapID) {
        db.collection("MapData").document(mapID)
                .set(mapData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "SetMapData successful" + mapData.toString());
                        } else {
                            Log.d(TAG, "SetMapData unsuccessful" + mapData.toString());
                        }
                    }
                });
    }

    public static void UpdateMapData() {
        // TODO
    }
}
