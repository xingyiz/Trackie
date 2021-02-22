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

    public static List<MapData> GetMapData(String mapName) {

        List<MapData> mapData = new ArrayList<>();

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
                            }
                        } else {
                            Log.d(TAG, "GetMapData unsuccessful");
                        }
                    }
                });

        return mapData;
    }

    public static void SetMapData(MapData mapData) {
        db.collection("MapData")
                .add(mapData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "SetMapData successful");
                        } else {
                            Log.e(TAG, "SetMapData unsuccessful");
                        }
                    }
                });
    }

    public static void UpdateMapData() {
        // TODO
    }
}
