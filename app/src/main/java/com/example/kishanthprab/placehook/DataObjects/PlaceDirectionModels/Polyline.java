package com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels;

public class Polyline
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