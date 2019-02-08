package com.example.kishanthprab.placehook;

import android.media.Image;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();

        TextView txtV = (TextView)findViewById(R.id.txtV);
        ImageView blueback = (ImageView)findViewById(R.id.blueback);
        ImageView logo = (ImageView)findViewById(R.id.logo);
        MaterialButton signin = (MaterialButton)findViewById(R.id.signIn);
        MaterialButton signup = (MaterialButton)findViewById(R.id.signup);

        //blueback.setTranslationY(-500f);
        //blueback.animate().translationYBy(300f).setDuration(1000);

        txtV.setTranslationY(-500f);
        txtV.animate().translationYBy(500f).setDuration(1000);

        logo.setTranslationY(1000f);
        logo.animate().translationYBy(-1000f).setDuration(1000).setStartDelay(100);

        signin.setTranslationX(-500);
        signin.animate().translationXBy(500).setDuration(500).setStartDelay(800);

        signup.setTranslationX(500);
        signup.animate().translationXBy(-500).setDuration(500).setStartDelay(800);

    }
}
