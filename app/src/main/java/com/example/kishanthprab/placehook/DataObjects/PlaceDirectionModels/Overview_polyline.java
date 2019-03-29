package com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels;

public class Overview_polyline
{
    private String points;

    public String getPoints ()
    {
        return points;
    }

    public void setPoints (String points)
    {
        this.points = points;
    }

    @Override
    public String toString()
    {
        return "MyPlaceDirection [points = "+points+"]";
    }
}