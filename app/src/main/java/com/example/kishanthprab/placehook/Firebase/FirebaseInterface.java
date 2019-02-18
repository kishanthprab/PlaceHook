package com.example.kishanthprab.placehook.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public interface FirebaseInterface {

    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();

    public DatabaseReference getDB();
}
