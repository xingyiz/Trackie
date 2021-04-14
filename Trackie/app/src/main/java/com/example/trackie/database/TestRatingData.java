package com.example.trackie.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class TestRatingData implements MapRep, Parcelable {
    private String ratingText;
    private float ratingStars;

    public TestRatingData() {}

    public TestRatingData(String ratingText, float ratingStars) {
        this.ratingText = ratingText;
        this.ratingStars = ratingStars;
    }

    protected TestRatingData(Parcel in) {
        ratingText = in.readString();
        ratingStars = in.readFloat();
    }

    public static final Creator<TestRatingData> CREATOR = new Creator<TestRatingData>() {
        @Override
        public TestRatingData createFromParcel(Parcel in) {
            return new TestRatingData(in);
        }

        @Override
        public TestRatingData[] newArray(int size) {
            return new TestRatingData[size];
        }
    };

    public String getRatingText() {
        return ratingText;
    }

    public void setRatingText(String ratingText) {
        this.ratingText = ratingText;
    }

    public float getRatingStars() {
        return ratingStars;
    }

    public void setRatingStars(float ratingStars) {
        this.ratingStars = ratingStars;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ratingText);
        dest.writeFloat(ratingStars);
    }

    @Override
    public Map<String, Object> retrieveRepresentation() {
        Map<String, Object> map = new HashMap<>();
        map.put("ratingText", ratingText);
        map.put("ratingStars", ratingStars);
        return map;
    }
}
