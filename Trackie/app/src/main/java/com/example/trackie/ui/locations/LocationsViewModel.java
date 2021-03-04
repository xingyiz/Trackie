package com.example.trackie.ui.locations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trackie.database.MapData;

import java.util.ArrayList;

public class LocationsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<MapData>> locationsData;

    public LocationsViewModel() {

        locationsData = new MutableLiveData<>();

    }

    public LiveData<ArrayList<MapData>> getLocationsData() {
        return locationsData;
    }
}