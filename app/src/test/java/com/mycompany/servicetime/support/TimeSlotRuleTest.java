package com.mycompany.servicetime.support;

import android.os.Build;

import com.mycompany.servicetime.BuildConfig;
import com.mycompany.servicetime.domain.business.TimeSlotRule;
import com.mycompany.servicetime.util.DateUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by szhx on 4/6/2016.
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class TimeSlotRuleTest {

    private int currentTime;
    private long expected;

    //parameters pass via this constructor
    public TimeSlotRuleTest(int currentTime, long expected) {
        this.currentTime = currentTime;
        this.expected = expected;
    }

    //Declares parameters here
    @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: ({0} --> {1}")
    public static Collection<Object[]> data1() {
        return Arrays.asList(new Object[][]{
                {200, DateUtil.getCurrentTimestamp(310) / 1000},
                {400, DateUtil.getCurrentTimestamp(400) / 1000},
                {745, DateUtil.getCurrentTimestamp(800) / 1000},
                {830, DateUtil.getCurrentTimestamp(830) / 1000},
                {1130, DateUtil.getCurrentTimestamp(1130) / 1000},
                {1200, DateUtil.getCurrentTimestamp(1200) / 1000},
                {1400, DateUtil.getCurrentTimestamp(1400) / 1000},
                {1650, DateUtil.getCurrentTimestamp(1650) / 1000},
                {1950, DateUtil.getCurrentTimestamp(1950) / 1000},
                {2050, DateUtil.getCurrentTimestamp(2050) / 1000},
                {2350, 0}
        });
    }

    private ArrayList<int[]> originalTimeSectors;

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;

        originalTimeSectors = new ArrayList<int[]>();
        originalTimeSectors.add(new int[]{310, 730});
        originalTimeSectors.add(new int[]{800, 1320});
        originalTimeSectors.add(new int[]{1110, 1530});
        originalTimeSectors.add(new int[]{1600, 1830});
        originalTimeSectors.add(new int[]{1800, 2130});
        originalTimeSectors.add(new int[]{2000, 2230});
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetNextAlarmTime() throws Exception {
        assertEquals(expected, TimeSlotRule.getNextAlarmTime(originalTimeSectors, true, currentTime) / 1000);
    }
}
