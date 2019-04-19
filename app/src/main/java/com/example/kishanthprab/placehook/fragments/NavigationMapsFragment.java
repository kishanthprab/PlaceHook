package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DashboardActivity;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.Geocoded_waypoints;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.Geometry;
import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.Result;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.Routes;
import com.example.kishanthprab.placehook.Helper.DirectionsJSONParser;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
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

public class NavigationMapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    Marker userCurrentLocation;
    Marker DestinationMarker = null;

    Location LastLocation = null;

    Address destAddress = null;
    String destAddressName;

    private static final int Request_user_Location_code = 2;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1123;

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

        //init components

        //init toolbar
        navmap_toolbar = (Toolbar)view.findViewById(R.id.navMap_toolbar);
        navMap_toolbar_title =(TextView)view.findViewById(R.id.navMap_toolbar_title);

        ((AppCompatActivity)getActivity()).setSupportActionBar(navmap_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        navMap_toolbar_title.setText("Navigation");

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


        //current locaton search text
        CLocationsearchText();

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

       /*

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
          */


    }

    private void CLocationsearchText() {


        navigation_searchCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "current text clicked", Toast.LENGTH_SHORT).show();

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
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });


        navigation_searchCurrent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute method for searching
                    //geolocatePlace();
                    Log.d(TAG, "onEditorAction: Clicked");
                }
                return false;
            }
        });

    }

    private void geolocatePlace(LatLng latLng) {
        Log.d(TAG, "geolocatePlace: geolocation");

        //String searchString = navigation_searchCurrent.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> ListAddresses = new ArrayList<>();
        try {

            //  ListAddresses = geocoder.getFromLocationName(searchString, 1);
            ListAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);


        } catch (Exception e) {

            Log.d(TAG, "geolocatePlace: Exception" + e.getMessage());

        }

        if (ListAddresses.size() > 0) {

            destAddress = ListAddresses.get(0);
            String t = destAddress.getAddressLine(0);
            //navigation_searchCurrent.setText(t);
            setDestination();
            Toast.makeText(getActivity(), "address : " + destAddress.toString(), Toast.LENGTH_SHORT).show();

            Log.d(TAG, "geolocatePlace: Found location : " + destAddress.toString());
        }


    }


    //setDestination
    private void setDestination() {

        if (DestinationMarker != null) {

            DestinationMarker.remove();
        }

        //LatLng destinationLatlng = new LatLng(destAddress.getLatitude(), destAddress.getLongitude());

        LatLng destinationLatlng = new LatLng(
                Double.parseDouble(CommonGoogle.currentResult.getGeometry().getLocation().getLat()),
                Double.parseDouble(CommonGoogle.currentResult.getGeometry().getLocation().getLng())
        );


        Bitmap bitmap= null;
        try {
            bitmap = Functions.getBitmapFromURL("https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=2|222F3E|FFFFFF");

        } catch (Exception e) {

            Log.d(TAG, "setDestination: bitmap" + "failed" + e);
        }

        if (bitmap!=null) {

            DestinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(destinationLatlng)
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination))
                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()*4,bitmap.getHeight()*4,false)))
                    .title(CommonGoogle.currentResult.getName()));

        }
        //route path
        DrawPath(LastLocation, destinationLatlng); //uses scalars

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

                        //Log.d(TAG, "onResponse: "+ response.body());

                        if (response.isSuccessful()) {

                            Log.d(TAG, "onResponse: " + response.body().toString());
                            Log.d(TAG, "onResponse: " + " scalr response success");


                            MyPlaceDirection PlaceDirection = response.body();

                            String jsonString = Functions.toJSON(PlaceDirection);

                            Log.d(TAG, "onResponse: " + jsonString);

                            new ParserTask().execute(jsonString);
                            //ParserPolylineTask pTask = new ParserPolylineTask();
                            //pTask.execute(mMap,polyline,jsonString);

                        }


                    }

                    @Override
                    public void onFailure(Call<MyPlaceDirection> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + "failed" + t.getMessage());
                    }
                });
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
                polylineOptions.color(getActivity().getResources().getColor(R.color.colorAccent));
                polylineOptions.geodesic(true);


            }
            polyline = mMap.addPolyline(polylineOptions);
            alertDialog.dismiss();

        }
    }

    /*//place details get call
    private void getPlaceDetails(String placeID) {

        StringBuilder googlePlacesURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlacesURL.append("placeid=" + placeID);
        googlePlacesURL.append("&fields=");
        googlePlacesURL.append("address_component,");
        googlePlacesURL.append("formatted_address,");
        googlePlacesURL.append("");
        googlePlacesURL.append("");
        googlePlacesURL.append("&key=" + getResources().getString(R.string.google_maps_key));

            String url = getURL(LastLocation.getLatitude(), LastLocation.getLongitude(), placeType);
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
                                alertDialog.dismiss();


                            }

                        }

                        @Override
                        public void onFailure(Call<MyPlaces> call, Throwable t) {
                            Log.d("responseFail", t.getStackTrace().toString());
                        }
                    });

    }*/


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
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                navigation_searchCurrent.setText("");
                //geolocate
                //geolocatePlace(place.getLatLng());


                String DestinationAddress = place.getName() + ", " + place.getAddress();
                //destAddressName = place.getName();

                CommonGoogle.currentResult = new Result();
                //setCurrent Result
                CommonGoogle.currentResult.setPlace_id(place.getId());
                CommonGoogle.currentResult.setName(place.getName());
                CommonGoogle.currentResult.setFormatted_address(place.getAddress());
                CommonGoogle.currentResult.setGeometry(new Geometry());
                CommonGoogle.currentResult.getGeometry().setLocation(new com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.Location());
                CommonGoogle.currentResult.getGeometry().getLocation().setLat(String.valueOf(place.getLatLng().latitude));
                CommonGoogle.currentResult.getGeometry().getLocation().setLng(String.valueOf(place.getLatLng().longitude));

                setDestination();
                navigation_searchCurrent.setText(DestinationAddress);


                // Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress() + ", " + place.getLatLng());
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
