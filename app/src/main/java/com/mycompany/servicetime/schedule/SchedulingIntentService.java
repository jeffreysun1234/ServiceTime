package com.mycompany.servicetime.schedule;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.format.DateUtils;

import com.mycompany.servicetime.R;
import com.mycompany.servicetime.provider.CHServiceTimeDAO;
import com.mycompany.servicetime.support.PreferenceSupport;
import com.mycompany.servicetime.support.TimeSlotSupport;
import com.mycompany.servicetime.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SchedulingIntentService extends IntentService {
    public static final String TAG = makeLogTag(SchedulingIntentService.class);

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SET_ALARM =
            "com.mycompany.servicetime.schedule.action.SET_ALARM";
    private static final String ACTION_STOP_ALARM =
            "com.mycompany.servicetime.schedule.action.STOP_ALARM";
    private static final String ACTION_INIT_ALARM =
            "com.mycompany.servicetime.schedule.action.INIT_ALARM";

    public static final String EXTRA_SILENT_FLAG = "com.mycompany.servicetime.schedule.extra" +
            ".SILENT_FLAG";

    public SchedulingIntentService() {
        super("SchedulingIntentService");
    }

    public static void startActionInitAlarm(Context context) {
        Intent intent = new Intent(context, SchedulingIntentService.class);
        intent.setAction(ACTION_INIT_ALARM);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action "Set Alarm" with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSetAlarm(Context context, boolean silentFlag) {
        Intent intent = new Intent(context, SchedulingIntentService.class);
        intent.setAction(ACTION_SET_ALARM);
        intent.putExtra(EXTRA_SILENT_FLAG, silentFlag);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action "Stop Alarm" with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionStopAlarm(Context context) {
        Intent intent = new Intent(context, SchedulingIntentService.class);
        intent.setAction(ACTION_STOP_ALARM);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            LOGD(TAG, "SchedulingIntentService Action=" + action);

            if (ACTION_SET_ALARM.equals(action)) {
                final boolean silentFlag = intent.getBooleanExtra(EXTRA_SILENT_FLAG, true);
                handleActionSetAlarm(silentFlag);
            } else if (ACTION_STOP_ALARM.equals(action)) {
                handleActionStopAlarm();
            } else if (ACTION_INIT_ALARM.equals(action)) {
                handleActionInitAlarm();
            }
        }
    }

    private void handleActionInitAlarm() {
        new AlarmReceiver().InitAlarm(getApplicationContext());
    }

    /**
     * Handle action "Set Alarm" in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSetAlarm(boolean silentFlag) {
        long timePoint = 0;
        try {
            timePoint = TimeSlotSupport.getNextAlarmTime(
                    CHServiceTimeDAO.create(getApplicationContext()).getNextAlarmTime(silentFlag),
                    silentFlag, DateUtil.getHHmm(System.currentTimeMillis()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            LOGD(TAG, "Set Alarm: timePoint=" + timePoint + "[" + DateUtil.format(timePoint)
                    + "], silentFlag = " + silentFlag);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // save alarm text for display.
        String alarmText;
        if (timePoint != 0) {
            new AlarmReceiver().setAlarm(getApplicationContext(), silentFlag, timePoint);

            StringBuffer sb = new StringBuffer();
            if (silentFlag) {
                sb.append("Vibrate");
            } else {
                sb.append("Sound");
            }
            sb.append(" setting at ")
                    .append(new SimpleDateFormat("MMM dd, HH:mm 'on' EEE").format(timePoint));
            alarmText = sb.toString();
        } else {
            alarmText = getString(R.string.next_operation_no);
        }
        PreferenceSupport.setNextAlarmDetail(getApplicationContext(), alarmText);

    }

    /**
     * Handle action "Stop Alarm" in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStopAlarm() {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
