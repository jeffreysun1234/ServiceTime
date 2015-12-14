package com.mycompany.servicetime.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.mycompany.servicetime.model.TimeSlot;
import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * Created by szhx on 11/10/2015.
 */
public class CHServiceTimeDAO {
    private static final String TAG = makeLogTag(CHServiceTimeDAO.class);

    private Context mContext;

    private CHServiceTimeDAO(Context context) {
        mContext = context;
    }

    public static CHServiceTimeDAO create(Context context) {
        return new CHServiceTimeDAO(context);
    }

    public void addOrUpdateTimeSlot(String timeSlotId, String name, int beginTimeHour, int
            beginTimeMinute, int endTimeHour, int endTimeMinute, String days, boolean repeatFlag) {
        ContentValues values = new ContentValues();

        values.put(TimeSlots.NAME, name);
        values.put(TimeSlots.BEGIN_TIME_HOUR, beginTimeHour);
        values.put(TimeSlots.BEGIN_TIME_MINUTE, beginTimeMinute);
        values.put(TimeSlots.END_TIME_HOUR, endTimeHour);
        values.put(TimeSlots.END_TIME_MINUTE, endTimeMinute);
        values.put(TimeSlots.DAYS, days);
        values.put(TimeSlots.REPEAT_FLAG, repeatFlag ? 1 : 0);

        if (TextUtils.isEmpty(timeSlotId)) {
            values.put(TimeSlots.TIME_SLOT_ID, TimeSlots.generateTimeSlotId());
            mContext.getContentResolver().insert(TimeSlots.CONTENT_URI, values);
        } else {
            mContext.getContentResolver()
                    .update(TimeSlots.buildTimeSlotUri(timeSlotId), values, null, null);
        }

    }

    public void deleteTimeSlot(String timeSlotId) {
        mContext.getContentResolver().delete(TimeSlots.buildTimeSlotUri(timeSlotId), null, null);
    }

    /**
     * @param timeSlotId
     * @return null if not found.
     */
    public TimeSlot getTimeSlot(String timeSlotId) {
        TimeSlot timeSlot = new TimeSlot();
        Cursor cursor = mContext.getContentResolver().query(TimeSlots.buildTimeSlotUri(timeSlotId),
                TimeSlots.DEFAULT_PROJECTION, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        timeSlot.timeSlotId = cursor.getString(cursor.getColumnIndex(TimeSlots.TIME_SLOT_ID));
        timeSlot.name = cursor.getString(cursor.getColumnIndex(TimeSlots.NAME));
        timeSlot.serviceFlag = cursor.getInt(cursor.getColumnIndex(TimeSlots.SERVICE_FLAG)) == 1 ?
                true : false;
        timeSlot.beginTimeHour = cursor.getInt(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_HOUR));
        timeSlot.beginTimeMinute = cursor
                .getInt(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_MINUTE));
        timeSlot.endTimeHour = cursor.getInt(cursor.getColumnIndex(TimeSlots.END_TIME_HOUR));
        timeSlot.endTimeMinute = cursor.getInt(cursor.getColumnIndex(TimeSlots.END_TIME_MINUTE));
        timeSlot.days = cursor.getString(cursor.getColumnIndex(TimeSlots.DAYS));
        timeSlot.repeatFlag = cursor.getInt(cursor.getColumnIndex(TimeSlots.REPEAT_FLAG)) == 1
                ? true : false;

        return timeSlot;
    }

    public void updateServiceFlag(String timeSlotId, boolean serviceFlag) {
        LOGD(TAG, "updateServiceFlag: timeSlotId=" + timeSlotId + ", serviceFlag=" + serviceFlag);
        ContentValues values = new ContentValues();
        values.put(TimeSlots.SERVICE_FLAG, serviceFlag ? 1 : 0);

        if (!TextUtils.isEmpty(timeSlotId)) {
            mContext.getContentResolver()
                    .update(TimeSlots.buildTimeSlotUri(timeSlotId), values, null, null);
        }
    }

    public long getNextAlarmTime(boolean silentFlag) {
        long nextAlarmTime = 0L;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentDayInWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int beginTimeHour, beginTimeMinute, endTimeHour, endTimeMinute;
        ArrayList<int[]> silentTimeSlotListTemp = new ArrayList<int[]>();
        ArrayList<int[]> silentTimeSlotList = new ArrayList<int[]>();
        ArrayList<int[]> normalTimeSlotList = new ArrayList<int[]>();
        int[] timeSlotTemp;

        Cursor cursor = mContext.getContentResolver().query(TimeSlots.buildTimeSlotsUri(),
                TimeSlots.DEFAULT_PROJECTION,
                "substr(" + TimeSlots.DAYS + "," + currentDayInWeek + ",1) = ?",
                new String[]{"1"},
                TimeSlots.BEGIN_TIME_HOUR + "," + TimeSlots.BEGIN_TIME_MINUTE + ","
                        + TimeSlots.END_TIME_HOUR + "," + TimeSlots.END_TIME_MINUTE);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                beginTimeHour = cursor.getInt(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_HOUR));
                beginTimeMinute = cursor
                        .getInt(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_MINUTE));
                endTimeHour = cursor.getInt(cursor.getColumnIndex(TimeSlots.END_TIME_HOUR));
                endTimeMinute = cursor.getInt(cursor.getColumnIndex(TimeSlots.END_TIME_MINUTE));
                silentTimeSlotListTemp.add(new int[]{
                        beginTimeHour * 100 + beginTimeMinute, endTimeHour * 100 + endTimeMinute});
            }

            // arrange timeSlotList
            timeSlotTemp = new int[2];
            timeSlotTemp[0] = silentTimeSlotListTemp.get(0)[0];
            timeSlotTemp[1] = silentTimeSlotListTemp.get(0)[1];
            for (int i = 1; i < silentTimeSlotListTemp.size(); i++) {
                if (silentTimeSlotListTemp.get(i)[0] <= timeSlotTemp[1]) {
                    if (timeSlotTemp[1] < silentTimeSlotListTemp.get(i)[1]) {
                        timeSlotTemp[1] = silentTimeSlotListTemp.get(i)[1];
                    }
                } else {
                    silentTimeSlotList.add(timeSlotTemp);
                    timeSlotTemp = new int[2];
                    timeSlotTemp[0] = silentTimeSlotListTemp.get(i)[0];
                    timeSlotTemp[1] = silentTimeSlotListTemp.get(i)[1];
                }
            }
            silentTimeSlotList.add(timeSlotTemp);

            if (silentFlag) {
                nextAlarmTime = getTimePoint(silentTimeSlotList, calendar);
            } else {
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
                nextAlarmTime = getTimePoint(normalTimeSlotList, calendar);
            }
        }

        return nextAlarmTime;
    }

    private long getTimePoint(ArrayList<int[]> timeSlotList, Calendar calendar) {
        LOGD(TAG, "TimeSlotList: " + Arrays.deepToString(timeSlotList.toArray()));

        long timePoint = 0L;

        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentTime = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);

        for (int[] timeSlotTemp : timeSlotList) {
            // array structure is [beginTime, endTime].
            if (currentTime < timeSlotTemp[1]) {
                if (currentTime < timeSlotTemp[0]) {
                    calendar.set(Calendar.HOUR_OF_DAY, (int) Math.floor(timeSlotTemp[0] / 100.0d));
                    calendar.set(Calendar.MINUTE, timeSlotTemp[0] % 100);
                }
                timePoint = calendar.getTimeInMillis();
                break;
            }
        }

        return timePoint;
    }
}
