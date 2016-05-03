package com.mycompany.servicetime.presentation.addedittimeslot;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mycompany.servicetime.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Created by szhx on 5/2/2016.
 *
 * Tests for the add TimeSlot screen.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddEditTimeSlotScreenTest {

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public IntentsTestRule<AddEditTimeSlotActivity> mAddTimeSlotIntentsTestRule =
            new IntentsTestRule<>(AddEditTimeSlotActivity.class);

    /**
     * Prepare your test fixture for this test.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        /**
         * we register an IdlingResources with Espresso.
         * IdlingResource resource is a great way to tell Espresso when your app is in an idle state.
         * This helps Espresso to synchronize your test actions, which makes tests significantly more reliable.
         */
        Espresso.registerIdlingResources(
                mAddTimeSlotIntentsTestRule.getActivity().getCountingIdlingResource());
    }

    @After
    public void tearDown() throws Exception {
        /**
         * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
         */
        Espresso.unregisterIdlingResources(
                mAddTimeSlotIntentsTestRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void errorShownOnEmptyTimeSlot() {
        // Add empty name
        //onView(withId(R.id.timeSlotNameEditText)).perform(typeText(""));

        // Save the task
        onView(withId(R.id.time_slot_save)).perform(click());

        // Verify empty tasks snackbar is shown
        String emptyTaskMessageText = getTargetContext().getString(R.string.input_name_error);
        onView(withText(emptyTaskMessageText)).check(matches(isDisplayed()));
    }
}