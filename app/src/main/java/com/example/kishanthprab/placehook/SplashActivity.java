package com.example.kishanthprab.placehook;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;
import com.eftimoff.androipathview.*;
import com.example.kishanthprab.placehook.Utility.FireAuthUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity {

    FirebaseAuth FireAuth;
    FirebaseAuth.AuthStateListener FireAuthStateListener;

    private static final String TAG= "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //getSupportActionBar().hide();
        try {
            FireAuth = FirebaseAuth.getInstance();
            FireAuthStateListener = FireAuthUtil.SettingFirebaseAuthStateListener();
        }catch (Exception e){
            Log.d(TAG, "onCreate: error "+ e.getMessage());
        }




        TextView splashtxt = (TextView) findViewById(R.id.textView);
        splashtxt.setAlpha(0f);
        splashtxt.animate().alpha(1f).setDuration(3500).setStartDelay(1500);

        TextView splashtxt1 = (TextView) findViewById(R.id.textView1);
        splashtxt1.setAlpha(0f);
        splashtxt1.animate().alpha(1f).setDuration(3500).setStartDelay(1000);


        ImageView splashimgView = (ImageView) findViewById(R.id.splashImgv);
        splashimgView.animate().alpha(0f).setDuration(2000).setStartDelay(1000);


        final PathView pathView = findViewById(R.id.pathview);
        pathView.setScaleX(1.5f);
        pathView.setScaleY(1.5f);
        pathView.animate().scaleX(1f).scaleY(1f).setDuration(2000).setStartDelay(500);
        pathView.getPathAnimator()
                .delay(1000)
                .duration(3000)
                .interpolator(new AccelerateDecelerateInterpolator())
                .start();

        pathView.useNaturalColors();
        pathView.setFillAfter(true);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FireAuthUtil.getmUser() != null) {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();

                }else {


                    Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivity(i);
                }
            }
        }, 4500);


        // If token is null -> LoginActivity, else MainActivity
/*
        new Handler().postDelayed(() -> {
            Intent i = new Intent(this,WelcomeActivity.class);
            startActivity(i);
            finish();
        }, 2000);

*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        FireAuth.addAuthStateListener(FireAuthStateListener);

        FirebaseUser CurrentUser = FireAuthUtil.getmUser();
        if (FireAuthUtil.getmUser() != null) {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            //finish();

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (FireAuthStateListener != null) {
            FireAuth.removeAuthStateListener(FireAuthStateListener);
        }
    }

}
