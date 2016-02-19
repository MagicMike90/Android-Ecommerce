package com.michael.onlinestore.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by michael on 19/02/2016.
 */
public class SensorServiceController implements SensorEventListener {
    private static String TAG = SensorServiceController.class.getName();

    private static SensorServiceController mInstance;

    private Context mContext;
    private Marker mMarker;

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

    public static SensorServiceController getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new SensorServiceController(ctx);
        }
        return mInstance;
    }

    private SensorServiceController(Context ctx) {
        mContext = ctx;

        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];

        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];
    }

    public void init(Marker marker) {
        mMarker = marker;
    }

    public void registerListener() {
        sensorManager.registerListener(this,
                sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorMagneticField,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener() {
        sensorManager.unregisterListener(this,
                sensorAccelerometer);
        sensorManager.unregisterListener(this,
                sensorMagneticField);
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
            //Log.d(TAG, "success");
            SensorManager.getOrientation(matrixR, matrixValues);
            try {
                azimuth = (float) Math.toDegrees(matrixValues[0]);
                pitch = (float) Math.toDegrees(matrixValues[1]);
                roll = (float) Math.toDegrees(matrixValues[2]);

                //mMarker.setRotation(azimuth);
               // Log.d(TAG, "azimuth: " + azimuth + " pitch: " + pitch + " roll: " + roll);
            } catch (Exception e) {
                Log.e(TAG,e.getMessage());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
