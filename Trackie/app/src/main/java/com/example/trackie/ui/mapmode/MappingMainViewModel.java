package com.example.trackie.ui.mapmode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trackie.database.MapData;

import java.util.ArrayList;
import java.util.List;

public class MappingMainViewModel extends ViewModel {
    private MutableLiveData<List<MapData>> mMapDataList;

    public MappingMainViewModel() {
        mMapDataList = new MutableLiveData<>();
        mMapDataList.setValue(new ArrayList<>());
    }

    public LiveData<List<MapData>> getMapDataList() {
        return mMapDataList;
    }

    public void setMapDataList(List<MapData> mapDataList) {
        mMapDataList.setValue(mapDataList);
    }

    public void addMapData(MapData mapData) {
        getMapDataList().getValue().add(mapData);
    }

    public void removeMapData(MapData mapData) {
        getMapDataList().getValue().remove(mapData);
    }
}
