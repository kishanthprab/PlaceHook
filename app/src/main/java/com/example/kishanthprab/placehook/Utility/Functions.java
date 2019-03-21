package com.example.kishanthprab.placehook.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Photos;
import com.example.kishanthprab.placehook.LoginActivity;
import com.example.kishanthprab.placehook.R;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import dmax.dialog.SpotsDialog;

public class Functions {

    private static Context context;

    public static AlertDialog spotsDialog(Context context){

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(context)
                .build();

        return alertDialog;

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
