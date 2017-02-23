package ruby.trialappv2;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * An Activity that takes the user selected options
 * and handles starting and stopping of the sensing service.
 */

public class SenseActivity extends WearableActivity {

    public final static String AGE_CHOICE = "Age Chosen";
    public final static String GENDER_CHOICE = "Gender Chosen";
    public final static String HEIGHT_CHOICE = "Height Chosen";
    public final static String TRIAL_CHOICE = "Trial Chosen";
    private static final String TAG = "WearableActivity";
    private BoxInsetLayout mContainerView;
    private Button mBtnView;
    private Button mBtnView2;
    private Button mBtnView3;
    private final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBtnView3.setVisibility(View.VISIBLE);
        }
    };
    private String gender;
    private String age;
    private String height;
    private String trial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sense_layout);
        setAmbientEnabled();


        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mBtnView = (Button) findViewById(R.id.btn);
        mBtnView2 = (Button) findViewById(R.id.btn2);
        mBtnView3 = (Button) findViewById(R.id.btn3);
        mBtnView3.setVisibility(View.INVISIBLE);
        mBtnView2.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        gender = intent.getStringExtra(HeightListActivity.GENDER_CHOICE);
        age = intent.getStringExtra(HeightListActivity.AGE_CHOICE);
        height = intent.getStringExtra(HeightListActivity.HEIGHT_CHOICE);
        trial = intent.getStringExtra(HeightListActivity.TRIAL_CHOICE);

        System.out.println(gender + age + height);

        registerReceiver(br, new IntentFilter("End"));

    }


    /* When start clicked, start the sensing service unless it is already running*/
    public void onStartClick(View view) {

        mBtnView2.setVisibility(View.VISIBLE);
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("ruby.trialappv2.WearableService"
                    .equals(service.service.getClassName())) {
                Log.i(TAG, "Service already running!");
            }
        }
        //Send the intent with the user chosen values
        Intent toservice = new Intent(this, WearableService.class);
        toservice.putExtra(GENDER_CHOICE, gender);
        toservice.putExtra(AGE_CHOICE, age);
        toservice.putExtra(HEIGHT_CHOICE, height);
        toservice.putExtra(TRIAL_CHOICE, trial);
        this.startService(toservice);
        //Make Start button invisible
        mBtnView.setVisibility(View.INVISIBLE);
    }

    /*On click of stop button stop the running service and make the button invisible*/
    public void onStopClick(View view) {
        Intent intent = new Intent("ruby.trialappv2.testIntent");
        intent.putExtra("value", 1);
        sendBroadcast(intent);

        mBtnView2.setVisibility(View.INVISIBLE);
    }

    //End the application
    public void onFinishClick(View view) {
        this.finishAffinity();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(br);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

}

