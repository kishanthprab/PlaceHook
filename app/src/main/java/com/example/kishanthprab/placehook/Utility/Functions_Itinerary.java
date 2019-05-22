package com.example.kishanthprab.placehook.Utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.MyPlaceDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.DataObjects.UserReview;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Functions_Itinerary {

    private static final String TAG = "Function_Itinerary";


    public static String userName;

    // public static Results[] ItineraryPlacesResults;
    public static ArrayList<Results> ItineraryPlacesResults;
    public static ArrayList<Results> AddedPlaces;

    public static HashMap<String, Results> ItineraryPlacesList; //to store all the places which got from planner

    public static Place itinerayLocation;
    public static int TotalNoOfPlaces;             //to store total no of places which got from planner
    public static ArrayList<Marker> AddedMarkers; //to store added markers in the itinerary map

    public static HashMap<String, Marker> AddedMarkersWithPlaceID;

    public static MyPlaceDetails CurrentPlaceDetails; //to store current place details

    public static ArrayList<MyPlaceDirection> ItineraryPlacesDirections; //to store directions of generated places


    //to store appUser Reviews from firebase

    public static ArrayList<UserReview> appUserReviews;

}
