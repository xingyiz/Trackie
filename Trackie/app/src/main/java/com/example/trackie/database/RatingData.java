package com.example.trackie.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class RatingData implements MapRep, Parcelable {
    private String ratingText;
    private float ratingStars;

    public RatingData() {}

    public RatingData(String ratingText, float ratingStars) {
        this.ratingText = ratingText;
        this.ratingStars = ratingStars;
    }

    protected RatingData(Parcel in) {
        ratingText = in.readString();
        ratingStars = in.readFloat();
    }

    public static final Creator<RatingData> CREATOR = new Creator<RatingData>() {
        @Override
        public RatingData createFromParcel(Parcel in) {
            return new RatingData(in);
        }

        @Override
        public RatingData[] newArray(int size) {
            return new RatingData[size];
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
