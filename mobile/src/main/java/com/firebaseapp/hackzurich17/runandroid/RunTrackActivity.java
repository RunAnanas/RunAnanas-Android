package com.firebaseapp.hackzurich17.runandroid;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RunTrackActivity extends Activity implements SensorEventListener {

    private static final String FIREBASE_URI = "https://hackzurich17.firebaseio.com/";

    private TextView stepsCount;
    private SensorManager sensorManager;
    private DatabaseReference stepsDB;
    private String userName = "Jonny";
    private int startCount = 0;
    private int lastReport = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init Layout
        setContentView(R.layout.activity_run_track);
        stepsCount = (TextView) findViewById(R.id.step_count);
        Button stepsResetButton = (Button) findViewById(R.id.reset_steps);
        stepsResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSteps();
            }
        });

        // Steps Sensor
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Firebase
        stepsDB = FirebaseDatabase.getInstance().getReference();
//        setUserName();
    }

//    private void setUserName() {
//        Get permission and set username
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            updateSteps(Math.round(event.values[0]));
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void resetSteps() {
        startCount = lastReport;
        setTripSteps(0);
    }

    private void updateSteps(int steps) {
        if (steps != lastReport) {
            lastReport = steps;
            int tripSteps = steps - startCount;
            setTripSteps(tripSteps);
        }
    }

    private void setTripSteps(int tripSteps) {
        stepsDB.child("users").child(userName).child("steps").setValue(tripSteps);
        stepsCount.setText(Integer.toString(tripSteps));
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}