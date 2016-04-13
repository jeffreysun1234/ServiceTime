package com.mycompany.servicetime.support;

import com.mycompany.servicetime.util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * Created by szhx on 4/6/2016.
 */
public class TimeSlotSupport {
    private static final String TAG = makeLogTag(TimeSlotSupport.class);

    /**
     * @param originalTimeSectors TimeSlot is ordered by begintime and endtime ascending
     * @param silentFlag
     * @param currentTimeInt      In 24 hours format, for example, 920 means 9:20am.
     * @return 0 means "No more alarm today"
     */
    public static long getNextAlarmTime(ArrayList<int[]> originalTimeSectors, boolean silentFlag, int currentTimeInt) {
        long nextAlarmTime = 0L;

        if (originalTimeSectors == null)
            return nextAlarmTime;

        // all time slots are unactivated on today.
        if (originalTimeSectors.size() == 0) {
            if (silentFlag)
                return 0;
            else
                return System.currentTimeMillis();
        }

        ArrayList<int[]> silentTimeSlotList = new ArrayList<int[]>();
        ArrayList<int[]> normalTimeSlotList = new ArrayList<int[]>();
        int[] timeSlotTemp;

        /*** arrange timeSlotList ***/
        // merge overlapping timeslots
        timeSlotTemp = new int[2];
        timeSlotTemp[0] = originalTimeSectors.get(0)[0];
        timeSlotTemp[1] = originalTimeSectors.get(0)[1];
        for (int i = 1; i < originalTimeSectors.size(); i++) {
            if (originalTimeSectors.get(i)[0] <= timeSlotTemp[1]) {
                if (timeSlotTemp[1] < originalTimeSectors.get(i)[1]) {
                    timeSlotTemp[1] = originalTimeSectors.get(i)[1];
                }
            } else {
                silentTimeSlotList.add(timeSlotTemp);
                timeSlotTemp = new int[2];
                timeSlotTemp[0] = originalTimeSectors.get(i)[0];
                timeSlotTemp[1] = originalTimeSectors.get(i)[1];
            }
        }
        silentTimeSlotList.add(timeSlotTemp);

        if (silentFlag) {
            nextAlarmTime = getTimePoint(silentTimeSlotList, currentTimeInt);
        } else {
            // build normal timeslot list
            timeSlotTemp = new int[2];
            timeSlotTemp[0] = 0;
            timeSlotTemp[1] = silentTimeSlotList.get(0)[0];
            normalTimeSlotList.add(timeSlotTemp);
            for (int i = 1; i < silentTimeSlotList.size(); i++) {
                timeSlotTemp = new int[2];
                timeSlotTemp[0] = silentTimeSlotList.get(i - 1)[1];
                timeSlotTemp[1] = silentTimeSlotList.get(i)[0];
                normalTimeSlotList.add(timeSlotTemp);
            }
            timeSlotTemp = new int[2];
            timeSlotTemp[0] = silentTimeSlotList.get(silentTimeSlotList.size() - 1)[1];
            timeSlotTemp[1] = 2359;
            normalTimeSlotList.add(timeSlotTemp);
            nextAlarmTime = getTimePoint(normalTimeSlotList, currentTimeInt);
        }

        return nextAlarmTime;
    }

    private static long getTimePoint(ArrayList<int[]> timeSlotList, int currentTime) {
        LOGD(TAG, "TimeSlotList: " + Arrays.deepToString(timeSlotList.toArray()));

        long timePoint = 0L;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        for (int[] timeSlotTemp : timeSlotList) {
            // array structure is [beginTime, endTime].
            if (currentTime < timeSlotTemp[1]) {
                calendar.set(Calendar.HOUR_OF_DAY, (int) Math.floor(currentTime / 100.0d));
                calendar.set(Calendar.MINUTE, currentTime % 100);
                calendar.set(Calendar.SECOND, 0);
                if (currentTime < timeSlotTemp[0]) {
                    calendar.set(Calendar.HOUR_OF_DAY, (int) Math.floor(timeSlotTemp[0] / 100.0d));
                    calendar.set(Calendar.MINUTE, timeSlotTemp[0] % 100);
                    calendar.set(Calendar.SECOND, 0);
                }
                timePoint = calendar.getTimeInMillis();
                break;
            }
        }

        try {
            LOGD(TAG, currentTime + " --> " + DateUtil.format(timePoint) + " === " + timePoint);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timePoint;
    }

}
