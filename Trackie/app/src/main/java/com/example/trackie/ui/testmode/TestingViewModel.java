package com.example.trackie.ui.testmode;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.database.StorageDownloader;

import java.util.ArrayList;

public class TestingViewModel extends ViewModel {

    public MutableLiveData<ArrayList<String>> goodBSSIDs = new MutableLiveData<>();
    public MutableLiveData<Integer> size;

    public TestingViewModel() {}

    public void loadGoodBSSIDs(String name, Context context) {
        StorageDownloader storageDownloader = new StorageDownloader(name, context);
        storageDownloader.execute(new OnCompleteCallback() {
            @Override
            public void onSuccess() {
                goodBSSIDs.setValue(storageDownloader.getGoodBSSIDs());
//                size.setValue(storageDownloader.getSize());
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Getting GOOD_BSSIDS file failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(context, "Getting GOOD_BSSIDS file errored", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<String> getGoodBSSIDs() {
        return goodBSSIDs.getValue();
    }

    public int getSize() {
        return size.getValue();
    }
}
