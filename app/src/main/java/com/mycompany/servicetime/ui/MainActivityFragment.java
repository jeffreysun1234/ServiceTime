package com.mycompany.servicetime.ui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mycompany.servicetime.firebase.FirebaseConstants;
import com.mycompany.servicetime.R;
import com.mycompany.servicetime.firebase.FirebaseDAO;
import com.mycompany.servicetime.firebase.FirebaseRestDAO;
import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;
import com.mycompany.servicetime.firebase.FirebaseEndpointInterface;
import com.mycompany.servicetime.schedule.InitAlarmIntentService;
import com.mycompany.servicetime.support.PreferenceSupport;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    ListView mListView;
    SimpleCursorAdapter mAdapter;
    TextView mNextAlarmTextView;
    SharedPreferences sp;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initViews() {
        mNextAlarmTextView = (TextView) getActivity().findViewById(R.id.nextOperationTextView);

        mListView = (ListView) getActivity().findViewById(R.id.listView);
        mListView.setEmptyView(getActivity().findViewById(android.R.id.empty));

        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mAdapter = new CustomSimpleCursorAdapter(getContext(), R.layout.list_item,
                null, TimeSlots.DEFAULT_PROJECTION, null, 0);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long
                    id) {
                Cursor cursor = (Cursor) mListView.getItemAtPosition(position);
                String timeSlotId = cursor.getString(cursor.getColumnIndex(TimeSlots.TIME_SLOT_ID));
                TimeSlotFragment timeSlotFragment = TimeSlotFragment.newInstance(timeSlotId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, timeSlotFragment)
                        .addToBackStack(null)
                        .commit();

                return true;
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.time_slot_list_add: {
                TimeSlotFragment timeSlotFragment = new TimeSlotFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, timeSlotFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            case R.id.backup_time_slot_list: {
                FirebaseRestDAO.create().backupTimeSlotList();

                Toast.makeText(getContext(), "Backup done.", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.restore_time_slot_list: {


//                Call<String> message = service.getTest();
//                message.enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        if (response.isSuccessful()) {
//                            Toast.makeText(getContext(), response.body(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            // error response, no access to resource?
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//
//                    }
//                });


//                Firebase myFirebaseRef = new Firebase(FirebaseConstants.FIREBASE_URL);
//                myFirebaseRef.child("message").addValueEventListener(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        Toast.makeText(getContext(), (String) snapshot.getValue(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancelled(FirebaseError error) {
//                    }
//
//                });
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), TimeSlots.CONTENT_URI,
                TimeSlots.DEFAULT_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        // Send the open and close sound alarms based on the current data.
        InitAlarmIntentService.startActionInit(getContext());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceSupport.NEXT_ALARM_DETAIL)) {
            mNextAlarmTextView.setText(PreferenceSupport.getNextAlarmDetail(getContext()));
        }

    }
}
