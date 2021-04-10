package com.example.trackie.ui.locations;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trackie.database.FloorplanData;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.OnCompleteCallback;

import java.util.ArrayList;

public class LocationsViewModel extends ViewModel {

    public MutableLiveData<ArrayList<FloorplanData>> locationsData = new MutableLiveData<>();

    public LocationsViewModel() {
        loadLocations();
    }
    public ArrayList<FloorplanData> getLocationsData() {
        return locationsData.getValue();
    }

    public void loadLocations() {
        FloorplanHelper.GetFloorplanList getFloorplanList = new FloorplanHelper.GetFloorplanList();
        getFloorplanList.execute(new OnCompleteCallback() {
            @Override
            public void onSuccess() {
                ArrayList<FloorplanData> floorplanDataList = (ArrayList<FloorplanData>) getFloorplanList.getFloorplanDataList();
//                Toast.makeText(getContext(), "Floorplan List Retrieved", Toast.LENGTH_SHORT).show();
                locationsData.setValue(floorplanDataList);
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void onError() {

            }
        });
    }
}