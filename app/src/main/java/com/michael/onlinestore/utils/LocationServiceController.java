package com.michael.onlinestore.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.michael.onlinestore.R;

/**
 * Created by michael on 19/02/2016.
 */
public class LocationServiceController implements LocationListener {
    private static String TAG = LocationServiceController.class.getName();

    private static LocationServiceController mInstance;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;

    private Location mLocation;
    private GoogleMap mMap;
    private Marker mMarker;
    private Circle mCircle;

    private float mZoomLevel;


    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;

    public static LocationServiceController getInstance(Context ctx, GoogleApiClient googleApiClient) {
        if (mInstance == null) {
            mInstance = new LocationServiceController(ctx, googleApiClient);
        }
        return mInstance;
    }

    private LocationServiceController(Context ctx, GoogleApiClient googleApiClient) {
        mContext = ctx;
        mGoogleApiClient = googleApiClient;
    }

    public void init(GoogleMap map, Marker marker, Circle circle, float zoomLevel) {
        mMap = map;
        mMarker = marker;
        mCircle = circle;
        mZoomLevel = zoomLevel;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
        double currentLat = 0;
        double currentLng = 0;
        if (mLocation != null) {
            currentLat = mLocation.getLatitude();
            currentLng = mLocation.getLongitude();
        }


        LatLng center = new LatLng(currentLat, currentLng);

        if (mMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().position(center).anchor(0.5f, 0.5f).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_black_24dp)).title("I am here!");
            mMarker = mMap.addMarker(markerOptions);


        } else {
            mMarker.setPosition(center);
        }


        if (mCircle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(center)
                    .radius(10).fillColor(0x5500ff00).strokeWidth(0);
            mCircle = mMap.addCircle(circleOptions);
        } else {
            mCircle.setCenter(center);
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(center)      // Sets the center of the map to Mountain View
                .zoom(mZoomLevel)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mLocationRequest == null) {
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setFastestInterval(1000);
                mLocationRequest.setInterval(5 * 1000);
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public void requestLocationUpdate() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }
    }

    public void removeLocationUpdate() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    public void getMyLocation() {
        if (mLocation != null) {
            double currentLat = mLocation.getLatitude();
            double currentLng = mLocation.getLongitude();

            LatLng latLng = new LatLng(currentLat, currentLng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double currentLat = location.getLatitude();
        double currentLng = location.getLongitude();

        LatLng center = new LatLng(currentLat, currentLng);
        mMarker.setPosition(center);
        mCircle.setCenter(center);

        mLocation = location;
    }

}

