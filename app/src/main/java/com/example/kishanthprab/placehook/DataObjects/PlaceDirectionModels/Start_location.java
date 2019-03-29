package com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels;

public class Start_location
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
        return "MyPlaceDirection [lng = "+lng+", lat = "+lat+"]";
    }
}

