package ruby.trialappv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;

/* Display A Begin button and on click, go to next activity (gender selection)*/

public class MainActivity extends WearableActivity {

    private static final String TAG = "WearableActivity";
    private BoxInsetLayout mContainerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onBeginClick(View view) {
        Intent intent = new Intent(this, SelectTrialType.class);
        startActivity(intent);
        finish();
    }
}
