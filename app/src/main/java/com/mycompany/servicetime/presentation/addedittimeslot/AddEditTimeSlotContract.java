package com.mycompany.servicetime.presentation.addedittimeslot;

import com.mycompany.servicetime.base.presentation.BasePresenter;
import com.mycompany.servicetime.base.presentation.BaseView;
import com.mycompany.servicetime.data.source.model.TimeSlot;

/**
 * Created by szhx on 5/1/2016.
 * <p/>
 * This specifies the contract between the view and the presenter.
 */
public class AddEditTimeSlotContract {
    interface View extends BaseView<Presenter> {

        void showError(int error);

        void finishView();

        void setTimeSlotFields(TimeSlot timeSlot);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void createTimeSlot(String name, int beginTimeHour, int beginTimeMinute,
                            int endTimeHour, int endTimeMinute, String days, boolean repeatFlag);

        void updateTimeSlot(String name, int beginTimeHour, int beginTimeMinute,
                            int endTimeHour, int endTimeMinute, String days, boolean repeatFlag);

        void populateTimeSlot();
    }
}
