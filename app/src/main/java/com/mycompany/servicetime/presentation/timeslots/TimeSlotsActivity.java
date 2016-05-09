package com.mycompany.servicetime.presentation.timeslots;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mycompany.servicetime.Injection;
import com.mycompany.servicetime.R;
import com.mycompany.servicetime.util.ActivityUtils;
import com.mycompany.servicetime.util.EspressoIdlingResource;

public class TimeSlotsActivity extends AppCompatActivity {

    private TimeSlotsPresenter mTimeSlotsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slots);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TimeSlotsFragment timeSlotsFragment =
                (TimeSlotsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (timeSlotsFragment == null) {
            // Create the fragment
            timeSlotsFragment = TimeSlotsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), timeSlotsFragment, R.id.fragment);
        }

        // Create the presenter
        mTimeSlotsPresenter = new TimeSlotsPresenter(
                Injection.provideUseCaseHandler(),
                timeSlotsFragment,
                Injection.provideActivateTimeSlot(getApplicationContext()),
                Injection.provideDeleteTimeSlot(getApplicationContext()),
                getSupportLoaderManager()
        );

        // Add AdView
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                //.addTestDevice(getString(R.string.ad_test_device_id))
//                .build();
//        mAdView.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

}
