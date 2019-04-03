package com.example.kishanthprab.placehook.DataObjects;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

public class ItinerarysearchDetails {

    private Place placeFrom;
    private Place placeTo;
    String placetype;

    public Place getPlaceFrom() {
        return placeFrom;
    }

    public void setPlaceFrom(Place placeFrom) {
        this.placeFrom = placeFrom;
    }

    public Place getPlaceTo() {
        return placeTo;
    }

    public void setPlaceTo(Place placeTo) {
        this.placeTo = placeTo;
    }
}
