package com.michael.onlinestore.fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.michael.onlinestore.MapsActivity;
import com.michael.onlinestore.R;
import com.michael.onlinestore.utils.ImagePostProcessor;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends TouchableSupportMapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener, TouchableWrapper.TouchActionDown, TouchableWrapper.TouchActionUp {

    public static String TAG = MapsActivity.class.getName();

    private Context mContext;


    private GoogleMap mMap;
    private String provider;
    private GoogleApiClient mGoogleApiClient;


    private float mZoomLevel = 17; //This goes up to 21
    private Location mLastLocation;
    private Marker mMarker;
    private Circle mCircle;


    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;


    SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;

    private float azimuth = 0;
    private float pitch = 0;
    private float roll = 0;


    private ImageView imgMyLocation;


    public MapFragment() {
    }


    public static MapFragment newInstance(Context ctx) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext =  getActivity();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];

        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_maps, container, false);

        imgMyLocation = (ImageView) view.findViewById(R.id.imgMyLocation);
        imgMyLocation.setImageBitmap(ImagePostProcessor.getInstance(mContext).glowProcess(R.drawable.ic_room_black_24dp));
        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();
            }
        });


        return view;
    }

    private void getMyLocation() {
        double currentLat = mLastLocation.getLatitude();
        double currentLng = mLastLocation.getLongitude();

        LatLng latLng = new LatLng(currentLat, currentLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
        mMap.animateCamera(cameraUpdate);
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
        Log.d(TAG, "onMapReady");
    }

    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            // mLocationRequest.setExpirationDuration(500);
            //  mLocationRequest.setNumUpdates(1);
            mLocationRequest.setInterval(5000);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public void onStart() {
        Log.d(TAG, "onStart");
        mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    public void onResume() {
        sensorManager.registerListener(this,
                sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorMagneticField,
                SensorManager.SENSOR_DELAY_NORMAL);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {


        sensorManager.unregisterListener(this,
                sensorAccelerometer);
        sensorManager.unregisterListener(this,
                sensorMagneticField);


        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
        super.onPause();
    }


    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            if (mRequestingLocationUpdates) {
                startLocationUpdates();
            }

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            double currentLat = 0;
            double currentLng = 0;
            if (mLastLocation != null) {
                currentLat = mLastLocation.getLatitude();
                currentLng = mLastLocation.getLongitude();
            }


            LatLng center = new LatLng(currentLat, currentLng);
            if (mMarker == null) {
                MarkerOptions markerOptions = new MarkerOptions().position(center).anchor(0.5f, 0.5f).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_black_24dp)).title("I am here!");
                mMarker = mMap.addMarker(markerOptions);
            }

            if (mCircle == null) {
                CircleOptions circleOptions = new CircleOptions()
                        .center(center)
                        .radius(10).fillColor(0x5500ff00).strokeWidth(0);
                mCircle = mMap.addCircle(circleOptions);
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(center));


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(center)      // Sets the center of the map to Mountain View
                    .zoom(mZoomLevel)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "FAILED: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        double currentLat = location.getLatitude();
        double currentLng = location.getLongitude();

        LatLng center = new LatLng(currentLat, currentLng);
        mMarker.setPosition(center);
        mCircle.setCenter(center);

        mLastLocation = location;

        Log.d(TAG, "onLocationChanged");
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for (int i = 0; i < 3; i++) {
                    valuesAccelerometer[i] = event.values[i];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for (int i = 0; i < 3; i++) {
                    valuesMagneticField[i] = event.values[i];
                }
                break;
            case Sensor.TYPE_ORIENTATION:
                break;
        }

        boolean success = SensorManager.getRotationMatrix(
                matrixR,
                matrixI,
                valuesAccelerometer,
                valuesMagneticField);

        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues);
            try {
                azimuth = (float) Math.toDegrees(matrixValues[0]);
                pitch = (float) Math.toDegrees(matrixValues[1]);
                roll = (float) Math.toDegrees(matrixValues[2]);

                mMarker.setRotation(azimuth);
                // Log.d(TAG, "azimuth: " + azimuth + " pitch: " + pitch + " roll: " + roll);
            } catch (Exception e) {

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onTouchDown(MotionEvent event) {
        Log.d(TAG, "onTouchDown");
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        Log.d(TAG, "onTouchUp");
    }
}
