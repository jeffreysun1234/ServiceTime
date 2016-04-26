package com.mycompany.servicetime.ui;

import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mycompany.servicetime.R;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

/**
 * Created by szhx on 4/1/2016.
 * <p/>
 * This test class uses the actual DB
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private MainActivity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void addTimeSlot() {

        // There is no data at begin.
        onView(withId(R.id.empty_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_tv)).check(matches(withText("No Time Slots.")));

        // click add icon
        onView(withId(R.id.time_slot_list_add)).perform(click());

        // add TimeSlot data
        //onView(withId(R.id.timeSlotNameEditText)).perform(typeText("Espresso Test"), closeSoftKeyboard());
        onView(withId(R.id.timeSlotNameEditText)).perform(replaceText("Espresso Test"));
        onView(withId(R.id.beginTimePicker)).perform(PickerActions.setTime(9, 30));
        onView(withId(R.id.endTimePicker)).perform(PickerActions.setTime(17, 0));
        onView(withId(R.id.day2InWeekToggleButton)).perform(click());
        onView(withId(R.id.time_slot_save)).perform(click());

        // locate to the position 0.
        onView(withId(R.id.timeSlotListRecyclerView)).perform(RecyclerViewActions.scrollToPosition(0));

        // verify the data display correctly
        onView(withId(R.id.nameTextView)).check(matches(withText("Espresso Test")));

        // TODO: verify Edit icon is not display
        //onView(withId(R.id.edit_item_button)).check(matches(not(isDisplayed())));

        // swipe
        onView(withId(R.id.nameTextView)).perform(swipeLeft());

        // click Edit icon
        onView(withId(R.id.edit_item_button)).perform(click());

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
