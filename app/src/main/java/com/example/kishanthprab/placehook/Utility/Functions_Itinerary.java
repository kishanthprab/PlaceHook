package com.example.kishanthprab.placehook.Utility;

import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.MyPlaceDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Functions_Itinerary {

   // public static Results[] ItineraryPlacesResults;
    public static ArrayList<Results> ItineraryPlacesResults;

    public static HashMap<String,Results> ItineraryPlacesList;

    public static Place itinerayLocation;
    public static int TotalNoOfPlaces;
    public static ArrayList<Marker> AddedMarkers;
    public static HashMap<String,Marker> AddedMarkersWithPlaceID;

    public static MyPlaceDetails CurrentPlaceDetails;



}
