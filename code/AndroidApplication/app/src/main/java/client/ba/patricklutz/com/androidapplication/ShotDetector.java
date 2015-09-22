package client.ba.patricklutz.com.androidapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * SensorEventListener to detect Shots
 * Created by privat-patrick on 14.05.2015.
 */
public class ShotDetector implements SensorEventListener {

    private static final int SHAKE_THRESHOLD = 2500;

    long lastUpdate = 0;
    double x = 0, y = 0, z = 0;
    double last_x = 0, last_y = 0, last_z = 0;

    CommandManager callback;

    public ShotDetector(Context context, CommandManager callback) {
        super();
        SensorManager sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        this.callback = callback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long difftime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                double speed = Math.abs(x+y+z - last_x - last_y - last_z) / difftime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Log.d("Sensor", "shake detected speed: " + speed);
                    callback.setShot(true);

                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
