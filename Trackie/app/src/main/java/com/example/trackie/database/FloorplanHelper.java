package com.example.trackie.database;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FloorplanHelper {

    public static class RetrieveFloorplan implements FirestoreExecute {
        private String name;
        private String floorplanURL;
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();

        public RetrieveFloorplan(String name) {
            this.name = name;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                db.collection(name + "_floorplan").document(name)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            FloorplanData floorplanData = documentSnapshot.toObject(FloorplanData.class);
                            name = floorplanData.getName();
                            floorplanURL = floorplanData.getFloorplan();
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
    }

    public static class UploadFloorplan implements FirestoreExecute {

        private String name;
        private Uri floorplan;

        private final FirebaseStorage storage = FirebaseStorage.getInstance();
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();

        public UploadFloorplan(String name, Uri floorplan) {
            this.name = name;
            this.floorplan = floorplan;
        }

        @Override
        public void execute(OnCompleteCallback callback) {
            try {
                StorageReference ref = storage.getReference(name);
                ref.putFile(floorplan).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                floorplan = uri;
                                db.collection(name + "_floorplan").document(name)
                                        .set(new FloorplanData(name, floorplan.toString()), SetOptions.merge())
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
}
