package com.example.trackie.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trackie.ui.Prefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
                db.collection(mapName)
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
            if (checkMapData(mapData)) {
                try {
                    id = db.collection(mapData.getName()).document().getId();
                    mapData.setId(id);
                    db.collection(mapData.getName())
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
            } else {
                callback.onFailure();
            }
        }

        private boolean checkMapData(MapData mapData) {
            return mapData.getName() != null && mapData.getData() != null
                    && mapData.getDevice() != null && mapData.getLocation() != null
                    && mapData.getTimestamp() != null;
        }
    }

    public static class RemoveMapData implements FirestoreExecute{
        private MapData mapData;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public RemoveMapData(MapData mapData) {
            this.mapData = mapData;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                db.collection(mapData.getName()).document(mapData.getId())
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

    public static class UploadRating implements FirestoreExecute {
        private Context context;
        private TestRatingData testRatingData;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private String location;

        public UploadRating(Context context, TestRatingData testRatingData) {
            this.context = context;
            this.testRatingData = testRatingData;
            location = Prefs.getCurrentLocation(context);
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                db.collection("Ratings").document(location)
                        .set(testRatingData)
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
}
