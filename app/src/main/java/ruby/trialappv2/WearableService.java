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
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * post sense write with consumer thread
 */

public class WearableService extends Service implements SensorEventListener {
    public final static String AGE_CHOICE = "Age Chosen";
    public final static String GENDER_CHOICE = "Gender Chosen";
    public final static String HEIGHT_CHOICE = "Height Chosen";
    public final static String TRIAL_CHOICE = "Trial Chosen";

    private static final String TAG = "SenseService";

    //private static Queue<String> q = new ArrayDeque<String>();
    private final IBinder mBinder = new LocalBinder();
    Sensor senAccelerometer;
    boolean stopped = false;
    // BlockingQueue<String> sharedQueue;
    ArrayList<String> arrayList;
    BroadcastReceiver receiver;
    Thread consThread;
    int written = 0;
    int added = 0;
    private SensorManager senSensorManager;
    private FileWriter writer;
    private BufferedWriter bufferedWriter;

    @Override
    public void onCreate() {

        super.onCreate();
        // sharedQueue = new LinkedBlockingQueue<>();
        arrayList = new ArrayList<>();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get accelerometer
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //  senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //register the sensor, use context, name and rate at which sensor events are delivered to us.
        senSensorManager.registerListener(this, senAccelerometer, 100000);
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

            //sharedQueue.put(event.timestamp + ";" + event.values[0] + ";" + event.values[1] + ";" + event.values[2] + "\n");
            arrayList.add(event.timestamp + ";" + event.values[0] + ";" + event.values[1] + ";" + event.values[2] + "\n");
            added = added + 1;


        }

    }


    /*Get the user selected options from the intent*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        String gender = intent.getStringExtra(GENDER_CHOICE);
        String age = intent.getStringExtra(AGE_CHOICE);
        String height = intent.getStringExtra(HEIGHT_CHOICE);
        String trial = intent.getStringExtra(TRIAL_CHOICE);
        trial = trial.replaceAll(" ", "_").toLowerCase();

        try {
            makeFile(trial, gender, age, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        Thread prodThread = new Thread(new Producer(sharedQueue, arraylist));
        prodThread.start();
        */

        Log.d(TAG, "onStartCommand Finished");

        return Service.START_NOT_STICKY;
    }

    public void stopSensing() throws IOException, InterruptedException {
        if (senSensorManager != null) {
            senSensorManager.unregisterListener(this);
        }

        consThread = new Thread(new Consumer(arrayList, bufferedWriter));
        consThread.start();
        Log.d(TAG, "start cons thread called");
        consThread.join();
        bufferedWriter.close();
        Log.d(TAG, added + " " + written);
        Log.d(TAG, "buffered writer closed");
        writer.close();
        sendBroadcast(new Intent("End"));
        stopSelf();
        Log.d(TAG, "on destroy called");
    }

    @Override
    public void onDestroy() {

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
    public void makeFile(String trial, String gender, String age, String height) throws IOException {


        writer = new FileWriter(Environment.getExternalStorageDirectory().toString() + "/" + trial + "_" + gender + "_" +
                age + "_" + height + "_accelerometer.dat");
        bufferedWriter = new BufferedWriter(writer);


    }


    public class LocalBinder extends Binder {

        public WearableService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return WearableService.this;
        }
    }

      /*
    class Producer implements Runnable {
        private final BlockingQueue sharedQueue;
        private final ArrayList<String> arraylist;
        private static final String TAG = "ProducerThread";

        public Producer(BlockingQueue sharedQueue, ArrayList arraylist) {
            this.sharedQueue = sharedQueue;
            this.arraylist = arraylist;

            Log.d(TAG, "Producer Created");
        }

        @Override
        public void run() {
            Log.d(TAG, "run");
            int i = 0;
            while(true){
                    try {
                        sharedQueue.put(arraylist.get(i));
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    */


    class Consumer implements Runnable {
        private static final String TAG = "ConsumerThread";
        private final ArrayList<String> arrayList;
        private BufferedWriter bufferedWriter;

        public Consumer(ArrayList<String> arrayList, BufferedWriter buf) {
            this.arrayList = arrayList;
            this.bufferedWriter = buf;

            Log.d(TAG, "Consumer Created");
        }

        @Override
        public void run() {
            Log.d(TAG, "run");

            for (String str : arrayList) {

                try {
                    bufferedWriter.append(str);
                    written = written + 1;
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO: quit the thread + app at this point?
                }
            }
        }
    }
}



