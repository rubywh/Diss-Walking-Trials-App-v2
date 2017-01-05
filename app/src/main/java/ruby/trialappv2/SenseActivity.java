package ruby.trialappv2;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by ruby__000 on 23/12/2016.
 */

public class SenseActivity extends WearableActivity {

    public final static String AGE_CHOICE = "Age Chosen";
    public final static String GENDER_CHOICE = "Gender Chosen";
    public final static String HEIGHT_CHOICE = "Height Chosen";
    private static final String TAG = "WearableActivity";
    private BoxInsetLayout mContainerView;
    private Button mBtnView;
    private Button mBtnView2;
    private String gender;
    private String age;
    private String height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sense_layout);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mBtnView = (Button) findViewById(R.id.btn);
        mBtnView2 = (Button) findViewById(R.id.btn2);

        Intent intent = getIntent();
        gender = intent.getStringExtra(HeightListActivity.GENDER_CHOICE);
        age = intent.getStringExtra(HeightListActivity.AGE_CHOICE);
        height = intent.getStringExtra(HeightListActivity.HEIGHT_CHOICE);
    }

    public void onStartClick(View view) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.WearableService"
                    .equals(service.service.getClassName())) {
                Log.i(TAG, "Service already running!");
            }
        }

        Intent toservice = new Intent(this, WearableService.class);
        toservice.putExtra(GENDER_CHOICE, gender);
        toservice.putExtra(AGE_CHOICE, age);
        toservice.putExtra(HEIGHT_CHOICE, height);
        this.startService(toservice);
        mBtnView.setVisibility(View.INVISIBLE);
    }

    public void onStopClick(View view) {
        this.stopService(new Intent(this, WearableService.class));
        mBtnView2.setVisibility(View.INVISIBLE);
    }
}

