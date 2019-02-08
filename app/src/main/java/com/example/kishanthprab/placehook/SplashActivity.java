package com.example.kishanthprab.placehook;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;
import com.eftimoff.androipathview.*;



public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        TextView splashtxt = (TextView) findViewById(R.id.textView);
        splashtxt.setAlpha(0f);
        splashtxt.animate().alpha(1f).setDuration(3500).setStartDelay(1500);


        ImageView splashimgView = (ImageView)findViewById(R.id.splashImgv);
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
        Intent i = new Intent(SplashActivity.this,WelcomeActivity.class);
        startActivity(i);
    }
},4500);


        // If token is null -> LoginActivity, else MainActivity
/*
        new Handler().postDelayed(() -> {
            Intent i = new Intent(this,WelcomeActivity.class);
            startActivity(i);
            finish();
        }, 2000);

*/
    }
}
