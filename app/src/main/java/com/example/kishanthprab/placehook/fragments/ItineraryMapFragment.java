package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kishanthprab.placehook.DataObjects.ItinerarysearchDetails;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.example.kishanthprab.placehook.Utility.Functions_Itinerary;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ItineraryMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "ItineraryFragment";

    GoogleMap mMap;

    LatLng pickedLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_itinerary_maps, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.itineraryMap);
        supportMapFragment.getMapAsync(this);


        //init


        if (Functions_Itinerary.itinerayLocation != null) {
            pickedLocation = Functions_Itinerary.itinerayLocation.getLatLng();

        }


        return view;

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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickedLocation,12));

        SetMapMarkers();


    }



    private void SetMapMarkers(){

        for (int i = 0; i< Functions_Itinerary.TotalNoOfPlaces;i++){


            Bitmap bm = createBitmapIcon(i+1);

            Log.d(TAG, "SetMapMarkers: bm is null: "+(bm==null));

            if (bm!=null) {

                LatLng latLng = new LatLng(
                        Double.parseDouble(Functions_Itinerary.ItineraryPlacesResults.get(i).getGeometry().getLocation().getLat()),
                        Double.parseDouble(Functions_Itinerary.ItineraryPlacesResults.get(i).getGeometry().getLocation().getLng())
                );
                Log.d(TAG, "SetMapMarkers: Latlng value s"+latLng.toString());

                String placeName = Functions_Itinerary.ItineraryPlacesResults.get(i).getName();

                 mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bm,bm.getWidth()*4,bm.getHeight()*4,false)))
                        .title(placeName));


            }


        }




    }

    private Bitmap createBitmapIcon(int num){

        Bitmap bitmap= null;
        try {
            bitmap = Functions.getBitmapFromURL("https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld="+num+"|222F3E|FFFFFF");

        } catch (Exception e) {

            Log.d(TAG, "setDestination: bitmap" + "failed" + e);
        }

        return bitmap;
    }


}
