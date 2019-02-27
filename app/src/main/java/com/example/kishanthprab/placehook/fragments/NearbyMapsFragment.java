package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
//import com.google.android.gms.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.google.android.gms.flags.IFlagProvider;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyMapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    LocationManager locationManager;
    GoogleMap mMap;
    Marker userCurrentLocation;

    Location LastLocation;

    private static final int Request_user_Location_code = 2;
    final static String TAG = "NearbyFragment";

    GoogleAPIService mService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.nearbyMap);
        mapFragment.getMapAsync(this);


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize services
        mService = CommonGoogle.getGoogleAPIService();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT < 23) {


            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_Location_code);
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }


        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(23,12));
        markerOptions.title("Current location");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));

        mMap.addMarker(markerOptions);


    }

    @Override
    public void onLocationChanged(Location location) {

        LastLocation = location;

        if (userCurrentLocation != null) {


            userCurrentLocation.remove();
        }



        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());



        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current location");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));

        userCurrentLocation = mMap.addMarker(markerOptions);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));


        Toast.makeText(getActivity(), "Locaa " + location.toString(), Toast.LENGTH_SHORT).show();
        nearbyPlace("hospital");
        Log.d(TAG, "Locaa " + location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void nearbyPlace(final String placeType) {
        mMap.clear();

        double latitude = LastLocation.getLatitude();
        double longitude = LastLocation.getLongitude();

        String url = getURL(latitude, longitude, placeType);
        mService.getNearbyPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        if (response.isSuccessful()) {

                            Log.d("response","susscessfull");

                            for (int i = 0;i<response.body().getResults().length;i++){

                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                Location placeLocation = googlePlace.getGeometry().getLocation();

                                String PlaceName = googlePlace.getName();
                                String Vicinity = googlePlace.getVicinity();
                                LatLng Place_latLng = new LatLng(placeLocation.getLatitude(),placeLocation.getLongitude());
                                markerOptions.position(Place_latLng);
                                markerOptions.title(PlaceName);

                                if (placeType.equals("hospital")){

                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }
                                else {

                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                }

                                //mMap.addMarker(markerOptions);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(Place_latLng));
                                //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {
                        Log.d("responseFail",t.getStackTrace().toString());
                    }
                });

    }

    private String getURL(double latitude, double longitude, String placeType) {

        StringBuilder googlePlacesURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlacesURL.append("location="+latitude+","+longitude);
        googlePlacesURL.append("&radius="+10000);
        googlePlacesURL.append("&type="+placeType);
        googlePlacesURL.append("&key="+getResources().getString(R.string.google_maps_key));

        Log.d("getURl",googlePlacesURL.toString());


        return googlePlacesURL.toString();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case Request_user_Location_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                    }
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onrequest permision " + "Permission denied");
                }
                return;

        }


    }
}
