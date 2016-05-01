package com.mycompany.servicetime.presentation.addedittimeslot;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mycompany.servicetime.base.usecase.UseCase;
import com.mycompany.servicetime.base.usecase.UseCaseHandler;
import com.mycompany.servicetime.domain.usecase.GetTimeSlot;
import com.mycompany.servicetime.domain.usecase.SaveTimeSlot;
import com.mycompany.servicetime.model.TimeSlot;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by szhx on 5/1/2016.
 */
public class AddEditTimeSlotPresenter implements AddEditTimeSlotContract.Presenter {

    private final AddEditTimeSlotContract.View mAddTimeSlotView;

    private final GetTimeSlot mGetTimeSlot;

    private final SaveTimeSlot mSaveTimeSlot;

    private final UseCaseHandler mUseCaseHandler;

    @Nullable
    private String mTimeSlotId;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param timeSlotId      ID of the timeSlot to edit or null for a new timeSlot
     * @param addTimeSlotView the add/edit view
     */
    public AddEditTimeSlotPresenter(@NonNull UseCaseHandler useCaseHandler, @Nullable String timeSlotId,
                                    @NonNull AddEditTimeSlotContract.View addTimeSlotView,
                                    @NonNull GetTimeSlot getTimeSlot,
                                    @NonNull SaveTimeSlot saveTimeSlot) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mTimeSlotId = timeSlotId;
        mAddTimeSlotView = checkNotNull(addTimeSlotView, "addTimeSlotView cannot be null!");
        mGetTimeSlot = checkNotNull(getTimeSlot, "getTimeSlot cannot be null!");
        mSaveTimeSlot = checkNotNull(saveTimeSlot, "saveTimeSlot cannot be null!");

        mAddTimeSlotView.setPresenter(this);
    }

    @Override
    public void createTimeSlot(String name, int beginTimeHour, int beginTimeMinute,
                               int endTimeHour, int endTimeMinute, String days, boolean repeatFlag) {
        TimeSlot newTimeSlot = new TimeSlot(name, beginTimeHour, beginTimeMinute, endTimeHour, endTimeMinute,
                days, repeatFlag);

        String verifyTimeSlotResult = verifyTimeSlot(newTimeSlot);
        // no error information means verify success.
        if (verifyTimeSlotResult != null && verifyTimeSlotResult.equals("")) {
            mUseCaseHandler.execute(mSaveTimeSlot, new SaveTimeSlot.RequestValues(newTimeSlot),
                    new UseCase.UseCaseCallback<SaveTimeSlot.ResponseValue>() {
                        @Override
                        public void onSuccess(SaveTimeSlot.ResponseValue response) {
                            mAddTimeSlotView.finishView();
                        }

                        @Override
                        public void onError() {
                            showSaveError();
                        }
                    });
        } else {
            showVerifyTimeSlotError(verifyTimeSlotResult);
        }
    }


    @Override
    public void updateTimeSlot(String name, int beginTimeHour, int beginTimeMinute,
                               int endTimeHour, int endTimeMinute, String days, boolean repeatFlag) {
        if (mTimeSlotId == null) {
            throw new RuntimeException("updateTimeSlot() was called but timeSlot is new.");
        }
        TimeSlot newTimeSlot = new TimeSlot(mTimeSlotId, name, beginTimeHour, beginTimeMinute,
                endTimeHour, endTimeMinute, days, repeatFlag);
        mUseCaseHandler.execute(mSaveTimeSlot, new SaveTimeSlot.RequestValues(newTimeSlot),
                new UseCase.UseCaseCallback<SaveTimeSlot.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveTimeSlot.ResponseValue response) {
                        // After an edit, go back to the list.
                        mAddTimeSlotView.finishView();
                    }

                    @Override
                    public void onError() {
                        showSaveError();
                    }
                });
    }

    @Override
    public void populateTimeSlot() {
        if (mTimeSlotId == null) {
            throw new RuntimeException("populateTimeSlot() was called but timeSlot is new.");
        }

        mUseCaseHandler.execute(mGetTimeSlot, new GetTimeSlot.RequestValues(mTimeSlotId),
                new UseCase.UseCaseCallback<GetTimeSlot.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTimeSlot.ResponseValue response) {
                        showTimeSlot(response.getTimeSlot());
                    }

                    @Override
                    public void onError() {
                        showVerifyTimeSlotError("Fail to get TimeSlot List");
                    }
                });
    }

    private String verifyTimeSlot(TimeSlot timeSlot) {
        if (timeSlot == null) return null;

        if (timeSlot.name == null || timeSlot.name.equals("")) {
            return "You must input a Name.";
        }
        if (timeSlot.beginTimeHour * 100 + timeSlot.beginTimeMinute >=
                timeSlot.endTimeHour * 100 + timeSlot.endTimeMinute) {
            return "Begin Time must be less than End Time.";
        }

        if (timeSlot.days == null || "0000000".equals(timeSlot.days)) {
            return "You must set at least one day.";
        }
        return "";
    }

    @Override
    public void start() {
        if (mTimeSlotId != null) {
            populateTimeSlot();
        }
    }

    private void showTimeSlot(TimeSlot timeSlot) {
        // The view may not be able to handle UI updates anymore
        if (mAddTimeSlotView.isActive()) {
            mAddTimeSlotView.setTimeSlotFields(timeSlot);
        }
    }
    
    private void showSaveError() {
        //TODO: Show error, log, etc.
    }

    private void showVerifyTimeSlotError(String error) {
        // The view may not be able to handle UI updates anymore
        if (mAddTimeSlotView.isActive()) {
            mAddTimeSlotView.showVerifyTimeSlotError(error);
        }
    }
}
