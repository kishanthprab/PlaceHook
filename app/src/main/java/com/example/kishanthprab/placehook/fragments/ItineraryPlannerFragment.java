package com.example.kishanthprab.placehook.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DashboardActivity;
import com.example.kishanthprab.placehook.DataObjects.ItinerarysearchDetails;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.Recycler.RecyclerListItem;
import com.example.kishanthprab.placehook.Remote.CommonGoogle;
import com.example.kishanthprab.placehook.Remote.GoogleAPIService;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.example.kishanthprab.placehook.Utility.Functions_Itinerary;
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
import java.util.HashMap;
import java.util.List;

import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItineraryPlannerFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "ItineraryFragment";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 12;

    TextView txt_tripLocation;
    Spinner spinner_NoOfPlaces;
    Spinner spinner_placeType;

    MaterialButton btn_generateTrip;

    List<String> numberList;
    ArrayAdapter<String> NoOfPlacesAdapter;
    boolean validationBool = false;

    ItinerarysearchDetails itineraryDetails;
    Toolbar itinPlan_toolbar;
    TextView itinPlan_toolbar_title;

    boolean zeroException = false;


    GoogleAPIService googleService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_itineraryplanner, container, false);


        //init components

        txt_tripLocation = (TextView) v.findViewById(R.id.txt_ip_location);
        spinner_NoOfPlaces = (Spinner) v.findViewById(R.id.spinner_ip_NoOfPlaces);
        spinner_placeType = (Spinner) v.findViewById(R.id.spinner_ip_placeType);
        btn_generateTrip = (MaterialButton) v.findViewById(R.id.btn_ip_generateTrip);
        btn_generateTrip.setEnabled(false);

        //init toolbar
        itinPlan_toolbar = (Toolbar) v.findViewById(R.id.itinPlan_toolbar);
        itinPlan_toolbar_title = (TextView) v.findViewById(R.id.itinPlan_toolbar_title);

        ((AppCompatActivity) getActivity()).setSupportActionBar(itinPlan_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        itinPlan_toolbar_title.setText("Itinerary Planner");

        //navigation drawer toggle
        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(getActivity(), DashboardActivity.getDrawer(), itinPlan_toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        DashboardActivity.getDrawer().addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();


        //init google services
        googleService = CommonGoogle.getGoogleAPIService();

        // Initialize Places.
        Places.initialize(getActivity(), getActivity().getResources().getString(R.string.google_maps_key));

        //inti search details
        itineraryDetails = new ItinerarysearchDetails();

        txt_tripLocation.setOnClickListener(this);
        spinner_NoOfPlaces.setOnItemSelectedListener(this);
        spinner_placeType.setOnItemSelectedListener(this);
        btn_generateTrip.setOnClickListener(this);


        ArrayList<String> spinnerArray = itineraryDetails.getPlaceTypeList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_placeType.setAdapter(adapter);

        spinner_NoOfPlaces.setVisibility(View.INVISIBLE);

        //List<String> numberList = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"));
        numberList = new ArrayList<String>();

        NoOfPlacesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, numberList);
        NoOfPlacesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_NoOfPlaces.setAdapter(NoOfPlacesAdapter);


        return v;
    }


    //validate fields
    private boolean validateGenerateItineraryFields() {

        if (txt_tripLocation.getText().toString().equals("") || txt_tripLocation.getText().toString().equals(null)) {

            Toast.makeText(getActivity(), "Set the Trip Location", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //to return url
    private String getURL(double latitude, double longitude, int radius, String placeType) {

        StringBuilder googlePlacesURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlacesURL.append("location=" + latitude + "," + longitude);
        googlePlacesURL.append("&radius=" + radius);
        googlePlacesURL.append("&type=" + placeType);
        googlePlacesURL.append("&key=" + getResources().getString(R.string.google_maps_key));

        Log.d("getURl", googlePlacesURL.toString());

        return googlePlacesURL.toString();
    }


    private String getURL(String query) {

        StringBuilder googlePlacesURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");

        googlePlacesURL.append("query=" + query);
        googlePlacesURL.append("&key=" + getResources().getString(R.string.google_maps_key));

        Log.d("getURl", googlePlacesURL.toString());

        return googlePlacesURL.toString();
    }


    //itinerary places get call
    private void SearchItineraryPlaces() {

        final AlertDialog alertDialog = Functions.spotsDialog(getActivity());

        alertDialog.setMessage("searching places! please wait...");
        alertDialog.show();


        if (itineraryDetails.getTripLocation().getLatLng() != null) {

            LatLng loc = itineraryDetails.getTripLocation().getLatLng();

            String url = "";
            String placeType = itineraryDetails.returnTypeFieldName(spinner_placeType.getSelectedItem().toString());
            if (placeType.equals("beach_side_places")) {
                url = getURL("public beaches in " + itineraryDetails.getTripLocation().getName());
            } else {
                url = getURL(loc.latitude, loc.longitude, 3000, placeType);
            }

            googleService.getNearbyPlaces(url)
                    .enqueue(new Callback<MyPlaces>() {
                        @Override
                        public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                            if (response.isSuccessful()) {


                                Log.d("response", "successfull");


                                if (response.body().getResults().length == 0) {

                                    alertDialog.dismiss();
                                    txt_tripLocation.setText("");
                                    Toast.makeText(getActivity(), "Sorry! there are no places in that area. Try again with the different category or location", Toast.LENGTH_SHORT).show();

                                } else {


                                    Functions_Itinerary.TotalNoOfPlaces = response.body().getResults().length;
                                    Log.d(TAG, "onResponse: totPlaces" + response.body().getResults().length);

                                    Functions_Itinerary.ItineraryPlacesList = new HashMap<String, Results>();
                                    Functions_Itinerary.ItineraryPlacesList.clear();

                                    Functions_Itinerary.ItineraryPlacesResults = new ArrayList<Results>();
                                    Functions_Itinerary.ItineraryPlacesResults.clear();


                                    for (int i = 0; i < response.body().getResults().length; i++) {

                                        Functions_Itinerary.ItineraryPlacesResults.add(i, response.body().getResults()[i]);

                                        String PlaceID = response.body().getResults()[i].getPlace_id();
                                        Functions_Itinerary.ItineraryPlacesList.put(
                                                PlaceID,
                                                response.body().getResults()[i]);

                                        Log.d(TAG, "onResponse: all values " +
                                                Functions_Itinerary.ItineraryPlacesList.get(PlaceID).toString());

                                    }


                                    btn_generateTrip.setEnabled(true);

                                    //set places count in spinner
                                    numberList.clear();
                                    spinner_NoOfPlaces.setVisibility(View.VISIBLE);
                                    numberList.addAll(itineraryDetails.getNoOfPlacesList(Functions_Itinerary.TotalNoOfPlaces));
                                    NoOfPlacesAdapter.notifyDataSetChanged();
                                    alertDialog.dismiss();

                                }


                            }

                        }

                        @Override
                        public void onFailure(Call<MyPlaces> call, Throwable t) {
                            Log.d("responseFail", t.getStackTrace().toString());
                        }
                    });
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txt_ip_location:

                createLocationSearchIntent(AUTOCOMPLETE_REQUEST_CODE);

                Log.d(TAG, "onClick: " + "From Clicked");

                break;


            case R.id.btn_ip_generateTrip:

                if (validateGenerateItineraryFields()) {

                    itineraryDetails.PlacesCount = Integer.parseInt(spinner_NoOfPlaces.getSelectedItem().toString());

                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.ItineraryHome_root, new ItineraryMapFragment(), null)
                            .addToBackStack(null)
                            .commit();

                    //Toast.makeText(getActivity(), "placetype:" + spinner_placeType.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                }

                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.spinner_ip_placeType:


                Toast.makeText(getContext(), "spinner item Clicked : " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                break;

            case R.id.spinner_ip_NoOfPlaces:

                Toast.makeText(getContext(), "spinner item Clicked : " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                break;


        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        switch (parent.getId()) {

            case R.id.spinner_ip_NoOfPlaces:

                Toast.makeText(getActivity(), "No item selected no of places", Toast.LENGTH_SHORT).show();
                break;

            case R.id.spinner_ip_placeType:
                Toast.makeText(getActivity(), "No item selected place type", Toast.LENGTH_SHORT).show();
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case AUTOCOMPLETE_REQUEST_CODE:

                if (resultCode == AutocompleteActivity.RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Functions_Itinerary.itinerayLocation = place;

                    itineraryDetails.setTripLocation(place);
                    txt_tripLocation.setText(place.getName());

                    //search places function
                    //SearchItineraryPlaces("natural_feature");
                    SearchItineraryPlaces();


                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Log.i(TAG, "error " + Autocomplete.getStatusFromIntent(data).getStatusMessage());
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
