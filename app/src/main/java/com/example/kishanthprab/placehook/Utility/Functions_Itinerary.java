package com.example.kishanthprab.placehook.Utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.MyPlaceDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Functions_Itinerary {

  private static final String TAG= "Function_Itinerary";


   public static String userName;

   // public static Results[] ItineraryPlacesResults;
    public static ArrayList<Results> ItineraryPlacesResults;
    public static ArrayList<Results> AddedPlaces;

    public static HashMap<String,Results> ItineraryPlacesList;

    public static Place itinerayLocation;
    public static int TotalNoOfPlaces;
    public static ArrayList<Marker> AddedMarkers;

    public static HashMap<String,Marker> AddedMarkersWithPlaceID;

    public static MyPlaceDetails CurrentPlaceDetails;

    public static ArrayList<MyPlaceDirection> ItineraryPlacesDirections;


}
