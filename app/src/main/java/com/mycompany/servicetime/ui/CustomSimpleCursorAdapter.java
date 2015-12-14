package com.mycompany.servicetime.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.servicetime.R;
import com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;
import com.mycompany.servicetime.provider.CHServiceTimeDAO;
import com.mycompany.servicetime.schedule.SchedulingIntentService;

import java.util.Arrays;

/**
 * Created by szhx on 12/12/2015.
 */
public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int mLayout;
    private String[] mFrom;
    private int[] mTo;

    private class ViewHolder {
        Switch activeSwitch;
        TextView nameTextView;
        TextView timeTextView;
        TextView daysTextView;
        TextView repeatWeeklyTextView;

        String currentTimeSlotId;

        ViewHolder(View v) {
            nameTextView = (TextView) v.findViewById(R.id.nameTextView);
            activeSwitch = (Switch) v.findViewById(R.id.activeSwitch);
            timeTextView = (TextView) v.findViewById(R.id.timeTextView);
            daysTextView = (TextView) v.findViewById(R.id.daysTextView);
            repeatWeeklyTextView = (TextView) v.findViewById(R.id.repeatWeeklyTextView);

            activeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean vSilentFlag = activeSwitch.isChecked();
                    CHServiceTimeDAO.create(mContext)
                            .updateServiceFlag(currentTimeSlotId, vSilentFlag);
                    SchedulingIntentService.startActionSetAlarm(mContext, vSilentFlag);
                }
            });
        }
    }

    public CustomSimpleCursorAdapter(Context context, int layout,
                                     Cursor c,
                                     String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.mContext = context;
        this.mLayout = layout;
        this.mFrom = from;
        this.mTo = to;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context ctx, Cursor cursor, ViewGroup parent) {
        View vView = mLayoutInflater.inflate(R.layout.list_item, parent, false);
        vView.setTag(new ViewHolder(vView));
        // no need to bind data here. you do in later
        return vView;
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cursor) {

        String name = cursor.getString(cursor.getColumnIndex(TimeSlots.NAME));
        boolean serviceFlag = cursor.getInt(cursor.getColumnIndex(TimeSlots.SERVICE_FLAG)) == 1 ?
                true : false;
        String time = cursor.getString(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_HOUR))
                + ":"
                + cursor.getString(cursor.getColumnIndex(TimeSlots.BEGIN_TIME_MINUTE))
                + " --- "
                + cursor.getString(cursor.getColumnIndex(TimeSlots.END_TIME_HOUR))
                + ":"
                + cursor.getString(cursor.getColumnIndex(TimeSlots.END_TIME_MINUTE));
        String days = cursor.getString(cursor.getColumnIndex(TimeSlots.DAYS));
        String repeatFlag = cursor.getInt(cursor.getColumnIndex(TimeSlots.REPEAT_FLAG)) == 1
                ? "Repeat weekly" : "";


        ViewHolder vh = (ViewHolder) view.getTag();

        vh.nameTextView.setText(name);
        vh.activeSwitch.setChecked(serviceFlag);
        vh.timeTextView.setText(time);
        vh.daysTextView.setText(daysToText(days));
        vh.repeatWeeklyTextView.setText(repeatFlag);
        vh.currentTimeSlotId = cursor.getString(cursor.getColumnIndex(TimeSlots.TIME_SLOT_ID));
    }

    private String daysToText(String days) {
        String[] weekText = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        StringBuffer daysText = new StringBuffer();
        for (int i = 0; i < days.length(); i++) {
            if (Character.getNumericValue(days.charAt(i)) == 1)
                daysText.append(weekText[i]).append(", ");
        }
        if (daysText.length() > 0)
            daysText.deleteCharAt(daysText.length() - 2);

        return daysText.toString();
    }
}
