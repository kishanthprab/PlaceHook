package com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels;

public class Location
{
    private String lng;

    private String lat;

    public String getLng ()
    {
        return lng;
    }

    public void setLng (String lng)
    {
        this.lng = lng;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "MyPlaceDetails [lng = "+lng+", lat = "+lat+"]";
    }
}


