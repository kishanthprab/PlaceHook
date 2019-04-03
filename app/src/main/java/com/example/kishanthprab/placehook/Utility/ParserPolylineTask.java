package com.example.kishanthprab.placehook.Utility;

import android.os.AsyncTask;
import android.util.Log;

import com.example.kishanthprab.placehook.Helper.DirectionsJSONParser;
import com.example.kishanthprab.placehook.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserPolylineTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    public static GoogleMap mMap;
    public static Polyline polyline;
    private static final String TAG = "ParserPolylineTask";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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

            polylineOptions.width(15);
            polylineOptions.color(R.color.colorAccent);
            polylineOptions.geodesic(true);


        }
        polyline = mMap.addPolyline(polylineOptions);

    }


}
