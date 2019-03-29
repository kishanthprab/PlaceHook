package com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels;

public class Open
{
    private String time;

    private String day;

    public String getTime ()
    {
        return time;
    }

    public void setTime (String time)
    {
        this.time = time;
    }

    public String getDay ()
    {
        return day;
    }

    public void setDay (String day)
    {
        this.day = day;
    }

    @Override
    public String toString()
    {
        return "MyPlaceDetails [time = "+time+", day = "+day+"]";
    }
}


