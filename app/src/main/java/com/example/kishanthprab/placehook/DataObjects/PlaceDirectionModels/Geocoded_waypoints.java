package com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels;

public class Geocoded_waypoints
{
    private String[] types;

    private String geocoder_status;

    private String place_id;

    public String[] getTypes ()
    {
        return types;
    }

    public void setTypes (String[] types)
    {
        this.types = types;
    }

    public String getGeocoder_status ()
    {
        return geocoder_status;
    }

    public void setGeocoder_status (String geocoder_status)
    {
        this.geocoder_status = geocoder_status;
    }

    public String getPlace_id ()
    {
        return place_id;
    }

    public void setPlace_id (String place_id)
    {
        this.place_id = place_id;
    }

    @Override
    public String toString()
    {
        return "MyPlaceDirection [types = "+types+", geocoder_status = "+geocoder_status+", place_id = "+place_id+"]";
    }
}


