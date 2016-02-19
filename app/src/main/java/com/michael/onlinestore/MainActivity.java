package com.michael.onlinestore;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.michael.onlinestore.fragment.DashboardFragment;
import com.michael.onlinestore.fragment.TouchableSupportMapFragment;
import com.michael.onlinestore.fragment.TouchableWrapper;
import com.michael.onlinestore.fragment.UploadProductFragment;
import com.michael.onlinestore.utils.CustomTypefaceSpan;
import com.michael.onlinestore.utils.GoogleClientController;
import com.michael.onlinestore.utils.ImagePostProcessor;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UploadProductFragment.OnFragmentInteractionListener,
        OnMapReadyCallback,
        TouchableWrapper.TouchActionDown, TouchableWrapper.TouchActionUp,
        TouchableWrapper.TouchActionScroll {

    public static String TAG = MainActivity.class.getName();

    private ActionBarDrawerToggle mDrawerToggle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;


    private GoogleMap mMap;
    private GoogleClientController mGoogleClientCtr;
    //private SensorServiceController mSensorCtr;

    private String provider;


    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;


    private ImageView imgMyLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


//        //Toast.makeText(getApplicationContext(), "Inbox Selected", Toast.LENGTH_SHORT).show();
        TouchableSupportMapFragment fragment = new TouchableSupportMapFragment();
        fragment.getMapAsync(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        TextView toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/zorque.ttf");
        toolbarTitle.setTypeface(face);


//
//        Menu m = navigationView.getMenu();
//        for (int i=0;i<m.size();i++) {
//            MenuItem mi = m.getItem(i);
//
//            //for aapplying a font to subMenu ...
//            SubMenu subMenu = mi.getSubMenu();
//            if (subMenu!=null && subMenu.size() >0 ) {
//                for (int j=0; j <subMenu.size();j++) {
//                    MenuItem subMenuItem = subMenu.getItem(j);
//                    applyFontToMenuItem(subMenuItem);
//                }
//            }
//
//            //the method we have create in activity
//            applyFontToMenuItem(mi);
//        }




        Log.d(TAG, "onCreate");
        //intialise the map
        initMap();
    }

    private void initMap() {
        mGoogleClientCtr = GoogleClientController.getInstance(this);
        //mSensorCtr = SensorServiceController.getInstance(this);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequestHighAccuracy)
//                .addLocationRequest(mLocationRequestBalancedPowerAccuracy);
//        builder.setNeedBle(true);

//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(mGoogleClientCtr.getGoogleApiClient(), builder.build());

        imgMyLocation = (ImageView) findViewById(R.id.imgMyLocation);
        imgMyLocation.setImageBitmap(ImagePostProcessor.getInstance(this).glowProcess(R.drawable.ic_room_black_24dp));
        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleClientCtr.getMyLocation();
            }
        });
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/zorque.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        UploadProductFragment fragment = (UploadProductFragment) getSupportFragmentManager().findFragmentByTag("notification");
        getSupportActionBar().show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            DashboardFragment fragment = new DashboardFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_notification) {
            //Toast.makeText(getApplicationContext(), "Inbox Selected", Toast.LENGTH_SHORT).show();
            UploadProductFragment fragment = new UploadProductFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            fragmentTransaction.add(R.id.map, fragment, "notification");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            getSupportActionBar().hide();

        } else if (id == R.id.nav_chat) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        mGoogleClientCtr.connect();
        super.onStart();


    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        mGoogleClientCtr.disconnect();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGoogleClientCtr.init(mMap);
        //mSensorCtr.init(mGoogleClientCtr.getOwnMarker());
        Log.d(TAG, "onMapReady");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleClientCtr.reset();
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        Log.d(TAG, "onTouchDown");
        mAppBarLayout.animate().translationY(-mToolbar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(180).start();
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        Log.d(TAG, "onTouchUp");
        mAppBarLayout.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(180).start();
    }

    @Override
    public void onTouchScroll(MotionEvent event) {
        Log.d(TAG, "onTouchScroll");

    }
}
