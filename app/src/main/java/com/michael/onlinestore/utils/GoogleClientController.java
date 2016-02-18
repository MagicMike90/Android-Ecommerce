package com.michael.onlinestore.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by michael on 18/02/2016.
 */
public class GoogleClientController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static String TAG = GoogleClientController.class.getName();

    private static GoogleClientController mInstance;
    private Context mContext;


    private GoogleApiClient mGoogleApiClient;


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
    }
    public void connect(){
        mGoogleApiClient.connect();
    }
    public void disconnect(){
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "FAILED: " + connectionResult.getErrorMessage());
    }
}
