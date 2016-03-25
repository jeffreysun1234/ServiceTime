package com.mycompany.servicetime.util;

import android.database.Cursor;

import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.provider.CHServiceTimeContract;
import com.mycompany.servicetime.provider.ColumnIndexCache;

/**
 * Created by szhx on 3/24/2016.
 */
public class ModelConverter {

    public static TimeSlotItem cursorToTimeSlotItem(Cursor cursor, ColumnIndexCache
            mColumnIndexCache) {
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
