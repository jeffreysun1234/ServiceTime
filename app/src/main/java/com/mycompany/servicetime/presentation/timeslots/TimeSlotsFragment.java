package com.mycompany.servicetime.presentation.timeslots;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycompany.servicetime.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class TimeSlotsFragment extends Fragment {

    public TimeSlotsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_slots, container, false);
    }
}
