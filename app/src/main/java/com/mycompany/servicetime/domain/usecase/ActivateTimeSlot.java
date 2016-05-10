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

import android.support.annotation.NonNull;

import com.mycompany.servicetime.base.usecase.UseCase;
import com.mycompany.servicetime.data.source.TimeSlotRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Marks a timeSlot as active (not completed yet).
 */
public class ActivateTimeSlot extends UseCase<ActivateTimeSlot.RequestValues, ActivateTimeSlot.ResponseValue> {

    private final TimeSlotRepository mTimeSlotRepository;

    public ActivateTimeSlot(@NonNull TimeSlotRepository timeSlotsRepository) {
        mTimeSlotRepository = checkNotNull(timeSlotsRepository, "timeSlotsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        String timeSlotId = values.getTimeSlotId();
        boolean activateFlag = values.getActivateFlag();
        mTimeSlotRepository.activateTimeSlot(timeSlotId, activateFlag);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String mTimeSlotId;
        private final boolean mActivateFlag;

        public RequestValues(@NonNull String activateTimeSlot, boolean activateFlag) {
            mTimeSlotId = checkNotNull(activateTimeSlot, "activateTimeSlot cannot be null!");
            mActivateFlag = activateFlag;
        }

        public String getTimeSlotId() {
            return mTimeSlotId;
        }

        public boolean getActivateFlag() {
            return mActivateFlag;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
