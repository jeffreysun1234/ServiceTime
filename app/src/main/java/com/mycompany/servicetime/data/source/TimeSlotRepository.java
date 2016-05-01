package com.mycompany.servicetime.data.source;

import android.support.annotation.NonNull;

import com.mycompany.servicetime.model.TimeSlot;

/**
 * Created by szhx on 5/1/2016.
 */
public class TimeSlotRepository implements TimeSlotDataSource{
    @Override
    public void getTimeSlots(@NonNull LoadTimeSlotsCallback callback) {

    }

    @Override
    public void getTimeSlot(@NonNull String timeSlotId, @NonNull GetTimeSlotCallback callback) {

    }

    @Override
    public void saveTimeSlot(@NonNull TimeSlot timeSlot) {

    }

    @Override
    public void activateTimeSlot(@NonNull String timeSlotId) {

    }

    @Override
    public void refreshTimeSlots() {

    }

    @Override
    public void deleteAllTimeSlots() {

    }

    @Override
    public void deleteTimeSlot(@NonNull String timeSlotId) {

    }
}
