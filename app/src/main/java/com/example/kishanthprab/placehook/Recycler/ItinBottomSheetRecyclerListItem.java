package com.example.kishanthprab.placehook.Recycler;

import android.graphics.Bitmap;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Photos;

public class ItinBottomSheetRecyclerListItem {

    String itin_placeNumber1;
    String itin_placeName1;
    String itin_placeNumber2;
    String itin_placeName2;

    String itin_placeDuration;
    String itin_placeDistance;


    public ItinBottomSheetRecyclerListItem(String itin_placeNumber1, String itin_placeName1, String itin_placeNumber2, String itin_placeName2, String itin_placeDuration, String itin_placeDistance) {
        this.itin_placeNumber1 = itin_placeNumber1;
        this.itin_placeName1 = itin_placeName1;
        this.itin_placeNumber2 = itin_placeNumber2;
        this.itin_placeName2 = itin_placeName2;
        this.itin_placeDuration = itin_placeDuration;
        this.itin_placeDistance = itin_placeDistance;
    }

    public String getItin_placeNumber1() {
        return itin_placeNumber1;
    }

    public void setItin_placeNumber1(String itin_placeNumber1) {
        this.itin_placeNumber1 = itin_placeNumber1;
    }

    public String getItin_placeName1() {
        return itin_placeName1;
    }

    public void setItin_placeName1(String itin_placeName1) {
        this.itin_placeName1 = itin_placeName1;
    }

    public String getItin_placeNumber2() {
        return itin_placeNumber2;
    }

    public void setItin_placeNumber2(String itin_placeNumber2) {
        this.itin_placeNumber2 = itin_placeNumber2;
    }

    public String getItin_placeName2() {
        return itin_placeName2;
    }

    public void setItin_placeName2(String itin_placeName2) {
        this.itin_placeName2 = itin_placeName2;
    }

    public String getItin_placeDuration() {
        return itin_placeDuration;
    }

    public void setItin_placeDuration(String itin_placeDuration) {
        this.itin_placeDuration = itin_placeDuration;
    }

    public String getItin_placeDistance() {
        return itin_placeDistance;
    }

    public void setItin_placeDistance(String itin_placeDistance) {
        this.itin_placeDistance = itin_placeDistance;
    }
}
