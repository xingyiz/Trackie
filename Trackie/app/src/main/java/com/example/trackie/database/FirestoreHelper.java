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
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";

    public static class GetMapData implements FirestoreExecute {
        private String mapName;
        private List<MapData> mapData = new ArrayList<>();
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public GetMapData(String mapName) {
            this.mapName = mapName;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
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
                                    callback.onSuccess();
                                } else {
                                    Log.d(TAG, "GetMapData unsuccessful");
                                    callback.onFailure();
                                }
                            }
                        });
            } catch (Exception e) {
                // error handling
                callback.onError();
            }
        }

        public List<MapData> getResult() {return this.mapData;}
    }

    public static class SetMapData implements FirestoreExecute {
        private String id;
        private MapData mapData;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public SetMapData(MapData mapData) {
            this.mapData = mapData;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                id = db.collection("MapData").document().getId();
                mapData.setId(id);
                db.collection("MapData")
                        .document(id)
                        .set(mapData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callback.onSuccess();
                                } else {
                                    callback.onFailure();
                                }
                            }
                        });
            } catch (Exception e) {
                // error handling
                callback.onError();
            }
        }
    }

    public static class RemoveMapData implements FirestoreExecute{
        private String id;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public RemoveMapData(String id) {
            this.id = id;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                db.collection("MapData").document(id)
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callback.onSuccess();
                                } else {
                                    callback.onFailure();
                                }
                            }
                        });
            } catch (Exception e) {
                callback.onError();
            }
        }
    }

    public static class UpdateMapData {
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        // TODO
    }
}
