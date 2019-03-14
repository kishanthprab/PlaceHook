package com.example.kishanthprab.placehook.fragments;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Photos;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.RecyclerAdapter;
import com.example.kishanthprab.placehook.RecyclerListItem;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;

    private List<RecyclerListItem> feedList;

    GoogleAPIService googleNearbyService;

    ImageView roundDot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_discover, container, false);

        //initialize services
        googleNearbyService = CommonGoogle.getGoogleAPIService();

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recycler view
        recyclerView = (RecyclerView) view.findViewById(R.id.disc_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        feedList = new ArrayList<>();
        feedList.clear();

        recyclerAdapter = new RecyclerAdapter(feedList, getActivity());
        recyclerView.setAdapter(recyclerAdapter);



        //gets details for the feed
        nearbyPlace("point_of_interest");


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 3000);
                recyclerAdapter.notifyDataSetChanged();
            }
        };
        handler.post(runnable);


        //nearbyPlace("point_of_interest");
        //recyclerAdapter.notifyDataSetChanged();

    }


    //nearby place get call
    private void nearbyPlace(String placeType) {



        String url = getURL(6.928064, 79.857336, placeType);
        googleNearbyService.getNearbyPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        if (response.isSuccessful()) {

                            Log.d("response", "susscessfull");

                            for (int i = 0; i < response.body().getResults().length; i++) {

                                Results googlePlace = response.body().getResults()[i];
                                Location placeLocation = googlePlace.getGeometry().getLocation();

                                String Vicinity = googlePlace.getVicinity();
                                LatLng Place_latLng = new LatLng(placeLocation.getLatitude(), placeLocation.getLongitude());

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





    private String getURL(double latitude, double longitude, String placeType) {

        StringBuilder googlePlacesURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlacesURL.append("location=" + latitude + "," + longitude);
        googlePlacesURL.append("&radius=" + 10000);
        googlePlacesURL.append("&type=" + placeType);
        googlePlacesURL.append("&key=" + getResources().getString(R.string.google_maps_key));

        Log.d("getURl", googlePlacesURL.toString());


        return googlePlacesURL.toString();
    }

}
