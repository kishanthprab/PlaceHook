package com.example.kishanthprab.placehook.DB;

import android.provider.BaseColumns;

public class DB_PlaceHook {

    private DB_PlaceHook() {}

    /* Inner class that defines the table contents */
    public static class DB_PlaceHookUserReviews implements BaseColumns {

        public static final String TABLE_NAME = "userReviews";
        public static final String COLUMN_NAME_USERID = "userID";
        public static final String COLUMN_NAME_PLACEID = "placeID";
        public static final String COLUMN_NAME_PLACENAME = "placeName";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_AUTHORNAME = "authorName";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_REVIEWTEXT = "reviewText";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_UNIXTIME = "unixTime";

    }

}
