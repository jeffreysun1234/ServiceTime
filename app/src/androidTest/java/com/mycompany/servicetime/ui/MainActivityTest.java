package com.mycompany.servicetime.ui;

import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by szhx on 4/1/2016.
 * <p/>
 * This test class uses the actual DB
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private MainActivity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);
//    {
//        @Override
//        protected Intent getActivityIntent() {
//            Context targetContext = InstrumentationRegistry.getInstrumentation()
// .getTargetContext();
//
//            RenamingDelegatingContext mockContext =
//                    new RenamingDelegatingContext(targetContext, "test_");
//            mockContext.makeExistingFilesAndDbsAccessible();
//
//            Intent result = new Intent(mockContext, MainActivity.class);
//            return result;
//        }
//    };


    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Only run at the first time.
     */
    //@Test
    public void testTimeSlotListIsEmpty() {
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
        //onView(withId(R.id.empty_tv)).check(matches(withText("No Time Slots.")));
    }

    @Test
    public void addTimeSlot() {

        // There is no data at begin.
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
        //onView(withId(R.id.empty_tv)).check(matches(withText("No Time Slots.")));

        // click add icon
        onView(withId(R.id.time_slot_list_add)).perform(click());

        // add TimeSlot data
        onView(withId(R.id.timeSlotNameEditText))
                .perform(typeText("Espresso Test"), closeSoftKeyboard());
        onView(withId(R.id.beginTimePicker)).perform(PickerActions.setTime(9, 30));
        onView(withId(R.id.endTimePicker)).perform(PickerActions.setTime(17, 0));
        onView(withId(R.id.day2InWeekToggleButton)).perform(click());
        onView(withId(R.id.saveButton)).perform(click());

        // verify the data display correctly
        onData(anything()).atPosition(0).onChildView(withId(R.id.nameTextView))
                .check(matches(withText("Espresso Test")));

        // Long click the added item
        onData(anything()).atPosition(0).onChildView(withId(R.id.nameTextView))
                .perform(longClick());

        // change the name of the item
        onView(withId(R.id.timeSlotNameEditText))
                .perform(clearText(), typeText("Name Changed"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(click());

        // verify the name of the item to be changed
        onData(anything()).atPosition(0).onChildView(withId(R.id.nameTextView))
                .check(matches(withText("Name Changed")));

        // Long click the item again
        onData(anything()).atPosition(0).onChildView(withId(R.id.nameTextView))
                .perform(longClick());

        // verify the delete button to be displayed then click the button
        onView(withId(R.id.deleteButton)).check(matches(isDisplayed()))
                .perform(click());

        // verify the item to be deleted.
        //onView(withId(R.id.empty_tv)).check(matches(withText("No Time Slots.")));
    }
}
