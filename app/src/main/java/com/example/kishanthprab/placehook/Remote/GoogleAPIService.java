package com.example.kishanthprab.placehook.Remote;

import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GoogleAPIService {

    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);

    @GET
    Call<MyPlaces> getPlaceDetails(@Url String url);

    @GET("maps/api/directions/json")
    Call<MyPlaceDirection> getDirections(@Query("origin")String origin, @Query("destination")String destination, @Query("key")String key);

    @GET
    Call<MyPlaceDirection> getDirections(@Url String url);
}
