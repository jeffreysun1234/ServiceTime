package com.mycompany.servicetime.data;

import android.database.Cursor;

import com.mycompany.servicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.data.source.model.TimeSlot;
import com.mycompany.servicetime.data.source.provider.CHServiceTimeContract;
import com.mycompany.servicetime.data.source.provider.ColumnIndexCache;

import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * Created by szhx on 3/24/2016.
 */
public class ModelConverter {
    private static final String TAG = makeLogTag(ModelConverter.class);

    public static TimeSlot converteCursorToTimeSlotModel(Cursor cursor, ColumnIndexCache mColumnIndexCache) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.timeSlotId = cursor.getString(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.TIME_SLOT_ID));
        timeSlot.name = cursor.getString(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract
                .TimeSlots.NAME));
        timeSlot.serviceFlag = cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract
                .TimeSlots.SERVICE_FLAG)) == 1 ? true : false;
        timeSlot.beginTimeHour = cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.BEGIN_TIME_HOUR));
        timeSlot.beginTimeMinute = cursor
                .getInt(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.BEGIN_TIME_MINUTE));
        timeSlot.endTimeHour = cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.END_TIME_HOUR));
        timeSlot.endTimeMinute = cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.END_TIME_MINUTE));
        timeSlot.days = cursor.getString(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.DAYS));
        timeSlot.repeatFlag = cursor.getInt(mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.REPEAT_FLAG)) == 1
                ? true : false;

        return timeSlot;
    }

    public static TimeSlotItem cursorToTimeSlotItem(Cursor cursor, ColumnIndexCache mColumnIndexCache) {
        TimeSlotItem timeSlotItem = new TimeSlotItem();
        timeSlotItem.setTimeSlotId(cursor.getString(mColumnIndexCache
                .getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.TIME_SLOT_ID)));
        timeSlotItem.setName(cursor.getString(
                mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.NAME)));
        timeSlotItem.setServiceFlag(cursor.getInt(mColumnIndexCache
                .getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.SERVICE_FLAG)) == 1 ?
                true : false);
        timeSlotItem.setBeginTimeHour(cursor.getInt(mColumnIndexCache
                .getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.BEGIN_TIME_HOUR)));
        timeSlotItem.setBeginTimeMinute(cursor.getInt(mColumnIndexCache
                .getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.BEGIN_TIME_MINUTE)));
        timeSlotItem.setEndTimeHour(cursor.getInt(mColumnIndexCache
                .getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.END_TIME_HOUR)));
        timeSlotItem.setEndTimeMinute(cursor.getInt(mColumnIndexCache
                .getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.END_TIME_MINUTE)));
        timeSlotItem.setDays(cursor.getString(
                mColumnIndexCache.getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.DAYS)));
        timeSlotItem.setRepeatFlag(cursor.getInt(mColumnIndexCache
                .getColumnIndex(cursor, CHServiceTimeContract.TimeSlots.REPEAT_FLAG)) == 1
                ? true : false);

        return timeSlotItem;
    }
}
