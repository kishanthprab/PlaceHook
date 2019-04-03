package com.example.kishanthprab.placehook.fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DataObjects.ItinerarysearchDetails;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Utility.TimePickerDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;

public class ItineraryPlannerFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "ItineraryFragment";
    private static final int AUTOCOMPLETE_REQUEST_CODE_FROM = 12;
    private static final int AUTOCOMPLETE_REQUEST_CODE_TO = 13;

    TextView txt_from;
    TextView txt_to;
    Spinner spinner_duration;
    Spinner spinner_placeType;

    MaterialButton btn_generateTrip;

    ItinerarysearchDetails itineraryDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_itineraryplanner, container, false);


        //init components

        txt_from = (TextView) v.findViewById(R.id.txt_ip_from);
        txt_to = (TextView) v.findViewById(R.id.txt_ip_to);
        spinner_duration = (Spinner) v.findViewById(R.id.spinner_ip_duration);
        spinner_placeType = (Spinner) v.findViewById(R.id.spinner_ip_placeType);
        btn_generateTrip = (MaterialButton) v.findViewById(R.id.btn_ip_generateTrip);


        // Initialize Places.
        Places.initialize(getActivity(), getActivity().getResources().getString(R.string.google_maps_key));

        //inti search details
        itineraryDetails = new ItinerarysearchDetails();

        txt_from.setOnClickListener(this);
        txt_to.setOnClickListener(this);
        spinner_duration.setOnItemSelectedListener(this);
        spinner_placeType.setOnItemSelectedListener(this);
        btn_generateTrip.setOnClickListener(this);


        ArrayList<CharSequence> spinnerArray = new ArrayList<CharSequence>();
        spinnerArray.add("natural feature");
        spinnerArray.add("12313");


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_placeType.setAdapter(adapter);

        List<String> numberList = new ArrayList<String>(Arrays.asList("1","2","3","4"));

        ArrayAdapter<String> durationAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,numberList);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_duration.setAdapter(durationAdapter);

        return v;
    }

    @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.txt_ip_from:

                    createLocationSearchIntent(AUTOCOMPLETE_REQUEST_CODE_FROM);

                    Log.d(TAG, "onClick: " + "From Clicked");
                    Toast.makeText(getActivity(), "From Clicked", Toast.LENGTH_SHORT).show();

                    break;

                case R.id.txt_ip_to:

                    createLocationSearchIntent(AUTOCOMPLETE_REQUEST_CODE_TO);
                    Log.d(TAG, "onClick: " + "to Clicked");
                    Toast.makeText(getContext(), "To Clicked", Toast.LENGTH_SHORT).show();
                    break;


                case R.id.btn_ip_generateTrip:
                    if (validateGenerateItineraryFields()){

                        LatLng startLatLng =itineraryDetails.getPlaceFrom().getLatLng();
                        LatLng EndLatLng = itineraryDetails.getPlaceTo().getLatLng();
                        float[] distresults = new float[5];
                        Location.distanceBetween(startLatLng.latitude,startLatLng.longitude
                        ,EndLatLng.latitude,EndLatLng.longitude,distresults);

                        Log.d(TAG, "onClick: distance"+distresults[0]);
                    }
                    Toast.makeText(getContext(), "To Clicked", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        //validate fields
    private boolean validateGenerateItineraryFields() {


        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.spinner_ip_placeType:


               // Toast.makeText(getContext(), "spinner item Clicked : " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                break;

            case R.id.spinner_ip_duration:


                //Toast.makeText(getContext(), "spinner item Clicked : " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case AUTOCOMPLETE_REQUEST_CODE_FROM:

                if (resultCode == AutocompleteActivity.RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);


                    itineraryDetails.setPlaceFrom(Autocomplete.getPlaceFromIntent(data));

                    txt_from.setText(itineraryDetails.getPlaceFrom().getName());

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Log.i(TAG, "error " +Autocomplete.getStatusFromIntent(data).getStatusMessage());
                } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                    Log.i(TAG, "Place: " + "result cancelled");
                }

                break;

            case AUTOCOMPLETE_REQUEST_CODE_TO:


                if (resultCode == AutocompleteActivity.RESULT_OK) {

                    itineraryDetails.setPlaceTo(Autocomplete.getPlaceFromIntent(data));


                    txt_to.setText(itineraryDetails.getPlaceTo().getName());

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Log.i(TAG, "error " +Autocomplete.getStatusFromIntent(data).getStatusMessage());
                } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                    Log.i(TAG, "Place: " + "result cancelled");
                }

                break;


        }

    }


    private void createLocationSearchIntent(int requestCode) {

        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.VIEWPORT, Place.Field.ADDRESS,
                Place.Field.TYPES, Place.Field.PHONE_NUMBER);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setLocationBias(RectangularBounds.newInstance(new LatLng(6.859629, 79.816731), new LatLng(6.984130, 79.888132)))
                 .setTypeFilter(TypeFilter.CITIES)
                .setCountry("LK")
                .build(getActivity());
        startActivityForResult(intent, requestCode);
    }


}
