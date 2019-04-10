package com.example.kishanthprab.placehook.Remote;

import android.graphics.Bitmap;

import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.MyPlaceDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.Photos;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GoogleAPIService {

    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);

    @GET("maps/api/place/details/json")
    Call<MyPlaceDetails> getPlaceDetails(@Query("placeid") String placeId,@Query("key")String key);

    @GET("maps/api/directions/json")
    Call<MyPlaceDirection> getDirections(@Query("origin")String origin, @Query("destination")String destination, @Query("key")String key);

    @GET("maps/api/place/photo")
    Call<ResponseBody> getPhoto(@Query("maxwidth")String maxWidth, @Query("photoreference")String photoReference, @Query("key")String key);
}
