package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Recycler.RecyclerAdapter;
import com.example.kishanthprab.placehook.Recycler.RecyclerListItem;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.example.kishanthprab.placehook.Utility.Functions_nearby;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;

    private List<RecyclerListItem> feedList;

    GoogleAPIService googleNearbyService;


    //for location
    FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    Location LastLocation = null;

    private static final int Request_user_Location_code = 2;
    final static String TAG = "DiscoverFragment";


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
        //nearbyPlace("point_of_interest");



        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 500);
                recyclerAdapter.notifyDataSetChanged();
            }
        };
        handler.post(runnable);



        //location
        fusedLocationClient = new FusedLocationProviderClient(getActivity());

        //check permission runtime
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_Location_code);

        } else {
            //if permission granted
            buildLocationRequest();
            buildLocationCallBack();

            //start fused location

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_Location_code);


                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


        }



    }

    private void buildLocationCallBack() {

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {

                    if (LastLocation==null) {
                        LastLocation = location;
                        nearbyPlace("point_of_interest");

                    }
                    LastLocation = location;

                    Log.i("LocVaues", "lat " + location.getLatitude() + " longitude" + location.getLongitude());
                }
            }
        };
    }


    public void stopLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_Location_code);


            return;
        }
        fusedLocationClient.removeLocationUpdates(locationCallback);


    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case Request_user_Location_code:
                if (grantResults.length > 0) {


                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_Location_code);

                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


                        }

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onrequest permision " + "Permission denied");
                    }


                }

        }


    }

    public AlertDialog showDialog(){

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .build();

        return alertDialog;

    }



    //nearby place get call
    private void nearbyPlace(String placeType) {

        final AlertDialog alertDialog = showDialog();

        alertDialog.setMessage("Loading please wait...");
        alertDialog.show();




        if (LastLocation!=null){

            String url = getURL(LastLocation.getLatitude(), LastLocation.getLongitude(), placeType);
            googleNearbyService.getNearbyPlaces(url)
                    .enqueue(new Callback<MyPlaces>() {
                        @Override
                        public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                            if (response.isSuccessful()) {


                                Log.d("response", "susscessfull");

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
                                alertDialog.dismiss();


                            }

                        }

                        @Override
                        public void onFailure(Call<MyPlaces> call, Throwable t) {
                            Log.d("responseFail", t.getStackTrace().toString());
                        }
                    });
        }

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
