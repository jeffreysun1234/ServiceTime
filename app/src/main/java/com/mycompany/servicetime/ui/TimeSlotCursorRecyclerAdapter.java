package com.mycompany.servicetime.ui;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.mycompany.servicetime.R;
import com.mycompany.servicetime.model.TimeSlot;
import com.mycompany.servicetime.provider.CHServiceTimeContract;
import com.mycompany.servicetime.provider.ColumnIndexCache;
import com.mycompany.servicetime.util.DisplayUtils;
import com.mycompany.servicetime.util.ModelConverter;


/**
 * Created by szhx on 3/8/2016.
 */
public class TimeSlotCursorRecyclerAdapter extends RecyclerView.Adapter<TimeSlotCursorRecyclerAdapter.TimeSlotViewHolder> {

    private final OnItemClickOfRecycleListener mOnItemClickOfRecycleListener;

    public interface OnItemClickOfRecycleListener {
        void onItemLongClicked(String timeSlotId);

        void onActiveFlagSwitchClicked(String timeSlotId, boolean activeFlag);
    }

    Cursor mDataCursor;
    boolean mDataValid;
    /**
     * current id in cursor
     **/
    int mTimeSlotIdColumn;
    /**
     * observer the change of curror
     **/
    DataSetObserver mDataSetObserver;

    final ColumnIndexCache mColumnIndexCache = new ColumnIndexCache();

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        Switch activeSwitch;
        TextView nameTextView;
        TextView timeTextView;
        TextView daysTextView;
        TextView repeatWeeklyTextView;

        String currentTimeSlotId;
        OnItemClickOfRecycleListener onItemClickOfRecycleListener;

        public TimeSlotViewHolder(View itemView, final OnItemClickOfRecycleListener onItemClickOfRecycleListener) {
            super(itemView);
            this.onItemClickOfRecycleListener = onItemClickOfRecycleListener;

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            activeSwitch = (Switch) itemView.findViewById(R.id.activeSwitch);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            daysTextView = (TextView) itemView.findViewById(R.id.daysTextView);
            repeatWeeklyTextView = (TextView) itemView.findViewById(R.id.repeatWeeklyTextView);
        }

        public void bindData(@NonNull TimeSlot timeSlot) {
            this.nameTextView.setText(timeSlot.name);
            this.activeSwitch.setChecked(timeSlot.serviceFlag);
            this.timeTextView.setText(DisplayUtils.buildTimePeriodString(timeSlot.beginTimeHour,
                    timeSlot.beginTimeMinute, timeSlot.endTimeHour, timeSlot.endTimeMinute));
            this.daysTextView.setText(DisplayUtils.daysToText(timeSlot.days));
            this.repeatWeeklyTextView.setText(DisplayUtils.repeatFlagToText(timeSlot.repeatFlag));
            this.currentTimeSlotId = timeSlot.timeSlotId;
        }

        public void setListeners() {


            activeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickOfRecycleListener != null) {
                        onItemClickOfRecycleListener.onActiveFlagSwitchClicked(currentTimeSlotId, activeSwitch.isChecked());
                    }
                }
            });
        }
    }

    public TimeSlotCursorRecyclerAdapter(OnItemClickOfRecycleListener onItemClickOfRecycleListener, Cursor cursor) {
        this.mOnItemClickOfRecycleListener = onItemClickOfRecycleListener;
        this.mDataCursor = cursor;
        this.mDataValid = cursor != null;
        mTimeSlotIdColumn = mDataValid ? mDataCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mDataCursor != null) {
            mDataCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TimeSlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new TimeSlotViewHolder(itemView, mOnItemClickOfRecycleListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TimeSlotViewHolder holder, int position) {
        holder.bindData(getItem(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (mOnItemClickOfRecycleListener != null) {
                    mOnItemClickOfRecycleListener.onItemLongClicked(holder.currentTimeSlotId);
                }
                //return true;
            }
        });
        holder.setListeners();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mDataValid && mDataCursor != null) ? mDataCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mDataCursor != null && mDataCursor.moveToPosition(position)) {
            return mDataCursor.getLong(mTimeSlotIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em> closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mDataCursor) {
            return null;
        }
        final Cursor oldCursor = mDataCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mDataCursor = newCursor;
        if (mDataCursor != null) {
            if (mDataSetObserver != null) {
                mDataCursor.registerDataSetObserver(mDataSetObserver);
            }
            mTimeSlotIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mTimeSlotIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private TimeSlot getItem(int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mDataCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        TimeSlot timeSlot = ModelConverter.converteCursorToTimeSlotModel(mDataCursor, mColumnIndexCache);
        return timeSlot;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
