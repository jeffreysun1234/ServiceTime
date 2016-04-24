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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.servicetime.CHApplication;
import com.mycompany.servicetime.R;
import com.mycompany.servicetime.firebase.FirebaseRestDAO;
import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;
import com.mycompany.servicetime.provider.CHServiceTimeDAO;
import com.mycompany.servicetime.schedule.InitAlarmIntentService;
import com.mycompany.servicetime.support.PreferenceSupport;

import java.io.IOException;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements
        TimeSlotCursorRecyclerAdapter.ItemActionListener,
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = makeLogTag(MainActivityFragment.class);

    RecyclerView mRecyclerView;
    LinearLayout mEmptyView;
    TimeSlotCursorRecyclerAdapter mAdapter;
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
        mEmptyView = (LinearLayout) getActivity().findViewById(R.id.empty_layout);
        mNextAlarmTextView = (TextView) getActivity().findViewById(R.id.nextOperationTextView);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.timeSlotListRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(getLayoutManager());

        mAdapter = new TimeSlotCursorRecyclerAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        // add item animation
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        mRecyclerView.setItemAnimator(itemAnimator);
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
                backupTimeSlotList();
                return true;
            }
            case R.id.restore_time_slot_list: {
                restoreTimeSlotList();
                return true;
            }
            default:
                return false;
        }
    }

    private void backupTimeSlotList() {
        if (((BaseActivity) getActivity()).isLogin) {
            new AccessFirebaseAsyn(getContext(), new AccessFirebaseAsyn.BackgroundAction() {

                @Override
                public void doActionInBackground() {
                    try {
                        String encodedUserEmail = PreferenceSupport.getEncodedEmail(getContext());
                        String authToken = PreferenceSupport.getAuthToken(getContext());

                        FirebaseRestDAO.create().backupTimeSlotItemList(
                                encodedUserEmail,
                                authToken,
                                CHServiceTimeDAO.create(getContext()).backupAllTimeSlots());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void doOnPostExecute() {
                    Toast.makeText(getContext(), "Backup done.", Toast.LENGTH_SHORT).show();
                }
            }).execute();
        } else {
            ((BaseActivity) getActivity()).showLoginHint();
        }
    }

    private void restoreTimeSlotList() {
        if (((BaseActivity) getActivity()).isLogin) {
            new AccessFirebaseAsyn(getContext(), new AccessFirebaseAsyn.BackgroundAction() {

                @Override
                public void doActionInBackground() {
                    try {
                        String encodedUserEmail = PreferenceSupport.getEncodedEmail(getContext());
                        String authToken = PreferenceSupport.getAuthToken(getContext());
                        CHServiceTimeDAO.create(getContext()).restoreAllTimeSlots(
                                FirebaseRestDAO.create().restoreTimeSlotItemList(
                                        encodedUserEmail, authToken));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void doOnPostExecute() {
                    Toast.makeText(getContext(), "Restore done.", Toast.LENGTH_SHORT).show();
                }
            }).execute();
        } else {
            ((BaseActivity) getActivity()).showLoginHint();
        }
    }

    /******
     * Cursor loader interface's implements
     ******/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), TimeSlots.CONTENT_URI,
                TimeSlots.DEFAULT_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //mAdapter.swapCursor(data);
        mAdapter.changeCursor(data);

        // check if show empty view
        checkAdapterIsEmpty();

        // Send the open and close sound alarms based on the current data.
        InitAlarmIntentService.startActionInit(getContext());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /******
     * Preference Listener's method
     ******/
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceSupport.NEXT_ALARM_DETAIL)) {
            mNextAlarmTextView.setText(PreferenceSupport.getNextAlarmDetail(getContext()));
        }

    }

    /******
     * Custom RecyclerAdapter Callback interface's implements
     ******/
    @Override
    public void onItemLongClicked(String timeSlotId) {
        LOGD(TAG, "onItemLongClicked(): timeSlotId=" + timeSlotId);

        TimeSlotFragment timeSlotFragment = TimeSlotFragment.newInstance(timeSlotId);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, timeSlotFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onActiveFlagSwitchClicked(String timeSlotId, boolean activeFlag) {
        LOGD(TAG, "onActiveFlagSwitchClicked(): timeSlotId=" + timeSlotId + " ; activeFlag=" + activeFlag);
        CHServiceTimeDAO.create(CHApplication.getContext()).updateServiceFlag(timeSlotId, activeFlag);
    }

    @Override
    public void deleteItem(String timeSlotId) {
        LOGD(TAG, "deleteItem(): timeSlotId=" + timeSlotId );
        CHServiceTimeDAO.create(CHApplication.getContext()).deleteTimeSlot(timeSlotId);
    }

    // Here is the method we extract to override in our testable subclass
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    /**
     * show an empty view with a RecyclerView
     */
    private void checkAdapterIsEmpty() {
        if (mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

}
