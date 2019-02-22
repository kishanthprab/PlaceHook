package com.example.kishanthprab.placehook.Remote;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GoogleAPIService {

    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);
}
