package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DashboardActivity;
import com.example.kishanthprab.placehook.DataObjects.NavigationPlaceDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.Geocoded_waypoints;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.Geometry;
import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.Result;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.Routes;
import com.example.kishanthprab.placehook.Helper.DirectionsJSONParser;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.example.kishanthprab.placehook.Remote.UberClient;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.example.kishanthprab.placehook.Utility.ParserPolylineTask;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//import com.google.android.gms.location.LocationListener;

public class NavigationMapsFragment extends Fragment implements OnMapReadyCallback,View.OnClickListener {

    GoogleMap mMap;
    Marker userCurrentLocation;
    Marker destinationMarker = null;
    Marker originMarker = null;

    Location LastLocation = null;

    NavigationPlaceDetails destinationInfoObject;
    NavigationPlaceDetails originInfoObject;

    private static final int Request_user_Location_code = 2;

    private static final int AUTOCOMPLETE_REQUEST_CODE_FROM = 2112;
    private static final int AUTOCOMPLETE_REQUEST_CODE_TO = 2212;

    final static String TAG = "NavigationFragment";

    Toolbar navmap_toolbar;
    TextView navMap_toolbar_title;

    TextView navigation_searchCurrent;
    TextView navigation_searchDestination;


    GoogleAPIService mService;

    FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    Polyline polyline;

    //bottom sheet
    BottomSheetBehavior navi_BottomSheetBehavior;
    LinearLayout navi_TapActionlayout;
    View navi_bottomSheet;

    //uber
    RideRequestButton btn_uberRide;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_navigation_maps, container, false);

        //init google services
        mService = CommonGoogle.getGoogleAPIService();
        //mService = CommonGoogle.getGoogleAPIServiceScalars();

        // Initialize Places.
        Places.initialize(getActivity(), getActivity().getResources().getString(R.string.google_maps_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity());


        //init toolbar
        navmap_toolbar = (Toolbar) view.findViewById(R.id.navMap_toolbar);
        navMap_toolbar_title = (TextView) view.findViewById(R.id.navMap_toolbar_title);

        ((AppCompatActivity) getActivity()).setSupportActionBar(navmap_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        navMap_toolbar_title.setText("Navigation");


        //init bottomsheet
        navi_TapActionlayout = (LinearLayout) view.findViewById(R.id.navi_tap_action_layout);
        navi_bottomSheet = view.findViewById(R.id.navi_bottom_sheet);

        navi_BottomSheetBehavior = (BottomSheetBehavior) BottomSheetBehavior.from(navi_bottomSheet);
        navi_BottomSheetBehavior.setPeekHeight(150);
        navi_BottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        navi_BottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    navi_TapActionlayout.setVisibility(View.VISIBLE);
                }

                if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    navi_TapActionlayout.setVisibility(View.GONE);
                }

                if (i == BottomSheetBehavior.STATE_DRAGGING) {
                    navi_TapActionlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        navi_TapActionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navi_BottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    navi_BottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });


        //inti uberclient
        UberClient.initiateUberClient();
        btn_uberRide = (RideRequestButton) view.findViewById(R.id.navi_btn_uberRide);


        //navigation drawer toggle
        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(getActivity(), DashboardActivity.getDrawer(), navmap_toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        DashboardActivity.getDrawer().addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.navigationMap);
        mapFragment.getMapAsync(this);


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = new FusedLocationProviderClient(getActivity());

        //initializing searchText
        navigation_searchCurrent = (TextView) view.findViewById(R.id.edt_Csearch);


        //destination locaton search text
        openLocationSearchIntent(navigation_searchCurrent, AUTOCOMPLETE_REQUEST_CODE_TO);

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

    //create uberRideRequest
    private void setUberRide(LatLng pickup,String pickupAddress,LatLng drop,String dropAddress){

        Log.d(TAG, "setUberRide: pickup" +pickupAddress +":" +pickup.toString() + " drop " +dropAddress+":"+drop);
        RideParameters rideParams = new RideParameters.Builder()
                // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                //.setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                .setProductId("666856eb-f007-45bd-a961-6441eb600c98") //for uberzip
                .setDropoffLocation(
                        drop.latitude, drop.longitude, "Drop Point", dropAddress)
                .setPickupLocation(pickup.latitude, pickup.longitude, "Pickup Point", pickupAddress)
                .build();

        btn_uberRide.setRideParameters(rideParams);
        btn_uberRide.setSession(UberClient.getNewUberSession());
        btn_uberRide.loadRideInformation();

    }


    //set Marker on map
    private void setMarkerOnMap(Marker marker, NavigationPlaceDetails placeObj) {

        LatLng currentLocation = null;
        if (marker != null) {
            marker.remove();
        }

        //set starting Location
        if (LastLocation != null) {

            currentLocation = new LatLng(LastLocation.getLatitude(),LastLocation.getLongitude());
            originMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_origin))
                    .title("Start Location"));
        }


        //set up destination marker
        if (marker == destinationMarker) {
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(placeObj.getLocation())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_destination))
                    //.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()*4,bitmap.getHeight()*4,false)))
                    .title(placeObj.getName()));
        }

        //route path
        DrawPath(LastLocation, placeObj.getLocation()); //uses scalars

        final String currentAddress = Functions.geolocatePlace(currentLocation,getActivity());
        final String destAddress = Functions.geolocatePlace(placeObj.getLocation(),getActivity());

        //set uber ride info
        setUberRide(currentLocation,destAddress,placeObj.getLocation(),placeObj.getAddress());
       // btn_uberRide.loadRideInformation();


    }


    //draw path to destination
    private void DrawPath(Location lastLocation,
                          LatLng destinationLocation) {
        if (polyline != null) {
            polyline.remove();
        }

        String origin = new StringBuilder(String.valueOf(lastLocation.getLatitude()))
                .append(",")
                .append(String.valueOf(lastLocation.getLongitude()))
                .toString();
        String destination = new StringBuilder(String.valueOf(destinationLocation.latitude))
                .append(",")
                .append(String.valueOf(destinationLocation.longitude))
                .toString();

        mService.getDirections(origin, destination, getActivity().getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<MyPlaceDirection>() {
                    @Override
                    public void onResponse(Call<MyPlaceDirection> call, Response<MyPlaceDirection> response) {

                        if (response.isSuccessful()) {

                            Log.d(TAG, "onResponse: " + response.body().toString());
                            MyPlaceDirection PlaceDirection = response.body();
                            String jsonString = Functions.toJSON(PlaceDirection);

                            Log.d(TAG, "onResponse: " + jsonString);

                            new ParserTask().execute(jsonString);

                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaceDirection> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + "failed" + t.getMessage());
                    }
                });
    }

    @Override
    public void onClick(View v) {

    }


    //inner class to parse data
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        AlertDialog alertDialog = Functions.spotsDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            alertDialog.setMessage("Loading Please wait...");
            alertDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                //get json from results

                jsonObject = new JSONObject(strings[0]);
                //parse json
                DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
                routes = directionsJSONParser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);

            ArrayList<LatLng> points = null;
            PolylineOptions polylineOptions = null;

            for (int i = 0; i < lists.size(); i++) {

                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++) {

                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat, lng);

                    Log.d(TAG, "onPostExecute: " + position.toString());
                    points.add(position);

                }


                polylineOptions.addAll(points);

                polylineOptions.width(15);
                polylineOptions.color(getActivity().getResources().getColor(R.color.colorPrimaryDark));
                polylineOptions.geodesic(true);


            }
            polyline = mMap.addPolyline(polylineOptions);
            alertDialog.dismiss();

        }
    }


