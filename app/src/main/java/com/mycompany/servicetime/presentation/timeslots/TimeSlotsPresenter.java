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

import static com.google.common.base.Preconditions.checkNotNull;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import com.mycompany.servicetime.CHApplication;
import com.mycompany.servicetime.base.usecase.UseCase;
import com.mycompany.servicetime.base.usecase.UseCaseHandler;
import com.mycompany.servicetime.data.source.TimeSlotDataSource;
import com.mycompany.servicetime.domain.usecase.ActivateTimeSlot;
import com.mycompany.servicetime.domain.usecase.DeleteTimeSlot;
import com.mycompany.servicetime.domain.usecase.GetTimeSlots;
import com.mycompany.servicetime.model.TimeSlot;
import com.mycompany.servicetime.presentation.addedittimeslot.AddEditTimeSlotActivity;

import java.util.List;

/**
 * Listens to user actions from the UI ({@link TimeSlotsFragment}), retrieves the data and updates the
 * UI as required.
 */
public class TimeSlotsPresenter implements TimeSlotsContract.Presenter {

    private final static int TIME_SLOTS_QUERY = 1;

    private final LoaderManager mLoaderManager;

    private final TimeSlotsContract.View mTimeSlotsView;

    private final ActivateTimeSlot mActivateTimeSlot;
    private final DeleteTimeSlot mDeleteTimeSlot;

    private boolean mFirstLoad = true;

    private final UseCaseHandler mUseCaseHandler;

    public TimeSlotsPresenter(@NonNull UseCaseHandler useCaseHandler,
                              @NonNull TimeSlotsContract.View timeSlotsView,
                              @NonNull ActivateTimeSlot activateTimeSlot,
                              @NonNull DeleteTimeSlot deleteTimeSlot,
                              @NonNull LoaderManager loaderManager) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTimeSlotsView = checkNotNull(timeSlotsView, "timeSlotsView cannot be null!");
        mActivateTimeSlot = checkNotNull(activateTimeSlot, "activateTimeSlot cannot be null!");
        mDeleteTimeSlot = checkNotNull(deleteTimeSlot, "deleteTimeSlot cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loader manager cannot be null");

        mTimeSlotsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTimeSlots(true);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a timeSlot was successfully added, show snackbar
        if (AddEditTimeSlotActivity.REQUEST_ADD_TIME_SLOT == requestCode && Activity.RESULT_OK == resultCode) {
            mTimeSlotsView.showSuccessfullySavedMessage();
        }
    }

    /**
     * @param showLoadingIndicator Pass in true to display a loading icon in the UI
     */
    @Override
    public void loadTimeSlots(final boolean showLoadingIndicator) {
        mLoaderManager.initLoader(TIME_SLOTS_QUERY, null,
                new TimeSlotsQueryLoaderCallbacks(CHApplication.getContext(), mTimeSlotsView, showLoadingIndicator));
    }

    @Override
    public void addNewTimeSlot() {
        mTimeSlotsView.showAddTimeSlotUI();
    }

    @Override
    public void openTimeSlotDetail(@NonNull String requestedTimeSlotId) {
        checkNotNull(requestedTimeSlotId, "requestedTimeSlotId cannot be null!");
        mTimeSlotsView.showEditTimeSlotUi(requestedTimeSlotId);
    }

    @Override
    public void activateTimeSlot(@NonNull TimeSlot activeTimeSlot) {
        checkNotNull(activeTimeSlot, "activeTimeSlot cannot be null!");
        mUseCaseHandler.execute(mActivateTimeSlot, new ActivateTimeSlot.RequestValues(activeTimeSlot.timeSlotId),
                new UseCase.UseCaseCallback<ActivateTimeSlot.ResponseValue>() {
                    @Override
                    public void onSuccess(ActivateTimeSlot.ResponseValue response) {
                        mTimeSlotsView.showTimeSlotMarkedActive();
                    }

                    @Override
                    public void onError() {
                        mTimeSlotsView.showLoadingTimeSlotsError();
                    }
                });
    }

    @Override
    public void deleteTimeSlot(@NonNull String requestedTimeSlotId) {
        mUseCaseHandler.execute(mDeleteTimeSlot, new DeleteTimeSlot.RequestValues(requestedTimeSlotId),
                new UseCase.UseCaseCallback<DeleteTimeSlot.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteTimeSlot.ResponseValue response) {
                        mTimeSlotsView.showTimeSlotDeleted();
                    }

                    @Override
                    public void onError() {
                        // TODO: Show error, log, etc.
                    }
                });
    }
}
