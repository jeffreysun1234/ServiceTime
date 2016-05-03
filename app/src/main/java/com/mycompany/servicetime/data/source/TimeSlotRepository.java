package com.mycompany.servicetime.data.source;

import android.support.annotation.NonNull;

import com.mycompany.servicetime.model.TimeSlot;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by szhx on 5/1/2016.
 * <p/>
 * Concrete implementation to load TimeSlots from the data sources.
 */
public class TimeSlotRepository implements TimeSlotDataSource {

    private static TimeSlotRepository INSTANCE = null;

    private final TimeSlotDataSource mTimeSlotDataSource;

    // Prevent direct instantiation.
    private TimeSlotRepository(@NonNull TimeSlotDataSource timeSlotRemoteDataSource) {
        mTimeSlotDataSource = checkNotNull(timeSlotRemoteDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param timeSlotDataSource the backend data source
     * @return the {@link TimeSlotRepository} instance
     */
    public static TimeSlotRepository getInstance(TimeSlotDataSource timeSlotDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TimeSlotRepository(timeSlotDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TimeSlotDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

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
