package com.example.trackie.ui.databasetest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DatabaseTestViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public DatabaseTestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is database fragment");
    }


    public LiveData<String> getText() {
        return mText;
    }
}
