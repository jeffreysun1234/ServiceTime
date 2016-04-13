package com.mycompany.servicetime.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = makeLogTag(AlarmReceiver.class);

    public AlarmReceiver() {
    }

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp ;
        if (InitAlarmIntentService.ACTION_INIT.equals(intent.getAction())) {
            comp = new ComponentName(context.getPackageName(),
                    InitAlarmIntentService.class.getName());
        } else {
            // If your receiver intent includes extras that need to be passed along to the service,
            // use setComponent() to indicate that the service should handle the receiver's intent.
            comp = new ComponentName(context.getPackageName(),
                    RingerModeIntentService.class.getName());
        }
        // This intent passed in this call will include the wake lock extra as well as
        // the receiver intent contents.
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
    }

    public void setAlarm(Context context, boolean silentFlag, long timePoint) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        if (silentFlag)
            intent.setAction(RingerModeIntentService.ACTION_SET_RINGER_MODE_VIBRATE);
        else
            intent.setAction(RingerModeIntentService.ACTION_SET_RINGER_MODE_NORMAL);
        alarmIntent = PendingIntent
                .getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the alarm to fire at approximately a time point, according to the device's
        // clock.
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timePoint, alarmIntent);

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the alarm.
     *
     * @param context
     */
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Sets a repeating alarm that runs once a day at approximately 0:01 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     *
     * @param context
     */
    public void InitAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(InitAlarmIntentService.ACTION_INIT);
        alarmIntent = PendingIntent
                .getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 00:01 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 01);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}
