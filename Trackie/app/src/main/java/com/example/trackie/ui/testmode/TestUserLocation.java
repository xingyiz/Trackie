package com.example.trackie.ui.testmode;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// handler class which adjusts input location based on number of steps taken by user.
public class TestUserLocation implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepsTakenFromPrev;

    public TestUserLocation(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected PointF getAdjustedLocation(PointF prevLocation, PointF newLocation) {
        if (stepsTakenFromPrev < 5) {
            stepsTakenFromPrev = 0;
            return new PointF( (prevLocation.x + newLocation.x) / 2,
                               (prevLocation.y + newLocation.y) / 2);
        } else {
            stepsTakenFromPrev = 0;
            return newLocation;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepsTakenFromPrev += event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
