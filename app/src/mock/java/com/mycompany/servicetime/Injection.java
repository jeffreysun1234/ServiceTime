package com.mycompany.servicetime;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mycompany.servicetime.base.usecase.UseCaseHandler;
import com.mycompany.servicetime.data.source.FakeTimeSlotDataSource;
import com.mycompany.servicetime.data.source.TimeSlotDataSource;
import com.mycompany.servicetime.data.source.TimeSlotRepository;
import com.mycompany.servicetime.domain.usecase.ActivateTimeSlot;
import com.mycompany.servicetime.domain.usecase.GetTimeSlot;
import com.mycompany.servicetime.domain.usecase.GetTimeSlots;
import com.mycompany.servicetime.domain.usecase.SaveTimeSlot;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link TimeSlotDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public abstract class Injection {

    public static TimeSlotRepository provideTimeSlotsRepository(@NonNull Context context) {
        checkNotNull(context);
        return TimeSlotRepository.getInstance(FakeTimeSlotDataSource.getInstance());
    }

    public static GetTimeSlots provideGetTimeSlots(@NonNull Context context) {
        return new GetTimeSlots(provideTimeSlotsRepository(context));
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetTimeSlot provideGetTimeSlot(@NonNull Context context) {
        return new GetTimeSlot(Injection.provideTimeSlotsRepository(context));
    }

    public static SaveTimeSlot provideSaveTimeSlot(@NonNull Context context) {
        return new SaveTimeSlot(Injection.provideTimeSlotsRepository(context));
    }

    public static ActivateTimeSlot provideActivateTimeSlot(@NonNull Context context) {
        return new ActivateTimeSlot(Injection.provideTimeSlotsRepository(context));
    }

    //
//    public static DeleteTimeSlot provideDeleteTimeSlot(@NonNull Context context) {
//        return new DeleteTimeSlot(Injection.provideTimeSlotsRepository(context));
//    }
}
