package com.mycompany.servicetime.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * Created by szhx on 4/14/2016.
 */
public class TimeSlotItemTouchCallback extends ItemTouchHelper.SimpleCallback {
    private static final String TAG = makeLogTag(TimeSlotItemTouchCallback.class);

    public TimeSlotItemTouchCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Toast.makeText(viewHolder.itemView.getContext(), "Swrop....", Toast.LENGTH_SHORT);

        // Remove the swiped item.

        // Tell the adapter that we removed the item
        //recyclerView.getAdapter().notifyItemRemoved(position);

    }
}
