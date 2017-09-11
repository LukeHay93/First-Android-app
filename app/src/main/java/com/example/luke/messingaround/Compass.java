package com.example.luke.messingaround;

import android.content.Context;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import android.widget.ImageView;
import android.widget.TextView;

import android.graphics.Matrix;

public class Compass extends AppCompatActivity implements SensorEventListener{

    private ImageView image;
    TextView tvHeading;

    private float azimut, azimutDegrees, currentDegree = 0f;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer =  mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        image = (ImageView) findViewById(R.id.compassImageView);

        tvHeading = (TextView) findViewById(R.id.compassTextView);

    }

    @Override
    protected void onResume(){
        super.onResume();

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == event.sensor.TYPE_ACCELEROMETER){
            mGravity = event.values;
        }
        if(event.sensor.getType() == event.sensor.TYPE_MAGNETIC_FIELD){
            mGeomagnetic = event.values;
        }
        if(mGravity != null && mGeomagnetic != null){
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic);

            if(success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0];

                azimutDegrees = Math.round(Math.toDegrees(azimut));
                animate(azimutDegrees);

            }
        }
    }

    public void animate(float azimutDegrees){
        tvHeading .setText(Float.toString(azimutDegrees));

        RotateAnimation ra = new RotateAnimation(currentDegree, -azimutDegrees,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(10);
        ra.setFillAfter(true);

        image.startAnimation(ra);
        currentDegree = -azimutDegrees;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}