package com.example.kishanthprab.placehook.DataObjects;

import com.google.android.gms.maps.model.LatLng;

public class NavigationPlaceDetails {

    String place_id;
    String Name;
    String address;
    LatLng location;


    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
