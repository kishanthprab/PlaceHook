package com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels;

public class MyPlaceDirection {

    private Routes[] routes;

    private Geocoded_waypoints[] geocoded_waypoints;

    private String status;

    public Routes[] getRoutes ()
    {
        return routes;
    }

    public void setRoutes (Routes[] routes)
    {
        this.routes = routes;
    }

    public Geocoded_waypoints[] getGeocoded_waypoints ()
    {
        return geocoded_waypoints;
    }

    public void setGeocoded_waypoints (Geocoded_waypoints[] geocoded_waypoints)
    {
        this.geocoded_waypoints = geocoded_waypoints;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "MyPlaceDirection [routes = "+routes+", geocoded_waypoints = "+geocoded_waypoints+", status = "+status+"]";
    }

}
