package com.example.trackie.database;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.trackie.ui.Prefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FloorplanHelper {

    public static class GetFloorplanList implements FirestoreExecute {
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private List<FloorplanData> floorplanDataList = new ArrayList<>();

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                db.collection("Floorplans").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        FloorplanData floorplanData = doc.toObject(FloorplanData.class);
                                        if (!floorplanDataList.contains(floorplanData.getName())) {
                                            floorplanDataList.add(floorplanData);
                                        }
                                    }
                                    callback.onSuccess();
                                } else {
                                    callback.onError();
                                }
                            }
                        });
            } catch (Exception e) {
                callback.onError();
            }
        }

        public List<FloorplanData> getFloorplanDataList() {
            return floorplanDataList;
        }
    }

    public static class RetrieveFloorplan implements FirestoreExecute {
        private String name;
        private String floorplanURL;
        private Timestamp timestamp;
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private int darkmode;

        public RetrieveFloorplan(String name, Context context) {
            this.name = name;
            this.darkmode = Prefs.getDarkModeState(context) ? 1 : 0;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                String floorplanName = name + darkmode;
                db.collection("Floorplans").document(floorplanName)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            FloorplanData floorplanData = documentSnapshot.toObject(FloorplanData.class);
                            if (floorplanData == null) {
                                callback.onFailure();
                                return;
                            }
                            name = floorplanData.getName();
                            floorplanURL = floorplanData.getFloorplan();
                            timestamp = floorplanData.getTimestamp();
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

        public String getFloorplanURL() {
            return floorplanURL;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }
    }

    public static class UploadFloorplan implements FirestoreExecute {

        private String name;
        private Uri floorplan;
        private int darkmode;
        private Timestamp timestamp;

        private final FirebaseStorage storage = FirebaseStorage.getInstance();
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();

        public UploadFloorplan(String name, Uri floorplan, int darkmode, Timestamp timestamp) {
            this.name = name;
            this.floorplan = floorplan;
            this.darkmode = darkmode;
            this.timestamp = timestamp;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                String floorplanName = name + darkmode;
                StorageReference ref = storage.getReference(floorplanName);
                ref.putFile(floorplan).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                floorplan = uri;
                                db.collection("Floorplans").document(floorplanName)
                                        .set(new FloorplanData(name, floorplan.toString(), darkmode, timestamp), SetOptions.merge())
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
                            }
                        });
                    }
                });
            } catch (Exception e) {
                // error handling
                callback.onFailure();
            }
        }
    }

    public static class RemoveFloorplan implements FirestoreExecute {

        private String floorplanName;
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();

        public RemoveFloorplan(String name) {
            this.floorplanName = name;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            WriteBatch writeBatch = db.batch();
            try {
                DocumentReference dark = db.collection("Floorplans").document(floorplanName + "1");
                DocumentReference light = db.collection("Floorplans").document(floorplanName + "0");
                if (dark != null) {
                    writeBatch.delete(dark);
                }
                if (light != null) {
                    writeBatch.delete(light);
                }
                writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
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
