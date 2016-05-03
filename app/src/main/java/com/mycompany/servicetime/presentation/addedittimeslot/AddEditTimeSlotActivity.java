package com.mycompany.servicetime.presentation.addedittimeslot;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mycompany.servicetime.Injection;
import com.mycompany.servicetime.R;
import com.mycompany.servicetime.util.ActivityUtils;
import com.mycompany.servicetime.util.EspressoIdlingResource;

public class AddEditTimeSlotActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TIME_SLOT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_time_slot);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        AddEditTimeSlotFragment addEditTimeSlotFragment =
                (AddEditTimeSlotFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String timeSlotId = null;
        if (addEditTimeSlotFragment == null) {
            if (getIntent().hasExtra(AddEditTimeSlotFragment.ARGUMENT_EDIT_TIME_SLOT_ID)) {
                timeSlotId = getIntent().getStringExtra(AddEditTimeSlotFragment.ARGUMENT_EDIT_TIME_SLOT_ID);
                actionBar.setTitle(R.string.edit_timeSlot);
            } else {
                actionBar.setTitle(R.string.add_timeSlot);
            }
            addEditTimeSlotFragment = AddEditTimeSlotFragment.newInstance(timeSlotId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTimeSlotFragment, R.id.contentFrame);
        }

        // Create the presenter
        new AddEditTimeSlotPresenter(Injection.provideUseCaseHandler(),
                timeSlotId,
                addEditTimeSlotFragment,
                Injection.provideGetTimeSlot(getApplicationContext()),
                Injection.provideSaveTimeSlot(getApplicationContext())
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
