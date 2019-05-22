package com.example.kishanthprab.placehook.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kishanthprab.placehook.DB.DB_PlaceHook.DB_PlaceHookUserReviews;
import com.example.kishanthprab.placehook.DataObjects.UserReview;
import com.google.firebase.database.DataSnapshot;


public class SQLiteDB_helper extends SQLiteOpenHelper {

    //create table entry
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DB_PlaceHookUserReviews.TABLE_NAME + " (" +
                    DB_PlaceHookUserReviews._ID + " INTEGER PRIMARY KEY," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_USERID + " TEXT," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_PLACEID + " TEXT UNIQUE," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_PLACENAME + " TEXT," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_LATITUDE + " TEXT," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_LONGITUDE + " TEXT," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_AUTHORNAME + " TEXT," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_RATING + " TEXT," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_REVIEWTEXT + " TEXT," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_TIME + " TEXT," +
                    DB_PlaceHookUserReviews.COLUMN_NAME_UNIXTIME + " TEXT)";

    //delete table entry
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DB_PlaceHookUserReviews.TABLE_NAME;

    //db version
    public static final int DATABASE_VERSION = 1;
    //db name
    public static final String DATABASE_NAME = "PlaceHook.db";


    public SQLiteDB_helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void createUserReviewsTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void dropuserReviewsTable(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ContentValues ValuesForTableReviews(ContentValues values, DataSnapshot snapshot){
        values = new ContentValues();

        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_USERID, snapshot.getValue(UserReview.class).getUserId());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_PLACEID, snapshot.getValue(UserReview.class).getPlaceId());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_PLACENAME, snapshot.getValue(UserReview.class).getPlaceName());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_LATITUDE, snapshot.getValue(UserReview.class).getLatitude());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_LONGITUDE, snapshot.getValue(UserReview.class).getLongitude());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_AUTHORNAME, snapshot.getValue(UserReview.class).getAuthorName());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_RATING, snapshot.getValue(UserReview.class).getRating());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_REVIEWTEXT, snapshot.getValue(UserReview.class).getReviewText());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_TIME, snapshot.getValue(UserReview.class).getTime());
        values.put(DB_PlaceHook.DB_PlaceHookUserReviews.COLUMN_NAME_UNIXTIME, snapshot.getValue(UserReview.class).getUnixTime());

        return values;
    }

}
