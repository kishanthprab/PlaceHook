package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DataObjects.ItinerarysearchDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.MyPlaceDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.Helper.DirectionsJSONParser;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.example.kishanthprab.placehook.Utility.Functions_Itinerary;
import com.example.kishanthprab.placehook.Utility.PlaceDetailsDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItineraryMapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "ItineraryFragment";

    GoogleMap mMap;
    Polyline polyline;

    GoogleAPIService mService;

    LatLng pickedLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_itinerary_maps, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.itineraryMap);
        supportMapFragment.getMapAsync(this);


        //init


        //init google services
        mService = CommonGoogle.getGoogleAPIService();


        if (Functions_Itinerary.itinerayLocation != null) {
            pickedLocation = Functions_Itinerary.itinerayLocation.getLatLng();

        }



        return view;

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

        mService.getPlaceDetails(placeID,getActivity().getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<MyPlaceDetails>() {
                    @Override
                    public void onResponse(Call<MyPlaceDetails> call, Response<MyPlaceDetails> response) {

                        if (response.isSuccessful()){

                            Log.d(TAG, "onResponse: "+"success");

                            Functions_Itinerary.CurrentPlaceDetails = response.body();

                            //String jsonString = Functions.toJSON(response.body().toString());

                            Log.d(TAG, "onResponse: "+Functions_Itinerary.CurrentPlaceDetails.getResult().toString());



                        }

                    dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyPlaceDetails> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+ t.getMessage());
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
                Functions_Itinerary.AddedMarkersWithPlaceID.put(placeId,m);
            }
        }

        if (polyline != null) {
            polyline.remove();
        }

        for (int r =0;r<placeIndexes.size()-1;r++){

            LatLng origin = new LatLng(Functions_Itinerary.AddedMarkers.get(r).getPosition().latitude,
                    Functions_Itinerary.AddedMarkers.get(r).getPosition().longitude);
            LatLng destination = new LatLng(Functions_Itinerary.AddedMarkers.get(r+1).getPosition().latitude,
                    Functions_Itinerary.AddedMarkers.get(r+1).getPosition().longitude);



            Log.d(TAG, "SetMapMarkers: "+ "origin : " +origin + " , dest :"+destination);
            DrawPath(origin,destination);

        }


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        //getDetails
        for (String plcID : Functions_Itinerary.AddedMarkersWithPlaceID.keySet()){

            Log.d(TAG, "onInfoWindowClick: marker "+ marker.getPosition().toString() +
                    ", added marker"+Functions_Itinerary.AddedMarkersWithPlaceID.get(plcID).getPosition());

            if (marker.getPosition().toString().equals(Functions_Itinerary.AddedMarkersWithPlaceID.get(plcID).getPosition().toString())){

                getPlaceDetails(plcID);

                Log.d(TAG, "onInfoWindowClick: "+"marker available");
                break;
            }
        }

        DialogFragment dialog = PlaceDetailsDialog.newInstance();

        //need to set callback
        ((PlaceDetailsDialog) dialog).setCallback(new PlaceDetailsDialog.Callback() {
            @Override
            public void onActionClick(TextView txtV) {

            }

            @Override
            public void onActionCLickForAllViews(final TextView txt_placeName, final TextView txt_totRating, TextView txt_distance, final TextView txt_address, final TextView txt_numOfReviews) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Functions_Itinerary.CurrentPlaceDetails!= null){

                            String name = Functions_Itinerary.CurrentPlaceDetails.getResult().getName();
                            String totRating =Functions_Itinerary.CurrentPlaceDetails.getResult().getRating();
                            String NoOfTotalRating = Functions_Itinerary.CurrentPlaceDetails.getResult().getUser_ratings_total();
                            String address = Functions_Itinerary.CurrentPlaceDetails.getResult().getFormatted_address();

                            txt_placeName.setText(name);
                            txt_address.setText(address);
                            txt_totRating.setText(totRating + "/5");
                            txt_numOfReviews.setText(NoOfTotalRating +" recommendations");

                        }else {

                            handler.post(this);
                        }

                    }
                },500);



            }
        });

        dialog.show(getChildFragmentManager(),"PlaceDialog");

        Toast.makeText(getActivity(), "marker info " + marker.getTitle() , Toast.LENGTH_SHORT).show();


    }


    private Bitmap createBitmapIcon(int num) {

        Bitmap bitmap = null;
        try {
            bitmap = Functions.getBitmapFromURL("https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + num + "|222F3E|FFFFFF");

        } catch (Exception e) {

            Log.d(TAG, "setDestination: bitmap" + "failed" + e);
        }

        return bitmap;
    }


    //draw path
    private void DrawPath(LatLng originLocation,
                          LatLng destinationLocation) {

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

                polylineOptions.width(13);
                polylineOptions.color(getActivity().getResources().getColor(R.color.colorAccent));
                polylineOptions.geodesic(true);


            }
            polyline = mMap.addPolyline(polylineOptions);
            alertDialog.dismiss();

        }
    }

}
