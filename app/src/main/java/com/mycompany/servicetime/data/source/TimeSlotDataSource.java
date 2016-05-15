package com.mycompany.servicetime.data.source;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mycompany.servicetime.data.source.model.TimeSlot;

/**
 * Main entry point for accessing TimeSlot data.
 * <p/>
 * For simplicity, only getTimeSlots() and getTimeSlot() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new TimeSlot is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface TimeSlotDataSource {
    interface LoadTimeSlotsCallback {

        void onTimeSlotsLoaded(Cursor timeSlots);

        void onDataNotAvailable();
    }

    interface GetTimeSlotCallback {

        void onTimeSlotLoaded(TimeSlot timeSlot);

        void onDataNotAvailable();
    }

    void getTimeSlots(@NonNull LoadTimeSlotsCallback callback);

    void getTimeSlot(@NonNull String timeSlotId, @NonNull GetTimeSlotCallback callback);

    void saveTimeSlot(@NonNull TimeSlot timeSlot);

    void activateTimeSlot(@NonNull String timeSlotId, boolean serviceFlag);

    //void clearCompletedTimeSlots();

    //void refreshTimeSlots();

    void deleteAllTimeSlots();

    void deleteTimeSlot(@NonNull String timeSlotId);
}
