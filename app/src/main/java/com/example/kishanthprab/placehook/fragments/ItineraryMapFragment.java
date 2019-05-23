package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DB.DB_PlaceHook;
import com.example.kishanthprab.placehook.DB.SQLiteDB_helper;
import com.example.kishanthprab.placehook.DashboardActivity;
import com.example.kishanthprab.placehook.DataObjects.ItinerarysearchDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.MyPlaceDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.Reviews;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.DataObjects.UserReview;
import com.example.kishanthprab.placehook.Helper.DirectionsJSONParser;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Recycler.ItinBottomSheetRecyclerAdapter;
import com.example.kishanthprab.placehook.Recycler.ItinBottomSheetRecyclerListItem;
import com.example.kishanthprab.placehook.Recycler.RecyclerListItem;
import com.example.kishanthprab.placehook.Recycler.ReviewRecyclerListItem;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.example.kishanthprab.placehook.Utility.FireDBUtil;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.example.kishanthprab.placehook.Utility.Functions_Itinerary;
import com.example.kishanthprab.placehook.PlaceDetailsDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItineraryMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "ItineraryFragment";

    GoogleMap mMap;
    Polyline polyline;

    GoogleAPIService mService;

    LatLng pickedLocation;

    Toolbar itinMap_toolbar;
    TextView itinMap_toolbar_title;


    //bottom sheet
    BottomSheetBehavior ItinBottomSheetBehavior;
    LinearLayout ItinTapaAtionlayout;
    View bottomSheet;
    RecyclerView bottomsheet_recycler;
    ItinBottomSheetRecyclerAdapter bottomsheet_recyclerAdapter;
    ArrayList<ItinBottomSheetRecyclerListItem> directionDetailsList;

    ArrayList<ReviewRecyclerListItem> recyclerReviewsList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_itinerary_maps, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.itineraryMap);
        supportMapFragment.getMapAsync(this);


        //init

        //init toolbar
        itinMap_toolbar = (Toolbar) view.findViewById(R.id.itinMap_toolbar);
        itinMap_toolbar_title = (TextView) view.findViewById(R.id.itinMap_toolbar_title);

        ((AppCompatActivity) getActivity()).setSupportActionBar(itinMap_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        itinMap_toolbar_title.setText("Itinerary Planner");

        //navigation drawer toggle
        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(getActivity(), DashboardActivity.getDrawer(), itinMap_toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        DashboardActivity.getDrawer().addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();

        //init bottomsheet
        ItinTapaAtionlayout = (LinearLayout) view.findViewById(R.id.itinBottomSheet_tap_action_layout);
        bottomSheet = view.findViewById(R.id.itinBottomSheet);

        ItinBottomSheetBehavior = (BottomSheetBehavior) BottomSheetBehavior.from(bottomSheet);
        ItinBottomSheetBehavior.setPeekHeight(150);
        ItinBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        ItinBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    ItinTapaAtionlayout.setVisibility(View.VISIBLE);
                }

                if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    ItinTapaAtionlayout.setVisibility(View.GONE);
                }

                if (i == BottomSheetBehavior.STATE_DRAGGING) {
                    ItinTapaAtionlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        ItinTapaAtionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ItinBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    ItinBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });


        //init bottomsheet recycler
        directionDetailsList = new ArrayList<>();
        directionDetailsList.clear();
        bottomsheet_recycler = (RecyclerView) bottomSheet.findViewById(R.id.itin_btmsheetRecyclerView);
        bottomsheet_recycler.setHasFixedSize(true);
        bottomsheet_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        bottomsheet_recyclerAdapter = new ItinBottomSheetRecyclerAdapter(directionDetailsList, getActivity());
        bottomsheet_recycler.setAdapter(bottomsheet_recyclerAdapter);


        //init google services
        mService = CommonGoogle.getGoogleAPIService();


        if (Functions_Itinerary.itinerayLocation != null) {
            pickedLocation = Functions_Itinerary.itinerayLocation.getLatLng();
        }


        return view;

    }


    public Context returnContext() {

        return getActivity();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);


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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickedLocation, 14));

        SetMapMarkers();


    }


    //load place direction details to recyclerview
    private void loadRecyclerItems() {


        final ArrayList<MyPlaceDirection> arrayList = Functions_Itinerary.ItineraryPlacesDirections;

        Log.d(TAG, "loadRecyclerItems: placesdirections is empty" + arrayList.isEmpty());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!Functions_Itinerary.ItineraryPlacesDirections.isEmpty()) {

                    for (int i = 0; i < arrayList.size(); i++) {

                        ItinBottomSheetRecyclerListItem listItem = new ItinBottomSheetRecyclerListItem(
                                String.valueOf(i + 1), Functions_Itinerary.AddedPlaces.get(i).getName()
                                , String.valueOf(i + 2), Functions_Itinerary.AddedPlaces.get(i + 1).getName()
                                , arrayList.get(i).getRoutes()[0].getLegs()[0].getDuration().getText()
                                , arrayList.get(i).getRoutes()[0].getLegs()[0].getDistance().getText()
                        );

                        directionDetailsList.add(listItem);

                    }

                    bottomsheet_recyclerAdapter.notifyDataSetChanged();

                } else {

                    handler.post(this);
                }

            }
        }, 2000);

    }


    //random place numbers
    private ArrayList<Integer> randomPlaceNumber(int totNumOfPlaces) {
        ArrayList<Integer> randomNumbers = new ArrayList<>();

        int placeCount = ItinerarysearchDetails.PlacesCount;
        Random random = new Random();

        for (int i = 0; i < placeCount; i++) {

            if (randomNumbers.isEmpty()) {
                randomNumbers.add(random.nextInt(placeCount + 1));

            } else {

                int newNumber;
                do {
                    newNumber = random.nextInt(placeCount + 1);

                } while (checkRandomNumberExist(randomNumbers, newNumber));

                randomNumbers.add(newNumber);

            }
            Log.d(TAG, "randomPlaceNumber: " + randomNumbers.get(i));

        }


        return randomNumbers;
    }

    //check random numbers
    private boolean checkRandomNumberExist(ArrayList<Integer> randomNumbersList, int generatedNumber) {

        for (int num : randomNumbersList) {
            if (num == generatedNumber) {
                return true;
            }
        }
        return false;
    }


    //get Place Details
    private void getPlaceDetails(String placeID) {

        final AlertDialog dialog = Functions.spotsDialog(getActivity());
        dialog.setMessage("Loading please wait...");
        dialog.show();

        mService.getPlaceDetails(placeID, getActivity().getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<MyPlaceDetails>() {
                    @Override
                    public void onResponse(Call<MyPlaceDetails> call, Response<MyPlaceDetails> response) {

                        if (response.isSuccessful()) {

                            Log.d(TAG, "onResponse: " + "success");


                            Functions_Itinerary.CurrentPlaceDetails = response.body();

                            //String jsonString = Functions.toJSON(response.body().toString());

                            Log.d(TAG, "onResponse: " + Functions_Itinerary.CurrentPlaceDetails.getResult().toString());


                        }

                        dialog.dismiss();
                    }


                    @Override
                    public void onFailure(Call<MyPlaceDetails> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });

    }

    //sets map marker
    private void SetMapMarkers() {

        ArrayList<Integer> placeIndexes = randomPlaceNumber(Functions_Itinerary.TotalNoOfPlaces);

        //set the markers result in function class
        Functions_Itinerary.AddedMarkers = new ArrayList<>();
        Functions_Itinerary.AddedMarkers.clear();

        Functions_Itinerary.AddedMarkersWithPlaceID = new HashMap<>();
        Functions_Itinerary.AddedMarkersWithPlaceID.clear();

        Functions_Itinerary.AddedPlaces = new ArrayList<>();
        Functions_Itinerary.AddedPlaces.clear();

        for (int i = 0; i < placeIndexes.size(); i++) {

            Bitmap bm = createBitmapIcon(i + 1);

            int plcIndex = placeIndexes.get(i);

            if (bm != null) {

                LatLng latLng = new LatLng(
                        Double.parseDouble(Functions_Itinerary.ItineraryPlacesResults.get(plcIndex).getGeometry().getLocation().getLat()),
                        Double.parseDouble(Functions_Itinerary.ItineraryPlacesResults.get(plcIndex).getGeometry().getLocation().getLng())
                );
                Log.d(TAG, "SetMapMarkers: Latlng values" + latLng.toString());

                String placeName = Functions_Itinerary.ItineraryPlacesResults.get(plcIndex).getName();
                String address = Functions_Itinerary.ItineraryPlacesResults.get(plcIndex).getPlace_id();
                String placeId = Functions_Itinerary.ItineraryPlacesResults.get(plcIndex).getPlace_id();

                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bm, bm.getWidth() * 4, bm.getHeight() * 4, false)))
                        .title(placeName));

                Functions_Itinerary.AddedMarkers.add(m);
                Functions_Itinerary.AddedMarkersWithPlaceID.put(placeId, m);
                Functions_Itinerary.AddedPlaces.add(i, Functions_Itinerary.ItineraryPlacesResults.get(plcIndex));

            }
        }

        if (polyline != null) {
            polyline.remove();
        }

        Functions_Itinerary.ItineraryPlacesDirections = new ArrayList<>();
        Functions_Itinerary.ItineraryPlacesDirections.clear();

        for (int r = 0; r < placeIndexes.size() - 1; r++) {

            LatLng origin = new LatLng(Functions_Itinerary.AddedMarkers.get(r).getPosition().latitude,
                    Functions_Itinerary.AddedMarkers.get(r).getPosition().longitude);
            LatLng destination = new LatLng(Functions_Itinerary.AddedMarkers.get(r + 1).getPosition().latitude,
                    Functions_Itinerary.AddedMarkers.get(r + 1).getPosition().longitude);


            Log.d(TAG, "SetMapMarkers: " + "origin : " + origin + " , dest :" + destination);

            DrawPath(origin, destination, 0);

        }

        //load recycler itmes
        loadRecyclerItems();

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        //getDetails
        for (String plcID : Functions_Itinerary.AddedMarkersWithPlaceID.keySet()) {

            Log.d(TAG, "onInfoWindowClick: marker " + marker.getPosition().toString() +
                    ", added marker" + Functions_Itinerary.AddedMarkersWithPlaceID.get(plcID).getPosition());

            if (marker.getPosition().toString().equals(Functions_Itinerary.AddedMarkersWithPlaceID.get(plcID).getPosition().toString())) {

                getPlaceDetails(plcID);

                Log.d(TAG, "onInfoWindowClick: " + "marker available");
                break;
            }
        }



        final DialogFragment dialog = PlaceDetailsDialog.newInstance(getActivity());

        //need to set callback
        ((PlaceDetailsDialog) dialog).setCallback(new PlaceDetailsDialog.Callback() {
            @Override
            public void onActionClick(TextView txtV) {

            }

            @Override
            public void onActionCLickForAllViews(final TextView txt_placeName, final TextView txt_totRating, final TextView txt_address, final TextView txt_numOfReviews, final ArrayList<ReviewRecyclerListItem> reviewsList) {

                recyclerReviewsList = new ArrayList<>();
                recyclerReviewsList.clear();


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (Functions_Itinerary.CurrentPlaceDetails != null) {

                            //get Reviews  from firebase
                            appUserReviewstoRecycler(Functions_Itinerary.CurrentPlaceDetails.getResult().getPlace_id());


                            //get Reviews from google
                            String name = Functions_Itinerary.CurrentPlaceDetails.getResult().getName();
                            String totRating = Functions_Itinerary.CurrentPlaceDetails.getResult().getRating();
                            String NoOfTotalRating = Functions_Itinerary.CurrentPlaceDetails.getResult().getUser_ratings_total();
                            String address = Functions_Itinerary.CurrentPlaceDetails.getResult().getFormatted_address();

                            txt_placeName.setText(name);
                            txt_address.setText(address);
                            txt_totRating.setText(totRating + "/5");
                            txt_numOfReviews.setText("Reviews");


                            assignArray(reviewsList);

                            //check whether reviews are null or not
                            if (Functions_Itinerary.CurrentPlaceDetails.getResult().getReviews() != null) {

                                int length;
                                if (Functions_Itinerary.CurrentPlaceDetails.getResult().getReviews().length <= 15) {

                                    length = Functions_Itinerary.CurrentPlaceDetails.getResult().getReviews().length;

                                } else {
                                    length = 15;

                                }

                                for (int i = 0; i < length; i++) {

                                    Reviews review = Functions_Itinerary.CurrentPlaceDetails.getResult().getReviews()[i];

                                    ReviewRecyclerListItem reviewListItem = new ReviewRecyclerListItem(
                                            review.getAuthor_name(),
                                            Double.parseDouble(review.getRating()),
                                            review.getProfile_photo_url(),
                                            review.getRelative_time_description(),
                                            review.getText()
                                    );
                                    reviewListItem.setReviewIconType("google");

                                    Log.d(TAG, "Review " + i + ": " + review.toString());

                                    recyclerReviewsList.add(reviewListItem);


                                }


                                reviewsList.addAll(recyclerReviewsList);
                                reviewsList.addAll(userReviewsList);

                            } else {
                                txt_numOfReviews.setText("No Google Reviews");
                                reviewsList.addAll(userReviewsList);
                            }
                        } else {

                            handler.post(this);
                        }

                    }
                }, 500);


            }

            @Override
            public void setImageView(final ImageView imageView) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (Functions_Itinerary.CurrentPlaceDetails != null) {
                            Bitmap bitmap = null;
                            try {
                                //  bitmap = Functions.getBitmapFromRedirectedURL("https://lh4.googleusercontent.com/-1wzlVdxiW14/USSFZnhNqxI/AAAAAAAABGw/YpdANqaoGh4/s1600-w400/Google%2BSydney");
                                bitmap = Functions
                                        .getBitmapFromRedirectedURL("https://maps.googleapis.com/maps/api/place/photo?maxheight=330&photoreference="
                                                + Functions_Itinerary.CurrentPlaceDetails.getResult().getPhotos()[0].getPhoto_reference() +
                                                "&key=" + getActivity().getResources().getString(R.string.google_maps_key));

                                Log.d(TAG, "onResponse: " + "done");
                            } catch (Exception e) {

                                Log.d(TAG, "onResponse: " + e.getMessage());
                            }

                            if (bitmap != null) {

                                imageView.setImageBitmap(bitmap);
                            }

                        } else {
                            handler.post(this);
                        }

                    }
                }, 500);


            }

        });

        dialog.show(getChildFragmentManager(), "PlaceDialog");

        //Toast.makeText(getActivity(), "marker info " + marker.getTitle(), Toast.LENGTH_SHORT).show();


    }


    ArrayList<ReviewRecyclerListItem> userReviewsList;

    //read stored reviews and put it to preferred place
    private void appUserReviewstoRecycler(String placeId) {

        userReviewsList = new ArrayList<>();
        userReviewsList.clear();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (Functions_Itinerary.appUserReviews.size() != 0) {

            for (UserReview review : Functions_Itinerary.appUserReviews) {

                if (review.getPlaceId().equals(placeId)) {
                    ReviewRecyclerListItem reviewListItem = new ReviewRecyclerListItem(
                            review.getAuthorName(),
                            Double.parseDouble(review.getRating()),
                            "https://wingslax.com/wp-content/uploads/2017/12/no-image-available.png",
                            calculateRelativeTime(Long.parseLong(review.getUnixTime())),
                            review.getReviewText()
                    );

                    reviewListItem.setReviewIconType("placeHook");

                    userReviewsList.add(reviewListItem);
                }

                Log.d(TAG, "appUserReviewstoRecycler: " + review.toString());
            }

        }


    }


    //to Calculate relative time from epochtime
    private String calculateRelativeTime(long time) {

        int unixTimeSec = Integer.parseInt(String.valueOf((Calendar.getInstance().getTimeInMillis() - time) / 1000));

        Log.d(TAG, "calculateRelativeTime: sec" + unixTimeSec);

        int minInSec = 60;
        int hourInSec = minInSec * 60;
        int dayInSec = hourInSec * 24;
        int weekInSec = dayInSec * 7;
        int monthInSec = dayInSec * 30;
        int yearInSec = dayInSec * 365;


        if (unixTimeSec >= yearInSec) {
            int i = unixTimeSec / yearInSec;
            return (i == 1) ? "a year ago" : i + " years ago";

        } else if (unixTimeSec >= monthInSec) {
            int i = unixTimeSec / monthInSec;
            return (i == 1) ? "a month ago" : i + " months ago";

        } else if (unixTimeSec >= weekInSec) {
            int i = unixTimeSec / weekInSec;
            return (i == 1) ? "a week ago" : i + " weeks ago";

        } else if (unixTimeSec >= dayInSec) {
            int i = unixTimeSec / dayInSec;
            return (i == 1) ? "a day ago" : i + " days ago";

        } else if (unixTimeSec >= hourInSec) {
            int i = unixTimeSec / hourInSec;
            return (i == 1) ? "a hour ago" : i + " hours ago";

        } else if (unixTimeSec >= minInSec) {
            int i = unixTimeSec / minInSec;
            return (i == 1) ? "a minute ago" : i + " minutes ago";

        } else {
            return unixTimeSec + " seconds ago";
        }


    }

    void assignArray(ArrayList<ReviewRecyclerListItem> list) {

        list = new ArrayList<>();
    }


    private Bitmap createBitmapIcon(int num) {

        //String url = "https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + num + "|222F3E|FFFFFF";
        String url = "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_black" + num + ".png";

        Bitmap bitmap = null;
        try {
            bitmap = Functions.getBitmapFromURL(url);

        } catch (Exception e) {

            Log.d(TAG, "setDestination: bitmap" + "failed" + e);
        }

        return bitmap;
    }


    //draw path
    private void DrawPath(LatLng originLocation,
                          LatLng destinationLocation, final int index) {

        String origin = new StringBuilder(String.valueOf(originLocation.latitude))
                .append(",")
                .append(String.valueOf(originLocation.longitude))
                .toString();
        String destination = new StringBuilder(String.valueOf(destinationLocation.latitude))
                .append(",")
                .append(String.valueOf(destinationLocation.longitude))
                .toString();
        ;

        mService.getDirections(origin, destination, getActivity().getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<MyPlaceDirection>() {
                    @Override
                    public void onResponse(Call<MyPlaceDirection> call, Response<MyPlaceDirection> response) {

                        if (response.isSuccessful()) {

                            Log.d(TAG, "onResponse: " + response.body().toString());
                            Log.d(TAG, "onResponse: " + " scalr response success");


                            //MyPlaceDirection PlaceDirection = response.body();
                            Functions_Itinerary.ItineraryPlacesDirections.add(index, response.body());
                            String jsonString = Functions.toJSON(Functions_Itinerary.ItineraryPlacesDirections.get(index));

                            Log.d(TAG, "onResponse: Place Direction results " + jsonString);

                            new ParserTask().execute(jsonString);

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

                    // Log.d(TAG, "onPostExecute: " + position.toString());
                    points.add(position);

                }


                polylineOptions.addAll(points);

                polylineOptions.width(13);
                polylineOptions.color(getActivity().getResources().getColor(R.color.colorAccent));
                polylineOptions.geodesic(true);


            }
            polyline = mMap.addPolyline(polylineOptions);
            alertDialog.dismiss();

        }
    }

}
