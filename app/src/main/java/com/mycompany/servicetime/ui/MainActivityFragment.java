package com.mycompany.servicetime.ui;

import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Toast;

import com.mycompany.servicetime.R;
import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    ListView mListView;
    SimpleCursorAdapter mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        //initViewListeners();

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    private void initViews() {
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
