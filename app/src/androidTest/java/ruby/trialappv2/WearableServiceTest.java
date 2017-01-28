package ruby.trialappv2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.test.AndroidTestCase;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by ruby__000 on 26/01/2017.
 */
public class WearableServiceTest extends AndroidTestCase {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();
    WearableService service = null;
    private SensorManager mSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGyroscope;
    private SensorListener mSensorListener;
    private ArrayList<Sensor> mListSensors = new ArrayList<Sensor>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new SensorListener();
        senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        assertNotNull(senAccelerometer);
        senGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        assertNotNull(senGyroscope);
        mListSensors.add(senAccelerometer);
        mListSensors.add(senGyroscope);

    }

    public void testBoundService() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(InstrumentationRegistry.getTargetContext(), WearableService.class);

        // Data can be passed to the service via the Intent.
        serviceIntent.putExtra(WearableService.AGE_CHOICE, "10");
        serviceIntent.putExtra(WearableService.TRIAL_CHOICE, "Arms swinging");
        serviceIntent.putExtra(WearableService.HEIGHT_CHOICE, "142");
        serviceIntent.putExtra(WearableService.GENDER_CHOICE, "Male");

        IBinder binder = mServiceRule.bindService(serviceIntent);
        service = ((WearableService.LocalBinder) binder).getService();

    }

    public void testWithStartedService() throws TimeoutException {
        Intent serviceIntent =
                new Intent(InstrumentationRegistry.getTargetContext(), WearableService.class);

    }

    public void testMakeFile() throws TimeoutException {
        testBoundService();

        service.makeFile("Arms swinging", "Female", "21", "144");
        String androidpath = Environment.getExternalStorageDirectory().toString();
        String resta = (androidpath + "/" + "Arms swinging" + "_" + "Female" + "_" +
                "21" + "_" + "144" + "_accelerometer.dat");
        File file = new File(resta);
        assertNotNull(file);
        assert (file.exists());
    }


    public void testAccelerometerAndGyro() {
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        assertNotNull(sensor);
        if (getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            assertEquals(Sensor.TYPE_ACCELEROMETER, sensor.getType());
            // assertSensorValues(sensor);
        }
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        assertNotNull(sensor);
        if (getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SENSOR_GYROSCOPE)) {
            assertEquals(Sensor.TYPE_GYROSCOPE, sensor.getType());
        }
    }


    public void testRegisterAccelerometerTwice() {
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean registerAccelerometer = mSensorManager.registerListener(mSensorListener, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
        assertTrue(registerAccelerometer);
        registerAccelerometer = mSensorManager.registerListener(mSensorListener, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
        assertFalse(registerAccelerometer);

    }

    public void testAcceleration() throws InterruptedException {
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Test
    public void makeFile() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }


}