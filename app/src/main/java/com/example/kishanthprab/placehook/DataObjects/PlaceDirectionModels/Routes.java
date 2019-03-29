package com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels;

public class Routes
{
    private String summary;

    private String copyrights;

    private Legs[] legs;

    private String[] warnings;

    private Bounds bounds;

    private Overview_polyline overview_polyline;

    private String[] waypoint_order;

    public String getSummary ()
    {
        return summary;
    }

    public void setSummary (String summary)
    {
        this.summary = summary;
    }

    public String getCopyrights ()
    {
        return copyrights;
    }

    public void setCopyrights (String copyrights)
    {
        this.copyrights = copyrights;
    }

    public Legs[] getLegs ()
    {
        return legs;
    }

    public void setLegs (Legs[] legs)
    {
        this.legs = legs;
    }

    public String[] getWarnings ()
    {
        return warnings;
    }

    public void setWarnings (String[] warnings)
    {
        this.warnings = warnings;
    }

    public Bounds getBounds ()
    {
        return bounds;
    }

    public void setBounds (Bounds bounds)
    {
        this.bounds = bounds;
    }

    public Overview_polyline getOverview_polyline ()
    {
        return overview_polyline;
    }

    public void setOverview_polyline (Overview_polyline overview_polyline)
    {
        this.overview_polyline = overview_polyline;
    }

    public String[] getWaypoint_order ()
    {
        return waypoint_order;
    }

    public void setWaypoint_order (String[] waypoint_order)
    {
        this.waypoint_order = waypoint_order;
    }

    @Override
    public String toString()
    {
        return "MyPlaceDirection [summary = "+summary+", copyrights = "+copyrights+", legs = "+legs+", warnings = "+warnings+", bounds = "+bounds+", overview_polyline = "+overview_polyline+", waypoint_order = "+waypoint_order+"]";
    }
}
