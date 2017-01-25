package ruby.trialappv2;

import android.graphics.Point;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.wearable.view.WearableListView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by ruby__000 on 25/01/2017.
 */
public class SelectTrialTypeTest {
    @Rule
    public ActivityTestRule<SelectTrialType> main = new ActivityTestRule<SelectTrialType>(SelectTrialType.class) {
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

    @Test
    public void onCreate() throws Exception {

    }

    //Test that item clicked is used for intent
    @Test
    public void onClick() throws Exception {
        //click random item in the list
        int position = (int) (Math.random() * ((1 - 0) + 1)) + 0;
        //get expected value
        String listItem = null;

        if (position == 0) {
            listItem = "Arms swinging";
        } else if (position == 1) {
            listItem = "Hands in pockets";
        } else if (position == 2) {
            listItem = "Flat Shoes";
        } else if (position == 3) {
            listItem = "Heeled Shoes";
        }

        onView(withId(R.id.List1)).perform(RecyclerViewActions.scrollToPosition(position), click());
        // onView(allOf(withId(R.id.List1), isDisplayed())).perform(click());
        //check new activity started
        intended(hasComponent(GenderListActivity.class.getName()));
        //check intent contains trial type just clicked
        intended((
                hasExtras(
                        hasEntry(equalTo("Trial Chosen"), equalTo((listItem))))));

    }

    @Test
    public void onTopEmptyRegionClick() throws Exception {

    }


    @Test
    public void testShouldLaunchActivityListScrolledToTopWithCorrectTotalEntries() throws Exception {
        // onView(withId(R.id.List1)).check(matches(withChild(withText("Female"))));
        WearableListView listview = (WearableListView) main.getActivity().findViewById(R.id.List1);
        onView(withId(R.id.List1)).check(matches(isDisplayed()));

        //Check that there are four items in the list
        int itemCount = listview.getAdapter().getItemCount();
        assertThat(itemCount, is(4));

        assertThat(listview.isAtTop(), is(true));
    }

    @Test
    public void testTitleNAme() throws Exception {
        onView(withId(R.id.title)).check(matches(withText("Please select Trial Type")));
    }


}