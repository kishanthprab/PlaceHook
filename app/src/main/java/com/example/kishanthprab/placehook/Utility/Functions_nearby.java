package com.example.kishanthprab.placehook.Utility;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Recycler.RecyclerListItem;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Functions_nearby {

    static List<RecyclerListItem> feedList;



    //get place details
    public static void nearbyPlace(GoogleAPIService googleNearbyService,final List<RecyclerListItem> feedList,String placeType, LatLng latLng) {


        String url = getURL(latLng.latitude, latLng.longitude, placeType);
        googleNearbyService.getNearbyPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        if (response.isSuccessful()) {

                            Log.d("response", "successfull");

                            for (int i = 0; i < response.body().getResults().length; i++) {

                                Results googlePlace = response.body().getResults()[i];
                                LatLng placeLocation = new LatLng(
                                        Double.parseDouble(googlePlace.getGeometry().getLocation().getLat()),
                                        Double.parseDouble(googlePlace.getGeometry().getLocation().getLng())
                                );

                                String Vicinity = googlePlace.getVicinity();
                                LatLng Place_latLng = new LatLng(placeLocation.latitude, placeLocation.longitude);

                                String placID = googlePlace.getPlace_id();
                                String PlaceName = googlePlace.getName();
                                double rating = Double.parseDouble(googlePlace.getRating());


                                RecyclerListItem listItem = new RecyclerListItem(
                                        PlaceName,
                                        rating,
                                        1.4
                                );

                                listItem.setPlacePhotos(googlePlace.getPhotos());


                                feedList.add(listItem);
                                //Log.d("response", "value added " + PlaceName);

                            }


                        }

                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {
                        Log.d("responseFail", t.getStackTrace().toString());
                    }
                });

    }


    public static String getURL(double latitude, double longitude, String placeType) {

        StringBuilder googlePlacesURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlacesURL.append("location=" + latitude + "," + longitude);
        googlePlacesURL.append("&radius=" + 10000);
        googlePlacesURL.append("&type=" + placeType);
        // googlePlacesURL.append("&key=" + getResources().getString(R.string.google_maps_key));
        googlePlacesURL.append("&key=" + R.string.google_maps_key);

        Log.d("getURl", googlePlacesURL.toString());


        return googlePlacesURL.toString();
    }


}
