package com.mycompany.servicetime.provider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;

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

    public void addTimeSlot(String name, String beginTime, String endTime, String days, int
            repeatFlag) {
        String nameTemp = name.toUpperCase();
        ContentValues values = new ContentValues();
        values.put(TimeSlots.TIME_SLOT_ID, TimeSlots.generateTimeSlotId(nameTemp));
        values.put(TimeSlots.NAME, name);
        values.put(TimeSlots.BEGIN_TIME, beginTime);
        values.put(TimeSlots.END_TIME, endTime);
        values.put(TimeSlots.DAYS, days);
        values.put(TimeSlots.REPEAT_FLAG, repeatFlag);

        Uri uri = mContext.getContentResolver().insert(TimeSlots.CONTENT_URI, values);
    }

    public void deleteTimeSlot(String timeSlotId) {
        mContext.getContentResolver().delete(TimeSlots.buildTimeSlotUri(timeSlotId), null, null);
    }
}
