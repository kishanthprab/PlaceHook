package com.example.kishanthprab.placehook.DataObjects;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.List;

public class ItinerarysearchDetails {

    private Place tripLocation;
    private ArrayList<String> placeTypeList;
    public static int PlacesCount;

    public Place getTripLocation() {
        return tripLocation;
    }

    public void setTripLocation(Place tripLocation) {
        this.tripLocation = tripLocation;
    }

    public ArrayList<String> getPlaceTypeList() {

        placeTypeList = new ArrayList<String>();
        placeTypeList.add("Natural Feature");
        placeTypeList.add("Tourist Places");
        placeTypeList.add("Beach Side places");
        placeTypeList.add("Worship Places");

        return placeTypeList;
    }

    public String returnTypeFieldName(String str) {

        String s = "";

        switch (str) {
            case "Natural Feature":
                s = "natural_feature";
                break;
            case "Tourist Places":
                s = "point_of_interest";
                break;
            case "Beach Side places":
                s = "beach_side_places";
                break;
            case "Worship Places":
                s = "place_of_worship";
                break;

        }
        return s;

    }

    public void setPlaceTypeList(ArrayList<String> placeTypeList) {
        this.placeTypeList = placeTypeList;
    }


    public ArrayList<String> getNoOfPlacesList(int num) {

        PlacesCount = num;

        ArrayList<String> NoOfPlacesList = new ArrayList<String>();

            for (int i = 1; i <= num; i++) {

                NoOfPlacesList.add(i + "");
            }


        return NoOfPlacesList;
    }

}
