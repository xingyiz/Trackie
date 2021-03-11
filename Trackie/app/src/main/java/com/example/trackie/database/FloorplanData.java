package com.example.trackie.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class FloorplanData implements MapRep, Parcelable {
    private String name;
    private String floorplan;
    private int darkmode;

    public FloorplanData() {

    }

    public FloorplanData(String name, String floorplan, int darkmode) {
        this.name = name;
        this.floorplan = floorplan;
        this.darkmode = darkmode;
    }

    protected FloorplanData(Parcel in) {
        name = in.readString();
        floorplan = in.readString();
        darkmode = in.readInt();
    }

    public static final Creator<FloorplanData> CREATOR = new Creator<FloorplanData>() {
        @Override
        public FloorplanData createFromParcel(Parcel in) {
            return new FloorplanData(in);
        }

        @Override
        public FloorplanData[] newArray(int size) {
            return new FloorplanData[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getFloorplan() {
        return floorplan;
    }

    public int getDarkmode() {
        return darkmode;
    }

    @Override
    public Map<String, Object> retrieveRepresentation() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("floorplan", floorplan);
        map.put("darkmode", darkmode);
        return map;
    }

    @NonNull
    @Override
    public String toString() {
        return "[FloorplanData: name = " + name
                + ", floorplan = " + floorplan
                + ", darkmode = " + darkmode + " ]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(floorplan);
        dest.writeInt(darkmode);
    }
}
