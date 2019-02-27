package com.example.kishanthprab.placehook.Remote;

public class CommonGoogle {

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";


    public static GoogleAPIService getGoogleAPIService() {

        return RetrofitClient.getClientRetrofit(GOOGLE_API_URL).create(GoogleAPIService.class);
    }


}
