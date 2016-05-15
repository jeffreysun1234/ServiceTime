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
import com.mycompany.servicetime.data.source.model.TimeSlot;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes a {@link TimeSlot} from the {@link TimeSlotRepository}.
 */
public class DeleteTimeSlot extends UseCase<DeleteTimeSlot.RequestValues, DeleteTimeSlot.ResponseValue> {

    private final TimeSlotRepository mTimeSlotRepository;

    public DeleteTimeSlot(@NonNull TimeSlotRepository timeSlotRepository) {
        mTimeSlotRepository = checkNotNull(timeSlotRepository, "timeSlotRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        mTimeSlotRepository.deleteTimeSlot(values.getTimeSlotId());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mTimeSlotId;

        public RequestValues(@NonNull String timeSlotId) {
            mTimeSlotId = checkNotNull(timeSlotId, "timeSlotId cannot be null!");
        }

        public String getTimeSlotId() {
            return mTimeSlotId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue { }
}
