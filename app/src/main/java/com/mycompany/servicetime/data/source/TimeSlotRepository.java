package com.mycompany.servicetime.data.source;

import android.support.annotation.NonNull;

import com.mycompany.servicetime.data.source.model.TimeSlot;
import com.mycompany.servicetime.data.source.provider.CHServiceTimeDAO;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by szhx on 5/1/2016.
 * <p/>
 * Concrete implementation to load TimeSlots from the data sources.
 */
public class TimeSlotRepository implements TimeSlotDataSource {

    private static TimeSlotRepository INSTANCE = null;

    private final CHServiceTimeDAO mCHServiceTimeDAO;

    // Prevent direct instantiation.
    private TimeSlotRepository(@NonNull CHServiceTimeDAO serviceTimeDAO) {
        mCHServiceTimeDAO = checkNotNull(serviceTimeDAO);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param serviceTimeDAO the backend data source
     * @return the {@link TimeSlotRepository} instance
     */
    public static TimeSlotRepository getInstance(CHServiceTimeDAO serviceTimeDAO) {
        if (INSTANCE == null) {
            INSTANCE = new TimeSlotRepository(serviceTimeDAO);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(CHServiceTimeDAO)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets TimeSlots from local data source (SQLite).
     * <p>
     * Note: {@link LoadTimeSlotsCallback#onDataNotAvailable()} is fired if the data sources fail to
     * get the data.
     */
    @Override
    public void getTimeSlots(@NonNull LoadTimeSlotsCallback callback) {
        checkNotNull(callback);

        try {
            callback.onTimeSlotsLoaded(mCHServiceTimeDAO.getAllTimeSlot());
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDataNotAvailable();
        }
    }

    /**
     * Gets TimeSlot from local data source (sqlite).
     * <p>
     * Note: {@link GetTimeSlotCallback#onDataNotAvailable()} is fired if data sources fail to
     * get the data.
     */
    @Override
    public void getTimeSlot(@NonNull String timeSlotId, @NonNull GetTimeSlotCallback callback) {
        checkNotNull(timeSlotId);
        checkNotNull(callback);

        try {
            callback.onTimeSlotLoaded(mCHServiceTimeDAO.getTimeSlot(timeSlotId));
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTimeSlot(@NonNull TimeSlot timeSlot) {
        checkNotNull(timeSlot);
        mCHServiceTimeDAO.addOrUpdateTimeSlot(timeSlot.timeSlotId, timeSlot.name, timeSlot.beginTimeHour,
                timeSlot.beginTimeMinute, timeSlot.endTimeHour, timeSlot.endTimeMinute, timeSlot.days,
                timeSlot.repeatFlag);
    }

    @Override
    public void activateTimeSlot(@NonNull String timeSlotId, boolean serviceFlag) {
        checkNotNull(timeSlotId);
        mCHServiceTimeDAO.updateServiceFlag(timeSlotId, serviceFlag);
    }

    @Override
    public void deleteAllTimeSlots() {
        // TODO: deleteAllTimeSlots()
    }

    @Override
    public void deleteTimeSlot(@NonNull String timeSlotId) {
        checkNotNull(timeSlotId);
        mCHServiceTimeDAO.deleteTimeSlot(timeSlotId);
    }
}
