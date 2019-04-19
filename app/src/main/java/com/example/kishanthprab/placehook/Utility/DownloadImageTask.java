package com.example.kishanthprab.placehook.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;

import com.example.kishanthprab.placehook.Recycler.RecyclerListItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView ImageV;

    public DownloadImageTask(ImageView ImageV) {
        this.ImageV = ImageV;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap myBitmap = null;

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(urldisplay);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            Log.d("imageTask", "getBitmapFromRedirectedURL: url"+connection.getURL());


            InputStream input = connection.getInputStream();

            //again have to get it from redirected url
            /*url = connection.getURL();
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            input = connection.getInputStream();*/
            Log.d("imageTask", "getBitmapFromRedirectedURL: redirected url" +connection.getURL());
            myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("imageTask", "doInBackground: error :"+e.getMessage());
            return null;
        }
    }

    protected void onPostExecute(Bitmap result) {
        ImageV.setImageBitmap(result);
    }
}