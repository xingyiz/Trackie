package com.example.trackie.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class StorageDownloader implements FirestoreExecute {
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference;

    private String name;
    private File goodSSIDs;
    private File legalPoints;
    private String LEGAL_POINTS;
    private Context context;
    private ArrayList<String> goodBSSIDs;
    private int size;
    private String type;

    private String contents;

    public StorageDownloader(String name, String type, Context context) {
        this.name = name;
        storageReference = firebaseStorage.getReference(this.name);
        this.context = context;
        this.type = type;
    }

    @Override
    public void execute(OnCompleteCallback callback) {
        try {

            switch(type) {
                case "ssids":
                    goodSSIDs = new File(context.getFilesDir(), name);
                    storageReference.getFile(goodSSIDs).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    FileInputStream fileIn = context.openFileInput(name);
                                    InputStreamReader inputReader =  new InputStreamReader(fileIn, StandardCharsets.UTF_8);

                                    StringBuilder stringBuilder = new StringBuilder();
                                    BufferedReader reader = new BufferedReader(inputReader);
                                    String line = reader.readLine();
                                    while (line != null) {
                                        stringBuilder.append(line).append('\n');
                                        line = reader.readLine();
                                    }

                                    contents = stringBuilder.toString();
                                    inputReader.close();
//                            Toast.makeText(context, "Contents: " + contents, Toast.LENGTH_LONG).show();

                                    goodBSSIDs = new ArrayList<>(Arrays.asList(contents.split(", ")));
                                    goodBSSIDs.remove(goodBSSIDs.size() - 1);
//                            goodBSSIDs = new ArrayList<>(goodBSSIDs.subList(0, 60));
                                    size = goodBSSIDs.size();

//                            Toast.makeText(context, "Good BSSIDs: " + goodBSSIDs.get(0), Toast.LENGTH_LONG).show();
//                            Toast.makeText(context, "Size: " + size, Toast.LENGTH_LONG).show();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                callback.onSuccess();

                            } else {
                                callback.onFailure();
                            }
                        }
                    });
                    break;

                case "legal_points":
                    legalPoints = new File(context.getFilesDir(), name);
                    storageReference.getFile(legalPoints).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    FileInputStream fileIn = context.openFileInput(name);
                                    InputStreamReader inputReader =  new InputStreamReader(fileIn, StandardCharsets.UTF_8);

                                    StringBuilder stringBuilder = new StringBuilder();
                                    BufferedReader reader = new BufferedReader(inputReader);
                                    String line = reader.readLine();
                                    while (line != null) {
                                        stringBuilder.append(line).append('\n');
                                        line = reader.readLine();
                                    }

                                    LEGAL_POINTS = stringBuilder.toString();
                                    inputReader.close();
                                    System.out.println(LEGAL_POINTS);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                callback.onSuccess();

                            } else {
                                callback.onFailure();
                            }
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            callback.onError();
        }
    }

    public ArrayList<String> getGoodBSSIDs() {
        return goodBSSIDs;
    }

    public int getSize() {
        return size;
    }

    public String getContents() {
        return contents;
    }

    public String getLEGAL_POINTS() {
        return LEGAL_POINTS;
    }
}
