package com.example.kishanthprab.placehook.Utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.kishanthprab.placehook.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireAuthUtil {

    private static final String TAG = "Firebase Auth : ";


    static  FirebaseUser mUser;
    static FirebaseAuth mAuth;

    public static FirebaseAuth setFireAuth(){
        mAuth = FirebaseAuth.getInstance();

        return mAuth;
    }

    public static FirebaseAuth.AuthStateListener SettingFirebaseAuthStateListener(){
        Log.d(TAG,"setting up");

        mAuth = FirebaseAuth.getInstance();

        com.google.firebase.auth.FirebaseAuth.AuthStateListener FireAuthListener = new com.google.firebase.auth.FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull com.google.firebase.auth.FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if (mUser!= null) {

                    Log.d(TAG,"state changed, signed in :"+mUser.getUid());

                }
                else {

                    Log.d(TAG,"state changed, signed out");
                }


            }
        };

    return FireAuthListener;
    }


    public static FirebaseUser getmUser() {

        return mUser;
    }

    public static void setmUser(FirebaseUser mUser) {
        FireAuthUtil.mUser = mUser;
    }
}
