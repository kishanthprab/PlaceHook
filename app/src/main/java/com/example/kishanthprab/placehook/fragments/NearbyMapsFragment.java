package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
//import com.google.android.gms.location.LocationListener;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.kishanthprab.placehook.DashboardActivity;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Recycler.RecyclerListItem;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.example.kishanthprab.placehook.Utility.FilterDialog;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyMapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    GoogleMap mMap;

    Marker userCurrentLocation;
    Location LastLocation = null;

    Toolbar nearby_toolbar;
    TextView nearby_toolbar_title;
    ImageView nearby_toolbar_filterMenu;

    AlertDialog spotsDialog;


    private static final int Request_user_Location_code = 2;
    final static String TAG = "NearbyFragment";

    GoogleAPIService mService;

    FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    HashMap<String, MyPlaces> googlePlacesResults;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby_maps, container, false);


        //init components

        //init toolbar
        nearby_toolbar = (Toolbar) view.findViewById(R.id.nearbyMap_toolbar);
        nearby_toolbar_title = (TextView) view.findViewById(R.id.nearbyMap_toolbar_title);
        nearby_toolbar_filterMenu = (ImageView) view.findViewById(R.id.nearbyMap_toolbar_filterMenu);

        nearby_toolbar_filterMenu.setOnClickListener(this);

        ((AppCompatActivity) getActivity()).setSupportActionBar(nearby_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);


        nearby_toolbar_title.setText("Nearby");

        //navigation drawer toggle
        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(getActivity(), DashboardActivity.getDrawer(), nearby_toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        DashboardActivity.getDrawer().addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.nearbyMap);
        mapFragment.getMapAsync(this);

        //init google services
        mService = CommonGoogle.getGoogleAPIService();

        //init list of places with types
        googlePlacesResults = new HashMap<String, MyPlaces>();
        googlePlacesResults.clear();

        //loading dialog
        spotsDialog = Functions.spotsDialog(getActivity());
        spotsDialog.setMessage("Loading please wait...");


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //initialize services
        mService = CommonGoogle.getGoogleAPIService();

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

    public void stopLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_Location_code);


            return;
        }
        fusedLocationClient.removeLocationUpdates(locationCallback);


    }

    private void buildLocationCallBack() {

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {


                for (Location location : locationResult.getLocations()) {

                    if (LastLocation != null) {

                        userCurrentLocation.remove();
                    }

                    LastLocation = location;
                    final LatLng userloc = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                    userCurrentLocation = mMap.addMarker(new MarkerOptions().position(userloc).title("You are here!").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

                    // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userloc,21));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userloc, 17));

                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userloc, 21));

                            handler.postDelayed(this, 5000);
                        }
                    };
                    handler.post(runnable);


                    Log.i("LocVaues", "lat " + location.getLatitude() + " longitude" + location.getLongitude());
                }
            }
        };

    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

        }


        // nearbyPlace("hospital");

    }

    //get UrlString
    private String getURL(double latitude, double longitude, int radius, String placeType) {

        StringBuilder googlePlacesURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlacesURL.append("location=" + latitude + "," + longitude);
        googlePlacesURL.append("&radius=" + radius);
        googlePlacesURL.append("&type=" + placeType);
        googlePlacesURL.append("&key=" + getResources().getString(R.string.google_maps_key));

        Log.d("getURl", googlePlacesURL.toString());


        return googlePlacesURL.toString();
    }

    //nearby place get call
    private void nearbyPlace(int radius, final String placeType) {


        spotsDialog.show();


        if (LastLocation != null) {

            String url = getURL(LastLocation.getLatitude(), LastLocation.getLongitude(), radius, placeType);
            mService.getNearbyPlaces(url)
                    .enqueue(new Callback<MyPlaces>() {
                        @Override
                        public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                            if (response.isSuccessful()) {

                                Log.d("response", "susscessfull");

                                googlePlacesResults.put(placeType, response.body());

                                addMapMarkers(placeType, response.body());

                                spotsDialog.dismiss();

                            }

                        }

                        @Override
                        public void onFailure(Call<MyPlaces> call, Throwable t) {
                            Log.d("responseFail", t.getStackTrace().toString());
                        }
                    });
        }

    }


    private void addMapMarkers(String placetype, MyPlaces places) {

        if (setMapIcon(placetype) == 0) {
            Toast.makeText(getActivity(), "marker icon not available", Toast.LENGTH_SHORT).show();
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();

        for (int i = 0; i < places.getResults().length; i++) {

            LatLng latLng = new LatLng(Double.parseDouble(places.getResults()[i].getGeometry().getLocation().getLat()),
                    Double.parseDouble(places.getResults()[i].getGeometry().getLocation().getLng()));

            markerOptions.position(latLng);
            markerOptions.title(places.getResults()[i].getName());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(setMapIcon(placetype)));

            mMap.addMarker(markerOptions);
        }


    }

    private int setMapIcon(String placeType) {

        int val = 0;
        switch (placeType) {

            case "bus_station":

                break;

            case "restaurant":
                break;

            case "cafe":
                val = R.drawable.marker_coffeeshop;
                break;

            case "shopping_mall":
                val = R.drawable.marker_shopping;
                break;

        }

        return val;
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

                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                mMap.setMyLocationEnabled(true);

                            }
                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


                        }

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onrequest permision " + "Permission denied");
                    }


                }

        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.nearbyMap_toolbar_filterMenu:

                showFilterMenu();

                Toast.makeText(getActivity(), "Filter menu clicked", Toast.LENGTH_SHORT).show();
                break;

        }

    }


    private void showFilterMenu() {

        DialogFragment filterDialog = FilterDialog.newInstance();

        ((FilterDialog) filterDialog).setCallback(new FilterDialog.Callback() {
            @Override
            public void onActionClick(HashMap<String, Boolean> checkBoxValues, int seekbarValue) {

                mMap.clear();
                for (String key : checkBoxValues.keySet()) {

                    nearbyPlace(seekbarValue, key);

                    Log.d(TAG, "onActionClick: key" + key + " value: " + checkBoxValues.get(key));

                }

            }
        });

        filterDialog.show(getChildFragmentManager(), "FilterMenu");
    }

}
