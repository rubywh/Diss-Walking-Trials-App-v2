package ruby.trialappv2;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * post sense write with consumer thread
 */

public class WearableService extends Service implements SensorEventListener {
    public final static String TRIAL_CHOICE = "Trial Chosen";
    public final static String PARTICIPANT_CHOICE = "Participant Chosen";

    private static final String TAG = "SenseService";

    //private static Queue<String> q = new ArrayDeque<String>();
    private final IBinder mBinder = new LocalBinder();
    Sensor senAccelerometer;
    Sensor senGyro;
    boolean stopped = false;
    BroadcastReceiver receiver;
    ArrayList<String> accArrayList;
    ArrayList<String> gyroArrayList;
    Thread aConsThread;

    Thread gConsThread;
    private SensorManager senSensorManager;
    private FileWriter accWriter;
    private FileWriter gyroWriter;
    private BufferedWriter aBufferedWriter;
    private BufferedWriter gBufferedWriter;
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {

        super.onCreate();
        // sharedQueue = new LinkedBlockingQueue<>();
        accArrayList = new ArrayList<>();
        gyroArrayList = new ArrayList<>();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get accelerometer
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //register the sensor, use context, name and rate at which sensor events are delivered to us.
        senSensorManager.registerListener(this, senAccelerometer, 20000);
        senSensorManager.registerListener(this, senGyro, 20000);
        //senSensorManager.registerListener(this, senGyro, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(TAG, "Finished Creation");

        IntentFilter filter = new IntentFilter("ruby.trialappv2.testIntent");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Stop clicked");
                if (intent.getExtras().getInt("value") == 1) {
                    stopped = true;
                    try {
                        stopSensing();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        registerReceiver(receiver, filter);
    }


    /*When a new sensor event received, execute a new AsyncTask for accelerometer and gyro*/
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            accArrayList.add(event.timestamp + ";" + event.values[0] + ";" + event.values[1] + ";" + event.values[2] + "\n");
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            gyroArrayList.add(event.timestamp + ";" + event.values[0] + ";" + event.values[1] + ";" + event.values[2] + "\n");
        }
    }


    /*Get the user selected options from the intent*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        mWakeLock.acquire();
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        String trial = intent.getStringExtra(TRIAL_CHOICE);
        String pNumber = intent.getStringExtra(PARTICIPANT_CHOICE);
        trial = trial.replaceAll(" ", "_").toLowerCase();

        try {
            makeFile(trial, pNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "onStartCommand Finished");

        return Service.START_NOT_STICKY;
    }

    public void stopSensing() throws IOException, InterruptedException {
        if (senSensorManager != null) {
            senSensorManager.unregisterListener(this);
        }

        aConsThread = new Thread(new accelerometerConsumer(accArrayList, aBufferedWriter));
        aConsThread.start();
        Log.d(TAG, "start cons thread called");
        aConsThread.join();
        aBufferedWriter.close();
        accWriter.close();

        gConsThread = new Thread(new gyroConsumer(gyroArrayList, gBufferedWriter));
        gConsThread.start();
        gConsThread.join();
        gBufferedWriter.close();

        sendBroadcast(new Intent("End"));
        stopSelf();
        Log.d(TAG, "on destroy called");
    }

    @Override
    public void onDestroy() {
        mWakeLock.release();
        unregisterReceiver(receiver);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /* Given gender/age/height make a file in external storage and set up print streams */
    public void makeFile(String trial, String pNumber) throws IOException {

        Date now = new Date();
        String stringDate = new SimpleDateFormat("ddMMyyyyHHmm").format(now);
        accWriter = new FileWriter(Environment.getExternalStorageDirectory().toString() + "/" + pNumber + "_" + trial + "_" + stringDate + "_accelerometer.dat");

        gyroWriter = new FileWriter(Environment.getExternalStorageDirectory().toString() + "/" + pNumber + "_" + trial + "_" + stringDate + "_gyroscope.dat");

        aBufferedWriter = new BufferedWriter(accWriter);
        gBufferedWriter = new BufferedWriter(gyroWriter);


    }


    public class LocalBinder extends Binder {

        public WearableService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return WearableService.this;
        }
    }

    class accelerometerConsumer implements Runnable {
        private static final String TAG = "ConsumerThread";
        private final ArrayList<String> arrayList;
        private BufferedWriter bufferedWriter;

        public accelerometerConsumer(ArrayList<String> arrayList, BufferedWriter buf) {
            this.arrayList = arrayList;
            this.bufferedWriter = buf;

            Log.d(TAG, "Consumer Created");
        }

        @Override
        public void run() {
            Log.d(TAG, "run");

            for (String str : arrayList) {

                try {
                    Log.d(TAG, str);
                    bufferedWriter.append(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class gyroConsumer implements Runnable {
        private static final String TAG = "ConsumerThread";
        private final ArrayList<String> arrayList;
        private BufferedWriter bufferedWriter;

        public gyroConsumer(ArrayList<String> arrayList, BufferedWriter buf) {
            this.arrayList = arrayList;
            this.bufferedWriter = buf;

            Log.d(TAG, "Consumer Created");
        }

        @Override
        public void run() {
            Log.d(TAG, "run");

            for (String str : arrayList) {

                try {
                    Log.d(TAG, str);
                    bufferedWriter.append(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}



