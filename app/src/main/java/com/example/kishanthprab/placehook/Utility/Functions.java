package com.example.kishanthprab.placehook.Utility;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.MyPlaces;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Photos;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;
import com.example.kishanthprab.placehook.LoginActivity;
import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.RecyclerListItem;
import com.example.kishanthprab.placehook.SplashActivity;
import com.example.kishanthprab.placehook.WelcomeActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Functions {

    public static void spotDialog(){

        //AlertDialog waitingdialog = new SpotsDialog(LoginActivity.getContext());

    }


    public static Bitmap getImageData(Photos p) {
        try {

            String src= "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+
                    p.getPhoto_reference()+
                    "&key="+
                    R.string.google_maps_key;
            Log.d("srcsrc", src);



            URL url = new URL(src);
            HttpsURLConnection ucon = (HttpsURLConnection) url.openConnection();
            //ucon.setInstanceFollowRedirects(true);
            //URL secondURL = new URL(ucon.getHeaderField("Location"));
           // HttpsURLConnection connection = (HttpsURLConnection) secondURL.openConnection();
            //connection.setDoInput(true);
            //connection.connect();
            ucon.setDoInput(true);
            ucon.connect();
            InputStream input = ucon.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Bitmap exception" + e);
            return null;
        }
    }



}
