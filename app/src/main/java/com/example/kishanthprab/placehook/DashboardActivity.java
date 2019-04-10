package com.example.kishanthprab.placehook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.DataObjects.User;
import com.example.kishanthprab.placehook.Utility.FilterDialog;
import com.example.kishanthprab.placehook.Utility.FireAuthUtil;
import com.example.kishanthprab.placehook.Utility.FireDBUtil;
import com.example.kishanthprab.placehook.fragments.DiscoverFragment;
import com.example.kishanthprab.placehook.fragments.ItineraryPlannerFragment;
import com.example.kishanthprab.placehook.fragments.MapsFragment;
import com.example.kishanthprab.placehook.fragments.NavigationMapsFragment;
import com.example.kishanthprab.placehook.fragments.NearbyMapsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private TextView toolbar_title;
    private ImageView img_filter;

    final static String TAG = "DashboardActivity";


    FirebaseAuth FireAuth;
    FirebaseAuth.AuthStateListener FireAuthStateListener;

    TextView nav_header_name, nav_header_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //sets toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        img_filter = (ImageView) toolbar.findViewById(R.id.img_filter);

        img_filter.setOnClickListener(this);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionbarToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionbarToggle);
        actionbarToggle.syncState();

        nav_header_name = (TextView) findViewById(R.id.nav_header_name);
        nav_header_email = (TextView) findViewById(R.id.nav_header_email);

        FireAuth = FirebaseAuth.getInstance();
        FireAuthStateListener = FireAuthUtil.SettingFirebaseAuthStateListener();

        FireDBUtil.getDatabaseReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue(User.class);

                nav_header_name = (TextView) findViewById(R.id.nav_header_name);
                nav_header_email = (TextView) findViewById(R.id.nav_header_email);
                nav_header_name.setText(user.getName());
                nav_header_email.setText(user.getEmail());

                Log.d(TAG, "user values " + user.getName() + user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (savedInstanceState == null) {

            //opens this fragment immediately
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DiscoverFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_Discover);
            toolbar_title.setText("Discover");
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_Discover:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DiscoverFragment()).commit();

                toolbar_title.setText("Discover");
                img_filter.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Discover", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Itinerary:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ItineraryPlannerFragment()).commit();

                toolbar_title.setText("Itinerary Planner");
                img_filter.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Itinerary Planner", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Nearby:
                toolbar_title.setText("Nearby");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NearbyMapsFragment()).commit();

                Toast.makeText(this, "Nearby", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Navigation:
                // startActivity(new Intent(DashboardActivity.this,MapsFragment.class));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NavigationMapsFragment()).commit();
                toolbar_title.setText("Navigation");
                img_filter.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Navigation", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_ARNavigation:
                toolbar_title.setText("AR Navigation");
                img_filter.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new sampleMapActivity()).commit();
                Toast.makeText(this, "AR Navigation", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Favourites:
                toolbar_title.setText("Favourites");
                img_filter.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Favourites", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                toolbar_title.setText("Settings");
                img_filter.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
                if (FireAuthUtil.getmUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                }

                break;

        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
            //System.exit(1);
            //finish();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        FireAuth.addAuthStateListener(FireAuthStateListener);

        FirebaseUser CurrentUser = FireAuthUtil.getmUser();
        if (FireAuthUtil.getmUser() == null) {
            startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
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


    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.img_filter:
                Toast.makeText(getApplicationContext(), "filter clicked", Toast.LENGTH_SHORT).show();
                final DialogFragment dialog = FilterDialog.newInstance();


                ((FilterDialog) dialog).setCallback(new FilterDialog.Callback() {
                    @Override
                    public void onActionClick(String name) {

                        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.example.kishanthprab.placehook", Context.MODE_PRIVATE);
                        String key = sp.getString("key", "123");


                        //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), "value "+key, Toast.LENGTH_SHORT).show();

                    }
                });

                dialog.show(getSupportFragmentManager(), "tag");

                break;

        }

    }


}
