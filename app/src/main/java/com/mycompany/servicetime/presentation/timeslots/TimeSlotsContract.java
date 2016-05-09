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

package com.mycompany.servicetime.presentation.timeslots;

import android.support.annotation.NonNull;

import com.mycompany.servicetime.base.presentation.BasePresenter;
import com.mycompany.servicetime.base.presentation.BaseView;
import com.mycompany.servicetime.model.TimeSlot;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface TimeSlotsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTimeSlots(List<TimeSlot> timeSlots);

        void showAddTimeSlotUI();

        void showEditTimeSlotUi(String timeSlotId);

        void showTimeSlotMarkedActive();

        void showLoadingTimeSlotsError();

        void showNoTimeSlots();

        void showSuccessfullySavedMessage();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTimeSlots(boolean showLoadingIndicator);

        void addNewTimeSlot();

        void openTimeSlotDetail(@NonNull String requestedTimeSlotId);

        void activateTimeSlot(@NonNull TimeSlot activeTimeSlot);
    }
}
