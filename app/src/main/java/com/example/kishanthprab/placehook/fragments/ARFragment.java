package com.example.kishanthprab.placehook.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DashboardActivity;
import com.example.kishanthprab.placehook.DataObjects.NavigationPlaceDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceDirectionModels.MyPlaceDirection;
import com.example.kishanthprab.placehook.Helper.DirectionsJSONParser;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.example.kishanthprab.placehook.Remote.UberClient;
import com.example.kishanthprab.placehook.Utility.Functions;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class ARFragment extends Fragment {


    private static final int AUTOCOMPLETE_REQUEST_CODE_TO = 122;

    final static String TAG = "ARFragment";

    Toolbar arFragment_toolbar;
    TextView arFragment_toolbar_title;

    TextView arFragment_search;

    GoogleAPIService mService;

    FirebaseAuth mAuth;
    DatabaseReference ARPlaceInfoRef;

    MaterialButton arFragment_btn;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ar, container, false);

        //init google services
        mService = CommonGoogle.getGoogleAPIService();

        // Initialize Places.
        Places.initialize(getActivity(), getActivity().getResources().getString(R.string.google_maps_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity());


        //init toolbar
        arFragment_toolbar = (Toolbar) view.findViewById(R.id.arFragment_toolbar);
        arFragment_toolbar_title = (TextView) view.findViewById(R.id.arFragment_toolbar_title);


        ((AppCompatActivity) getActivity()).setSupportActionBar(arFragment_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        arFragment_toolbar_title.setText("AR Navigation");


        //initializing searchText
        arFragment_search = (TextView) view.findViewById(R.id.arFragment_search);
        arFragment_btn = (MaterialButton) view.findViewById(R.id.arFragment_btn_startAR);

        //destination locaton search text
        openLocationSearchIntent(arFragment_search, AUTOCOMPLETE_REQUEST_CODE_TO);


        //navigation drawer toggle
        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(getActivity(), DashboardActivity.getDrawer(), arFragment_toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        DashboardActivity.getDrawer().addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();


        return view;
    }


    private void setResultsInTextView(int resultCode, Intent data) {

        if (resultCode == AutocompleteActivity.RESULT_OK) {

            Place place = Autocomplete.getPlaceFromIntent(data);
            arFragment_search.setText("");
            String address = place.getName() + ", " + place.getAddress();
            arFragment_search.setText(address);

            writeDataToFirebase(place);


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


    private void writeDataToFirebase(Place place){


        mAuth = FirebaseAuth.getInstance();
        ARPlaceInfoRef = FirebaseDatabase.getInstance().getReference().child("ARPlace");

        final HashMap<String,String> values = new HashMap<>();
        values.put("placeName",place.getName());
        values.put("placeAddress",place.getAddress());
        values.put("placeId",place.getId());
        values.put("latitude",String.valueOf(place.getLatLng().latitude));
        values.put("longitude",String.valueOf(place.getLatLng().longitude));

        arFragment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ARPlaceInfoRef.setValue(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getActivity(), "Values stored Successfully", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailure: "+e);
                    }
                });

            }
        });

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

            setResultsInTextView(resultCode, data);

        }

        //super.onActivityResult(requestCode, resultCode, data);

    }



}
