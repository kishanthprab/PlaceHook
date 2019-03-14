package com.example.kishanthprab.placehook;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Photos;

public class RecyclerListItem {
    String placeName;
    double rating;
    double dist;
    Photos[] PlacePhotos;

    public RecyclerListItem(String placeName, double rating, double dist) {
        this.placeName = placeName;
        this.rating = rating;
        this.dist = dist;
    }

    public Photos[] getPlacePhotos() {
        return PlacePhotos;
    }

    public void setPlacePhotos(Photos[] placePhotos) {
        PlacePhotos = placePhotos;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }
}
