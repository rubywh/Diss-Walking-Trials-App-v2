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
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Writes data to file in background
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
    Sensor senGyro;
    Long startTime;
    Thread writerThread;
    FileWriter fw;
    BufferedWriter bw;
    boolean stopped = false;
    BlockingQueue<String> sharedQueue;
    private SensorManager senSensorManager;
    private PrintStream ps;
    private PrintStream ps_gyro;
    private String androidpath;
    private String thisTimestamp;
    private String retrievedTimestamp;
    private int written = 0;
    private int read = 0;

    @Override
    public void onCreate() {

        super.onCreate();
        sharedQueue = new ArrayBlockingQueue(1000);


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get accelerometer
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //  senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //register the sensor, use context, name and rate at which sensor events are delivered to us.
        senSensorManager.registerListener(this, senAccelerometer, 5000);
        //senSensorManager.registerListener(this, senGyro, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(TAG, "Finished Creation");


        IntentFilter filter = new IntentFilter("ruby.trialappv2.testIntent");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int value = intent.getExtras().getInt("value");
                if (value == 1) {
                    stopped = true;
                    try {
                        finishWriting();
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

            thisTimestamp = String.valueOf(event.timestamp);
            String thisEvent = (thisTimestamp + ";" + event.values[0] + ";" + event.values[1] + ";" + event.values[2]);
            try {
                sharedQueue.put(thisEvent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            System.out.println(sharedQueue);  //NOTE: system.out.println is synchronous

            read = read + 1;  // TODO: concurrecy issue here if there are multiple threads appending to read?
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //  new GyroEventLoggerTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, event);
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

        makeFile(trial, gender, age, height);

        // TODO: you can simply use a lambda function for this, or a pointer to a regular function :)
        // TODO: stop this thread when you've finished writing

/*
        Thread prodThread = new Thread(new Producer(sharedQueue, arraylist));
        prodThread.start();
        */

        Thread consThread = new Thread(new Consumer(sharedQueue, bw));
        consThread.start();
        //new AccelerometerEventLoggerTask().executeOnExecutor(SERIAL_EXECUTOR);

        Log.d(TAG, "onStartCommand Finished");

        return Service.START_NOT_STICKY;
    }

    /*
        public void writerMethod(){
            Log.d(TAG, "Thread running");
              int j;
           for (j=0;j<1000;j++){
                Log.d(TAG, "qsize" + String.valueOf(q.size()));
                try {
                    bw.write(q.poll());
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    */
    public void finishWriting() throws IOException, InterruptedException {
        if (senSensorManager != null) {
            senSensorManager.unregisterListener(this);
        }

               /*
        bw.flush();
        bw.close();
        fw.close();
        Log.d(TAG, "written");
        */
    }


    @Override
    public void onDestroy() {
        if (senSensorManager != null) {
            senSensorManager.unregisterListener(this);
        }

        Log.d(TAG, "onDestroy");
        super.onDestroy();
        try {
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /* Given gender/age/height make a file in external storage and set up print streams */
    public void makeFile(String trial, String gender, String age, String height) {

        androidpath = Environment.getExternalStorageDirectory().toString();

        try {
            String resta = (androidpath + "/" + trial + "_" + gender + "_" +
                    age + "_" + height + "_accelerometer.dat");
            System.out.println(resta);
            Log.d(TAG, "FileMade");
            String restb = (androidpath + "/" + trial + "_" + gender + "_" +
                    age + "_" + height + "_gyroscope.dat");
            fw = new FileWriter(resta);
            bw = new BufferedWriter(fw);
            ps = new PrintStream(new FileOutputStream(resta));
            ps_gyro = new PrintStream(new FileOutputStream(restb));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class LocalBinder extends Binder {

        public WearableService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return WearableService.this;
        }
    }

    private class AccelerometerEventLoggerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

           /* ps.println(arrayList.get(arrayList.size() - 1));

            /*SensorEvent event = events[0];
            Long timestamp = System.currentTimeMillis() + ((event.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000L);

            //Set to 20dp for now
            String lin = Long.toString(event.timestamp) + ";" +
                    String.format("%.20f", event.values[0]) + ";" +
                    String.format("%.20f", event.values[1]) + ";" +
                    String.format("%.20f", event.values[2]);
            ps.println(line);
            return null;
            */
            int k;


            /*

            while(!q.isEmpty()){
                Log.d(TAG, "qsize" + String.valueOf(q.size()));
                try {
                    bw.write(q.poll());
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
            return null;
        }
    }


    // Write a line in the gyro file
    private class GyroEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
            //Getting the event and values
            SensorEvent event = events[0];
            Long timestamp = System.currentTimeMillis() + ((event.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000L);
            Long timeElapsed = timestamp - startTime;
            //Set to 20dp for now
            String line = Long.toString(event.timestamp) + ";" +
                    String.format(Locale.UK, "%.20f", event.values[0]) + ";" +
                    String.format(Locale.UK, "%.20f", event.values[1]) + ";" +
                    String.format(Locale.UK, "%.20f", event.values[2]);
            ps_gyro.println(line);
            return null;
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
        private final BlockingQueue sharedQueue;
        private BufferedWriter bufferedWriter;

        public Consumer(BlockingQueue sharedQueue, BufferedWriter buf) {
            this.sharedQueue = sharedQueue;
            this.bufferedWriter = buf;

            Log.d(TAG, "Consumer Created");
        }

        @Override
        public void run() {
            Log.d(TAG, "run");
            while (true) {
                try {
                    bufferedWriter.write(sharedQueue.take().toString());
                    bufferedWriter.newLine();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    // TODO: quit the thread + app at this point?
                }
            }
        }
    }
}

