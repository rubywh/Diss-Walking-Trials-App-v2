package ruby.trialappv2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by ruby__000 on 22/12/2016.
 */

public class WearableService extends Service implements SensorEventListener {
    public final static String AGE_CHOICE = "Age Chosen";
    public final static String GENDER_CHOICE = "Gender Chosen";
    public final static String HEIGHT_CHOICE = "Height Chosen";
    private static final String TAG = "SenseService";
    Sensor senAccelerometer;
    Sensor senGyro;
    private SensorManager senSensorManager;
    private PrintStream ps;
    private PrintStream ps_gyro;
    private String androidpath;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "startSensorService");

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get accelerometer
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //register the sensor, use context, name and rate at which sensor events are delivered to us.
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senGyro, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /*When a new sensor event received, execute a new AsyncTask for accelerometer and gyro*/
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            new AccelerometerEventLoggerTask().execute(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            new GyroEventLoggerTask().execute(event);
        }
    }

    /*Get the user selected options from the intent*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        String gender = intent.getStringExtra(GENDER_CHOICE);
        String age = intent.getStringExtra(AGE_CHOICE);
        String height = intent.getStringExtra(HEIGHT_CHOICE);
        makeFile(gender, age, height);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (senSensorManager != null) {
            senSensorManager.unregisterListener(this);
        }

        ps.flush();
        ps_gyro.flush();
        ps.close();
        ps_gyro.close();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* Given gender/age/height make a file in external storage and set up print streams */
    public void makeFile(String gender, String age, String height) {
        androidpath = Environment.getExternalStorageDirectory().toString();

        try {
            String resta = (androidpath + "/" + gender + "_" +
                    age + "_" + height + "_accelerometer.dat");
            System.out.println(resta);
            String restb = (androidpath + "/" + gender + "_" +
                    age + "_" + height + "_gyroscope.dat");
            ps = new PrintStream(new FileOutputStream(resta));
            System.out.println();
            ps_gyro = new PrintStream(new FileOutputStream(restb));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* Write a line in the accelerometer file */
    private class AccelerometerEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {

        @Override
        protected Void doInBackground(SensorEvent... events) {
            SensorEvent event = events[0];
            //Set to 20dp for now
            String line = Long.toString(event.timestamp) + ";" +
                    String.format("%.20f", event.values[0]) + ";" +
                    String.format("%.20f", event.values[1]) + ";" +
                    String.format("%.20f", event.values[2]);
            ps.println(line);
            return null;
        }
    }

    /* Write a line in the gyro file */
    private class GyroEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
            //Getting the event and values
            SensorEvent event = events[0];
            String line = Long.toString(event.timestamp) + ";" +
                    String.format("%.20f", event.values[0]) + ";" +
                    String.format("%.20f", event.values[1]) + ";" +
                    String.format("%.20f", event.values[2]);
            ps_gyro.println(line);
            return null;
        }
    }
}
