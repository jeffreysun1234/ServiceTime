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

package com.mycompany.servicetime.domain.usecase;

import static com.google.common.base.Preconditions.checkNotNull;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mycompany.servicetime.base.usecase.UseCase;
import com.mycompany.servicetime.data.source.TimeSlotDataSource;
import com.mycompany.servicetime.data.source.TimeSlotRepository;
import com.mycompany.servicetime.model.TimeSlot;

import java.util.List;

/**
 * Fetches the list of timeSlots.
 * It is replaced by a Loader. UI directly accesses DataSource using a loader.
 */
public class GetTimeSlots extends UseCase<GetTimeSlots.RequestValues, GetTimeSlots.ResponseValue> {

    private final TimeSlotRepository mTimeSlotRepository;

    public GetTimeSlots(@NonNull TimeSlotRepository timeSlotRepository) {
        mTimeSlotRepository = checkNotNull(timeSlotRepository, "timeSlotsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {

        mTimeSlotRepository.getTimeSlots(new TimeSlotDataSource.LoadTimeSlotsCallback() {
            @Override
            public void onTimeSlotsLoaded(Cursor timeSlots) {
                ResponseValue responseValue = new ResponseValue(timeSlots);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static final class RequestValues implements UseCase.RequestValues {
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Cursor mTimeSlots;

        public ResponseValue(@NonNull Cursor timeSlots) {
            mTimeSlots = checkNotNull(timeSlots, "timeSlots cannot be null!");
        }

        public Cursor getTimeSlots() {
            return mTimeSlots;
        }
    }
}
