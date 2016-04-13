package com.mycompany.servicetime.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.model.TimeSlot;
import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;
import com.mycompany.servicetime.util.ModelConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

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

    public ArrayList<int[]> getNextAlarmTime(boolean silentFlag) {
        ArrayList<int[]> timeSectors = new ArrayList<int[]>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentDayInWeek = calendar.get(Calendar.DAY_OF_WEEK);

        Cursor cursor = mContext.getContentResolver().query(TimeSlots.buildTimeSlotsUri(),
                TimeSlots.DEFAULT_PROJECTION,
                "substr(" + TimeSlots.DAYS + "," + currentDayInWeek + ",1) = ? and "
                        + TimeSlots.SERVICE_FLAG + " = ? ",
                new String[]{"1", "1"},
                TimeSlots.BEGIN_TIME_HOUR + "," + TimeSlots.BEGIN_TIME_MINUTE + ","
                        + TimeSlots.END_TIME_HOUR + "," + TimeSlots.END_TIME_MINUTE);
        if (cursor != null) {
            int beginTimeHour;
            int beginTimeMinute;
            int endTimeHour;
            int endTimeMinute;
            while (cursor.moveToNext()) {
                beginTimeHour = cursor.getInt(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_HOUR));
                beginTimeMinute = cursor
                        .getInt(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_MINUTE));
                endTimeHour = cursor.getInt(cursor.getColumnIndex(TimeSlots.END_TIME_HOUR));
                endTimeMinute = cursor.getInt(cursor.getColumnIndex(TimeSlots.END_TIME_MINUTE));
                timeSectors.add(new int[]{
                        beginTimeHour * 100 + beginTimeMinute, endTimeHour * 100 + endTimeMinute});
            }
        }

        return timeSectors;
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
