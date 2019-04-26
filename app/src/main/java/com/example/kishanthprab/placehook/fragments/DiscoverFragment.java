package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.app.AlertDialog;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.HttpResponse;
import com.example.kishanthprab.placehook.DashboardActivity;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Photos;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Recycler.RecyclerAdapter;
import com.example.kishanthprab.placehook.Recycler.RecyclerListItem;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.example.kishanthprab.placehook.Utility.DownloadImageTask;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.example.kishanthprab.placehook.Utility.Functions_nearby;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;

    private List<RecyclerListItem> feedList;

    Toolbar disc_toolbar;
    TextView disc_toolbar_title;

    GoogleAPIService googleNearbyService;
    PlacesClient placesClient;

    AlertDialog alertDialog;

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

        //init components

        //init toolbar
        disc_toolbar = (Toolbar)fragmentView.findViewById(R.id.disc_toolbar);
        disc_toolbar_title =(TextView)disc_toolbar.findViewById(R.id.disc_toolbar_title);

        ((AppCompatActivity)getActivity()).setSupportActionBar(disc_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        disc_toolbar_title.setText("Discover");

        //navigation drawer toggle
        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(getActivity(), DashboardActivity.getDrawer(), disc_toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        DashboardActivity.getDrawer().addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();


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

                    if (LastLocation == null) {
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

    public AlertDialog showDialog() {

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .build();

        return alertDialog;

    }


    //nearby place get call
    private void nearbyPlace(String placeType) {

        alertDialog = showDialog();

        alertDialog.setMessage("Loading please wait...");
        alertDialog.show();


        if (LastLocation != null) {

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

                                    float[] distance = new float[5];
                                    Location.distanceBetween(placeLocation.latitude,
                                            placeLocation.longitude,
                                            LastLocation.getLatitude(),LastLocation.getLongitude(),distance
                                            );
                                    double roundedDistance =  Double.parseDouble(String.format("%.2f",distance[0]/1000 ));

                                    if (distance.length != 0) {

                                        RecyclerListItem listItem = new RecyclerListItem(
                                                PlaceName,
                                                rating,
                                                roundedDistance
                                        );


                                        listItem.setPlacePhotos(googlePlace.getPhotos());

                                        //photoresult
                                        getPhoto(googlePlace, listItem);

                                        feedList.add(listItem);
                                    }
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

    private void getPhoto(Results googleplace, RecyclerListItem listItem) {

        Bitmap bitmap = null;
        try {
            //  bitmap = Functions.getBitmapFromRedirectedURL("https://lh4.googleusercontent.com/-1wzlVdxiW14/USSFZnhNqxI/AAAAAAAABGw/YpdANqaoGh4/s1600-w400/Google%2BSydney");
            bitmap = Functions
                    .getBitmapFromRedirectedURL("https://maps.googleapis.com/maps/api/place/photo?maxwidth=900&photoreference="
                            + googleplace.getPhotos()[0].getPhoto_reference() +
                            "&key="+getActivity().getResources().getString(R.string.google_maps_key));

            Log.d(TAG, "onResponse: " + "done");
        } catch (Exception e) {

            Log.d(TAG, "onResponse: " + e.getMessage());
        }
        if (bitmap != null) {
            listItem.setPhoto(bitmap);
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
