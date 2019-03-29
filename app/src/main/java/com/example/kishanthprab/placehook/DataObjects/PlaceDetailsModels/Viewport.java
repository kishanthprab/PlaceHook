package com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Northeast;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Southwest;

public class Viewport
{
    private Southwest southwest;

    private Northeast northeast;

    public Southwest getSouthwest ()
    {
        return southwest;
    }

    public void setSouthwest (Southwest southwest)
    {
        this.southwest = southwest;
    }

    public Northeast getNortheast ()
    {
        return northeast;
    }

    public void setNortheast (Northeast northeast)
    {
        this.northeast = northeast;
    }

    @Override
    public String toString()
    {
        return "MyPlaceDetails [southwest = "+southwest+", northeast = "+northeast+"]";
    }
}

