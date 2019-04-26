package com.example.kishanthprab.placehook.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.input.InputManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Photos;
import com.example.kishanthprab.placehook.LoginActivity;
import com.example.kishanthprab.placehook.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import dmax.dialog.SpotsDialog;

public class Functions {

    private final static String TAG = "Utility Functions";

    private static Context context;

    public static AlertDialog spotsDialog(Context context) {

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(context)
                .build();

        return alertDialog;

    }


    public static Bitmap getBitmapFromURL(String src) {

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitmapFromRedirectedURL(String src) {

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            Log.d(TAG, "getBitmapFromRedirectedURL: url"+connection.getURL());


            InputStream input = connection.getInputStream();

            //again have to get it from redirected url
            /*url = connection.getURL();
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            input = connection.getInputStream();*/
            Log.d(TAG, "getBitmapFromRedirectedURL: redirected url" +connection.getURL());
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    public static String toJSON(Object object) {


        String jsonStr = "";
        // Creating Object of ObjectMapper define in Jakson Api
        ObjectMapper Obj = new ObjectMapper();

        try {

            // get Oraganisation object as a json string
            jsonStr = Obj.writeValueAsString(object);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;

    }

    public static String geolocatePlace(LatLng latLng,Activity context) {
        Log.d(TAG, "geolocatePlace: geolocation");

        //String searchString = navigation_searchCurrent.getText().toString();
        String address = "";

        Geocoder geocoder = new Geocoder(context);
        List<Address> ListAddresses = new ArrayList<>();
        try {

            //  ListAddresses = geocoder.getFromLocationName(searchString, 1);
            ListAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        } catch (Exception e) {

            Log.d(TAG, "geolocatePlace: Exception" + e.getMessage());

        }

        if (ListAddresses.size() > 0) {


            address = ListAddresses.get(0).getAddressLine(0);
            //navigation_searchCurrent.setText(t);
            Toast.makeText(context, "address : " + address, Toast.LENGTH_SHORT).show();

            Log.d(TAG, "geolocatePlace: Found location : " + ListAddresses.get(0).toString());
        }

        return address;

    }


}
