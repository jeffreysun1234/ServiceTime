package com.mycompany.servicetime.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.servicetime.firebase.FirebaseConstants;
import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.model.TimeSlot;
import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;
import com.mycompany.servicetime.support.PreferenceSupport;
import com.mycompany.servicetime.util.ModelConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    /**
     * @return timeSlotId
     */
    public String addOrUpdateTimeSlot(String timeSlotId, String name, int beginTimeHour, int
            beginTimeMinute, int endTimeHour, int endTimeMinute, String days, boolean repeatFlag) {
        String returnTimeSlotId;

        ContentValues values = new ContentValues();

        values.put(TimeSlots.NAME, name);
        values.put(TimeSlots.BEGIN_TIME_HOUR, beginTimeHour);
        values.put(TimeSlots.BEGIN_TIME_MINUTE, beginTimeMinute);
        values.put(TimeSlots.END_TIME_HOUR, endTimeHour);
        values.put(TimeSlots.END_TIME_MINUTE, endTimeMinute);
        values.put(TimeSlots.DAYS, days);
        values.put(TimeSlots.REPEAT_FLAG, repeatFlag ? 1 : 0);

        if (TextUtils.isEmpty(timeSlotId)) {
            returnTimeSlotId = TimeSlots.generateTimeSlotId();
            values.put(TimeSlots.TIME_SLOT_ID, returnTimeSlotId);
            mContext.getContentResolver().insert(TimeSlots.CONTENT_URI, values);
        } else {
            returnTimeSlotId = timeSlotId;
            mContext.getContentResolver()
                    .update(TimeSlots.buildTimeSlotUri(timeSlotId), values, null, null);
        }

        return returnTimeSlotId;
    }

    public void deleteTimeSlot(String timeSlotId) {
        mContext.getContentResolver().delete(TimeSlots.buildTimeSlotUri(timeSlotId), null, null);
    }


    public void deleteAllTimeSlot() {
        mContext.getContentResolver().delete(TimeSlots.CONTENT_URI, null, null);
    }

    public Cursor getAllTimeSlot() {
        Cursor cursor = mContext.getContentResolver().query(TimeSlots.CONTENT_URI,
                TimeSlots.DEFAULT_PROJECTION, null, null, null);
        return cursor;
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
                "substr(" + TimeSlots.DAYS + "," + currentDayInWeek + ",1) = ? and "
                        + TimeSlots.SERVICE_FLAG + " = ? ",
                new String[]{"1", "1"},
                TimeSlots.BEGIN_TIME_HOUR + "," + TimeSlots.BEGIN_TIME_MINUTE + ","
                        + TimeSlots.END_TIME_HOUR + "," + TimeSlots.END_TIME_MINUTE);

        if (cursor != null) {
            // all time slots are unactivated on today.
            if (cursor.getCount() == 0) {
                if (silentFlag)
                    return 0;
                else
                    return calendar.getTimeInMillis();
            }

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

    public void restoreAllTimeSlots(Collection<TimeSlotItem> timeSlotItems) {
        String currentTimeSlotId;

        // clear DB
        deleteAllTimeSlot();
        for (TimeSlotItem tsItem : timeSlotItems) {
            // add a timeslot, timeSlotId will be a new value.
            currentTimeSlotId = addOrUpdateTimeSlot("", tsItem.getName(),
                    tsItem.getBeginTimeHour(), tsItem.getBeginTimeMinute(),
                    tsItem.getEndTimeHour(), tsItem.getEndTimeMinute(),
                    tsItem.getDays(), tsItem.isRepeatFlag());
            // restore the service flag
            updateServiceFlag(currentTimeSlotId, tsItem.isServiceFlag());
        }
    }

    public ArrayList<TimeSlotItem> backupAllTimeSlots() {
        // Get all TimeSlot from DB
        Cursor cursor = CHServiceTimeDAO.create(mContext).getAllTimeSlot();
        if (cursor == null)
            return null;

        ArrayList<TimeSlotItem> timeSlotItems = new ArrayList<TimeSlotItem>();

        ColumnIndexCache columnIndexCache = new ColumnIndexCache();
        TimeSlotItem tsItem;

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            // convert cursor to TimeSlotItem model
            tsItem = ModelConverter.cursorToTimeSlotItem(cursor, columnIndexCache);

            timeSlotItems.add(tsItem);
        }

        cursor.close();

        return timeSlotItems;
    }
}
