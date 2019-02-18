package com.example.kishanthprab.placehook.Utility;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.kishanthprab.placehook.DataObjects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireDBUtil {
    final static String TAG= "Firebase Utility";

    private static FirebaseDatabase FireDb;
    private static DatabaseReference Ref;


    public static FirebaseDatabase setFirebaseDatabase() {
        FireDb = FirebaseDatabase.getInstance();
        return FireDb;
    }

    public static DatabaseReference getRootDatabaseReference() {
        Ref = setFirebaseDatabase().getReference();
        return Ref;
    }

    public static DatabaseReference getDatabaseReference(String refName) {
        Ref = setFirebaseDatabase().getReference(refName);
        return Ref;
    }



}