//----------


    //stop location updates
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
                    userCurrentLocation = mMap.addMarker(new MarkerOptions()
                            .position(userloc)
                            .title("You are here!")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.userlocation)));

                    // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userloc,21));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userloc, 17));

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


    }

    private void setResultsInTextView(int resultCode, Intent data, Marker marker) {

        if (resultCode == AutocompleteActivity.RESULT_OK) {

            Place place = Autocomplete.getPlaceFromIntent(data);
            navigation_searchCurrent.setText("");

            String address = place.getName() + ", " + place.getAddress();

            //setCurrent Result
            destinationInfoObject = new NavigationPlaceDetails();

            destinationInfoObject.setPlace_id(place.getId());
            destinationInfoObject.setName(place.getName());
            destinationInfoObject.setAddress(place.getAddress());
            destinationInfoObject.setLocation(place.getLatLng());


            setMarkerOnMap(marker, destinationInfoObject);
            navigation_searchCurrent.setText(address);


        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i(TAG, "Place: " + "result error");
            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
            // The user canceled the operation.
            Log.i(TAG, "Place: " + "result cancelled");
        }
    }

    //open autocomplete search intent
    private void openLocationSearchIntent(TextView textView, final int code) {

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "textview clicked", Toast.LENGTH_SHORT).show();

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                        Place.Field.LAT_LNG, Place.Field.VIEWPORT, Place.Field.ADDRESS,
                        Place.Field.TYPES, Place.Field.PHONE_NUMBER);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .setLocationBias(RectangularBounds.newInstance(new LatLng(6.859629, 79.816731), new LatLng(6.984130, 79.888132)))
                        // .setTypeFilter(TypeFilter.ADDRESS)
                        .setCountry("LK")
                        .build(getActivity());
                startActivityForResult(intent, code);

            }
        });

        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    Log.d(TAG, "onEditorAction: Clicked");
                }
                return false;
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_TO) {

            setResultsInTextView(resultCode, data, destinationMarker);

        }

        //super.onActivityResult(requestCode, resultCode, data);

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


}
