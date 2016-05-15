/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.servicetime.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.mycompany.servicetime.data.source.model.TimeSlot;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of a data source with static access to the data for easy testing.
 */
public class FakeTimeSlotDataSource implements TimeSlotDataSource {

    private static FakeTimeSlotDataSource INSTANCE;

    private static final Map<String, TimeSlot> TIMESLOT_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeTimeSlotDataSource() {}

    public static FakeTimeSlotDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTimeSlotDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getTimeSlots(@NonNull LoadTimeSlotsCallback callback) {
       // callback.onTimeSlotsLoaded(Lists.newArrayList(TIMESLOT_SERVICE_DATA.values()));
    }

    @Override
    public void getTimeSlot(@NonNull String timeSlotId, @NonNull GetTimeSlotCallback callback) {
        TimeSlot timeSlot = TIMESLOT_SERVICE_DATA.get(timeSlotId);
        callback.onTimeSlotLoaded(timeSlot);
    }

    @Override
    public void saveTimeSlot(TimeSlot timeSlot) {
        TIMESLOT_SERVICE_DATA.put(timeSlot.timeSlotId, timeSlot);
    }

//    @Override
//    public void completeTimeSlot(TimeSlot timeSlot) {
//        TimeSlot completedTimeSlot = new TimeSlot(timeSlot.getTitle(), timeSlot.getDescription(), timeSlot.getId(), true);
//        TIMESLOT_SERVICE_DATA.put(timeSlot.getId(), completedTimeSlot);
//    }

//    @Override
//    public void completeTimeSlot(@NonNull String timeSlotId) {
//        // Not required for the remote data source.
//    }

//    @Override
//    public void activateTimeSlot(TimeSlot timeSlot) {
//        TimeSlot activeTimeSlot = new TimeSlot(timeSlot.getTitle(), timeSlot.getDescription(), timeSlot.getId());
//        TIMESLOT_SERVICE_DATA.put(timeSlot.getId(), activeTimeSlot);
//    }

    @Override
    public void activateTimeSlot(@NonNull String timeSlotId) {
        // Not required for the remote data source.
    }

    public void refreshTimeSlots() {
        // Not required because the {@link TimeSlotsRepository} handles the logic of refreshing the
        // timeSlots from all the available data sources.
    }

    @Override
    public void deleteTimeSlot(String timeSlotId) {
        TIMESLOT_SERVICE_DATA.remove(timeSlotId);
    }

    @Override
    public void deleteAllTimeSlots() {
        TIMESLOT_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addTimeSlots(TimeSlot... timeSlots) {
        for (TimeSlot timeSlot : timeSlots) {
            TIMESLOT_SERVICE_DATA.put(timeSlot.timeSlotId, timeSlot);
        }
    }
}
