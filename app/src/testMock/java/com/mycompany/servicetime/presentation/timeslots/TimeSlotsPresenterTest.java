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

import com.mycompany.servicetime.base.usecase.TestUseCaseScheduler;
import com.mycompany.servicetime.base.usecase.UseCaseHandler;
import com.mycompany.servicetime.data.source.TimeSlotDataSource;
import com.mycompany.servicetime.data.source.TimeSlotRepository;
import com.mycompany.servicetime.domain.usecase.ActivateTimeSlot;
import com.mycompany.servicetime.domain.usecase.GetTimeSlots;
import com.mycompany.servicetime.model.TimeSlot;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the implementation of {@link TimeSlotsPresenter}
 */
public class TimeSlotsPresenterTest {

    private static List<TimeSlot> TIMESLOTS;

    @Mock
    private TimeSlotRepository mTimeSlotRepository;

    @Mock
    private TimeSlotsContract.View mTimeSlotsView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<TimeSlotDataSource.LoadTimeSlotsCallback> mLoadTimeSlotsCallbackCaptor;

    private TimeSlotsPresenter mTimeSlotsPresenter;

    @Before
    public void setupTimeSlotsPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mTimeSlotsPresenter = givenTimeSlotsPresenter();

        // The presenter won't update the view unless it's active.
        when(mTimeSlotsView.isActive()).thenReturn(true);

        // We start the timeSlots to 3, with one active and two completed
        TIMESLOTS = new ArrayList<TimeSlot>();
        TIMESLOTS.add(new TimeSlot("1", "Work", 9, 0, 17, 0, "0111110", true));
        TIMESLOTS.add(new TimeSlot("2", "Test", 11, 0, 13, 0, "0110000", true));
        TIMESLOTS.add(new TimeSlot("3", "School", 8, 0, 15, 30, "0111110", true));
    }

    private TimeSlotsPresenter givenTimeSlotsPresenter() {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTimeSlots getTimeSlots = new GetTimeSlots(mTimeSlotRepository);
        ActivateTimeSlot activateTimeSlot = new ActivateTimeSlot(mTimeSlotRepository);

        //return new TimeSlotsPresenter(useCaseHandler, mTimeSlotsView, getTimeSlots, activateTimeSlot);
        return null;
    }

    @Test
    public void loadAllTimeSlotsFromRepositoryAndLoadIntoView() {
        // Given an initialized TimeSlotsPresenter with initialized timeSlots
        // When loading of TimeSlots is requested
        mTimeSlotsPresenter.loadTimeSlots(true);

        // Callback is captured and invoked with stubbed timeSlots
        verify(mTimeSlotRepository).getTimeSlots(mLoadTimeSlotsCallbackCaptor.capture());
        mLoadTimeSlotsCallbackCaptor.getValue().onTimeSlotsLoaded(TIMESLOTS);

        // Then progress indicator is shown
        verify(mTimeSlotsView).setLoadingIndicator(true);
        // Then progress indicator is hidden and all timeSlots are shown in UI
        verify(mTimeSlotsView).setLoadingIndicator(false);
        ArgumentCaptor<List> showTimeSlotsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        //verify(mTimeSlotsView).showTimeSlots(showTimeSlotsArgumentCaptor.capture());
        assertTrue(showTimeSlotsArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void clickAddIcon_ShowsAddTimeSlotUi() {
        // When adding a new timeSlot
        mTimeSlotsPresenter.addNewTimeSlot();

        // Then add timeSlot UI is shown
        verify(mTimeSlotsView).showAddTimeSlotUI();
    }

    @Test
    public void clickEditIconOnTimeSlot_ShowsEditTimeSlotUi() {
        // Given a stubbed active timeSlot
        TimeSlot requestedTimeSlot = new TimeSlot("2", "Test", 11, 0, 13, 0, "0110000", true);

        // When open timeSlot details is requested
        //mTimeSlotsPresenter.openTimeSlotDetail(requestedTimeSlot);

        // Then timeSlot detail UI is shown
        //verify(mTimeSlotsView).showEditTimeSlotUi(any(TimeSlot.class));
    }

    @Test
    public void activateTimeSlot_ShowsTimeSlotMarkedActive() {
        // Given a stubbed completed timeSlot
        TimeSlot timeSlot =  new TimeSlot("2", "Test", 11, 0, 13, 0, "0110000", true);
        mTimeSlotsPresenter.loadTimeSlots(true);

        // When timeSlot is marked as activated
        mTimeSlotsPresenter.activateTimeSlot(timeSlot);

        // Then repository is called and timeSlot marked active UI is shown
        verify(mTimeSlotRepository).activateTimeSlot(eq(timeSlot.timeSlotId));
        verify(mTimeSlotsView).showTimeSlotMarkedActive();
    }

    @Test
    public void unavailableTimeSlots_ShowsError() {
        // When timeSlots are loaded
        mTimeSlotsPresenter.loadTimeSlots(true);

        // And the timeSlots aren't available in the repository
        verify(mTimeSlotRepository).getTimeSlots(mLoadTimeSlotsCallbackCaptor.capture());
        mLoadTimeSlotsCallbackCaptor.getValue().onDataNotAvailable();

        // Then an error message is shown
        verify(mTimeSlotsView).showLoadingTimeSlotsError();
    }
}
