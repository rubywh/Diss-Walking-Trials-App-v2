package ruby.trialappv2;

import android.graphics.Point;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by ruby__000 on 25/01/2017.
 */
public class SenseActivityTest {

    @Rule
    public ActivityTestRule<SenseActivity> main = new ActivityTestRule<SenseActivity>(SenseActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            Intents.init();
            super.beforeActivityLaunched();
        }

        @Override
        protected void afterActivityFinished() {
            super.afterActivityFinished();
            Intents.release();
        }
    };


    //Wake up device before test
    @Before
    public void init() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Point[] coordinates = new Point[4];
        coordinates[0] = new Point(248, 1520);
        coordinates[1] = new Point(248, 929);
        coordinates[2] = new Point(796, 1520);
        coordinates[3] = new Point(796, 929);
        try {
            if (!uiDevice.isScreenOn()) {
                uiDevice.wakeUp();
                uiDevice.swipe(coordinates, 10);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //This test fails at the moment

    @Test
    public void onStartClick() throws Exception {
        onView(withId(R.id.btn)).perform(click());
        onView(withId(R.id.btn)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.btn2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        intended(hasComponent(WearableService.class.getName()));


        intended((
                hasExtraWithKey(
                        (equalTo("Trial Chosen")))));

        //Check there exists Extras for Gender Chosen and Trial Chosen
        //At the moment these can have values ANYTHING (really, null)
        intended((
                hasExtraWithKey(
                        (equalTo("Gender Chosen")))));
        intended((
                hasExtraWithKey(
                        (equalTo("Age Chosen")))));
        intended((
                hasExtraWithKey(
                        (equalTo("Height Chosen")))));
        onView(withId(R.id.btn2)).perform(scrollTo(), click());
        onView(withId(R.id.btn2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void buttonsDisplayedTest() throws Exception {
        onView(withId(R.id.btn)).check(matches(isDisplayed()));
        onView(withId(R.id.btn2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.btn2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }


    @Test
    public void onStopClick() throws Exception {

    }

    @Test
    public void onFinishClick() throws Exception {
        // onView(withId(R.id.btn3)).perform(click());
    }

}