package com.mycompany.servicetime.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        mAdapter = new CustomSimpleCursorAdapter(getContext(), R.layout.list_item,
                null, TimeSlots.DEFAULT_PROJECTION, null, 0);

        mListView.setAdapter(mAdapter);
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
