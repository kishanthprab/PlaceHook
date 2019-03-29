package com.example.kishanthprab.placehook.Remote;

import com.example.kishanthprab.placehook.DataObjects.PlaceDetailsModels.Result;
import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Results;

public class CommonGoogle {

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static Results currentResults;
    public static Result currentResult;

    public static GoogleAPIService getGoogleAPIService() {

        return RetrofitClient.getClientRetrofit(GOOGLE_API_URL).create(GoogleAPIService.class);
    }

    public static GoogleAPIService getGoogleAPIServiceScalars() {

        return RetrofitScalarsClient.getScalarClientRetrofit(GOOGLE_API_URL).create(GoogleAPIService.class);
    }

}
