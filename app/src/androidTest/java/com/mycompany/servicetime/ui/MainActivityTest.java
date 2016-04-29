package com.mycompany.servicetime.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.WindowManager;

import com.mycompany.servicetime.R;
import com.mycompany.servicetime.util.EspressoIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isTouchable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mycompany.servicetime.util.CustomViewActions.waitForMatch;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

/**
 * Created by szhx on 4/1/2016.
 * <p>
 * This test class uses the actual DB
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private static final long UI_TEST_TIMEOUT = 5 * 1000; //5 seconds

    private MainActivity mActivity;

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     * <p>
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with
     * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
     * idle state. This helps Espresso to synchronize your test actions, which makes tests significantly
     * more reliable.
     */
    @UiThreadTest
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();

        try {
            mActivityRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KeyguardManager mKG = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
                    KeyguardManager.KeyguardLock mLock = mKG.newKeyguardLock(Context.KEYGUARD_SERVICE);
                    mLock.disableKeyguard();

                    //turn the screen on
                    mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Espresso.registerIdlingResources(mActivityRule.getActivity().getCountingIdlingResource());

        closeSoftKeyboard();
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void tearDown() throws Exception {
        Espresso.unregisterIdlingResources(mActivityRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void addTimeSlot() {

        // There is no data at begin.
        onView(withId(R.id.empty_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_tv)).check(matches(withText("No Time Slots.")));

        // click add icon
        onView(withId(R.id.time_slot_list_add)).perform(click());

        // add TimeSlot dat
        onView(withId(R.id.timeSlotNameEditText)).perform(typeText("Espresso Test"), closeSoftKeyboard());
        //onView(withId(R.id.timeSlotNameEditText)).perform(replaceText("Espresso Test\n"));

        EspressoIdlingResource.increment();
        closeSoftKeyboard();
        EspressoIdlingResource.decrement();

        onView(withId(R.id.beginTimePicker)).perform(PickerActions.setTime(9, 30));
        onView(withId(R.id.endTimePicker)).perform(PickerActions.setTime(17, 0));
        onView(withId(R.id.day3InWeekToggleButton)).perform(click());
        onView(withId(R.id.time_slot_save)).perform(click());

        // locate to the position 0.
        onView(withId(R.id.timeSlotListRecyclerView)).perform(RecyclerViewActions.scrollToPosition(0));

        // verify the data display correctly
        onView(withId(R.id.nameTextView)).check(matches(withText("Espresso Test")));

        // TODO: verify Edit icon is not display
        //onView(withId(R.id.edit_item_button)).check(matches(not(isCompletelyDisplayed())));

        // swipe
        onView(withId(R.id.nameTextView)).perform(swipeLeft());

        // click Edit icon
        onView(withId(R.id.edit_item_button)).perform(click());

        //Wait for the root view of the TimeSlot fragment.
        onView(isRoot()).perform(waitForMatch(withId(R.id.timeSlotNameEditText), UI_TEST_TIMEOUT));

        // change the name of the item
        onView(withId(R.id.timeSlotNameEditText)).perform(clearText(), replaceText("Name Changed"));
        onView(withId(R.id.time_slot_save)).perform(click());

        // verify the name of the item to be changed
        onView(withId(R.id.timeSlotListRecyclerView)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.nameTextView)).check(matches(withText("Name Changed")));

        // swipe again and click Delete icon
        onView(withId(R.id.nameTextView)).perform(swipeLeft());
        onView(withId(R.id.delete_item_button)).perform(click());

        // verify the item to be deleted.
        onView(withId(R.id.empty_tv)).check(matches(withText("No Time Slots.")));
    }
}
