package com.mycompany.servicetime.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.mycompany.servicetime.model.TimeSlot;
import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;

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
}
