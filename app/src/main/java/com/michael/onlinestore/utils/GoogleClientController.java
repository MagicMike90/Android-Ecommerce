package com.michael.onlinestore.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by michael on 18/02/2016.
 */
public class GoogleClientController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public static String TAG = GoogleClientController.class.getName();

    private static GoogleClientController mInstance;
    private Context mContext;


    private GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;


    private GoogleMap mMap;
    private float mZoomLevel = 17; //This goes up to 21
    private Location mLastLocation;
    private Marker mMarker;
    private Circle mCircle;

    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;

    private SensorServiceController mSensorCtr;
    private LocationServiceController mLocationCtr;


    public static GoogleClientController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GoogleClientController(context);
        }
        return mInstance;
    }

    GoogleClientController(Context ctx) {
        mContext = ctx;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).addApi(AppIndex.API)
                    .build();
        }

        //get controller
        mSensorCtr = SensorServiceController.getInstance(mContext);
        mLocationCtr = LocationServiceController.getInstance(mContext,mGoogleApiClient);
    }


    public void init(GoogleMap map) {
        mMap = map;
    }

    public void connect() {
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AdminPanel Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.michael.onlinestore/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);

        mGoogleApiClient.connect();
        mSensorCtr.registerListener();
        mLocationCtr.requestLocationUpdate();
    }

    public void disconnect() {
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AdminPanel Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.michael.onlinestore/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
        mSensorCtr.registerListener();
        mLocationCtr.removeLocationUpdate();
    }

    public GoogleApiClient getGoogleApiClient(){
        return  mGoogleApiClient;
    }


    public void getMyLocation() {
        mLocationCtr.getMyLocation();
    }

    public void reset() {
        mInstance = null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //initialize controller
            mSensorCtr.init(mMarker);
            mLocationCtr.init(mMap, mMarker, mCircle, mZoomLevel);

            //start update location
            mLocationCtr.startLocationUpdates();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "FAILED: " + connectionResult.getErrorMessage());
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult((Activity) mContext, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            //showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

}
