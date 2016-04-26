package com.mycompany.servicetime.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.RenamingDelegatingContext;

import com.mycompany.servicetime.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by szhx on 4/1/2016.
 */
//@RunWith(AndroidJUnit4.class)
public class MainActivityTest2{
//public class MainActivityTest2 extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mActivity;

//    public MainActivityTest2() {
//        super(MainActivity.class);
//    }

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

//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//
//        // Injecting the Instrumentation instance is required
//        // for your test to run with AndroidJUnitRunner.
//        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
//        mActivity = getActivity();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        super.tearDown();
//    }

    /**
     * Only run at the first time.
     */
    //@Test
    public void testTimeSlotListIsEmpty() {
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
        //onView(withId(R.id.empty_tv)).check(matches(withText("No Time Slots.")));
    }

    //@Test
    public void addTimeSlot() {

        RenamingDelegatingContext mockContext =
                new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(),
                        "test_");
        mockContext.makeExistingFilesAndDbsAccessible();
        //setActivityContext(mockContext);

        // click add icon
        onView(withId(R.id.time_slot_list_add)).perform(click());

        // add TimeSlot data
//        onView(withId(R.id.timeSlotNameEditText))
//                .perform(typeText("Espresso Test"), closeSoftKeyboard());
//        onView(withId(R.id.beginTimePicker)).perform(PickerActions.setTime(9, 30));
//        onView(withId(R.id.endTimePicker)).perform(PickerActions.setTime(17, 0));
//        onView(withId(R.id.day2InWeekToggleButton)).perform(click());
        //onView(withId(R.id.saveButton)).perform(click());

        // verify the data display correctly
//        onData(anything()).atPosition(0).onChildView(withId(R.id.nameTextView))
//                .check(matches(withText("Espresso Test")));
    }
}
