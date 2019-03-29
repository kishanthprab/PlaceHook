package com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels;

public class Opening_hours
{
    private String open_now;

    private Periods[] periods;

    private String[] weekday_text;

    public String getOpen_now ()
    {
        return open_now;
    }

    public void setOpen_now (String open_now)
    {
        this.open_now = open_now;
    }

    public Periods[] getPeriods ()
    {
        return periods;
    }

    public void setPeriods (Periods[] periods)
    {
        this.periods = periods;
    }

    public String[] getWeekday_text ()
    {
        return weekday_text;
    }

    public void setWeekday_text (String[] weekday_text)
    {
        this.weekday_text = weekday_text;
    }

    @Override
    public String toString()
    {
        return "MyPlaceDetails [open_now = "+open_now+", periods = "+periods+", weekday_text = "+weekday_text+"]";
    }
}
