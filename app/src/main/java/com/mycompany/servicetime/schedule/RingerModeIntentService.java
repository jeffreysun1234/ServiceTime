package com.mycompany.servicetime.schedule;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.mycompany.servicetime.R;
import com.mycompany.servicetime.ui.MainActivity;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RingerModeIntentService extends IntentService {
    public static final String TAG = makeLogTag(RingerModeIntentService.class);

    public static final String ACTION_SET_RINGER_MODE_NORMAL =
            "com.mycompany.servicetime.schedule.action.SET_RINGER_MODE_NORMAL";
    public static final String ACTION_SET_RINGER_MODE_VIBRATE =
            "com.mycompany.servicetime.schedule.action.SET_RINGER_MODE_VIBRATE";

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public RingerModeIntentService() {
        super("RingerModeIntentService");
    }

    public static void startActionSetRingerMode(Context context, String action) {
        Intent intent = new Intent(context, RingerModeIntentService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            LOGD(TAG, "RingerModeIntentService Action=" + action);

            if (ACTION_SET_RINGER_MODE_NORMAL.equals(action) ||
                    ACTION_SET_RINGER_MODE_VIBRATE.equals(action)) {
                handleActionSetRingerMode(action);
            }

            // Release the wake lock provided by the BroadcastReceiver.
            AlarmReceiver.completeWakefulIntent(intent);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSetRingerMode(String action) {
        AudioManager audioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (ACTION_SET_RINGER_MODE_VIBRATE.equals(action)) {
            LOGD(TAG, "set ringer mode: RINGER_MODE_VIBRATE");
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

            //set the next alarm. for normal
            SchedulingIntentService.startActionSetAlarm(getApplicationContext(), false);
        } else if (ACTION_SET_RINGER_MODE_NORMAL.equals(action)) {
            LOGD(TAG, "set ringer mode: RINGER_MODE_NORMAL");
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Settings.System.putInt(this.getContentResolver(), Settings.System.VIBRATE_ON, 1);
            audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                    AudioManager.VIBRATE_SETTING_ON);
            audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
                    AudioManager.VIBRATE_SETTING_ON);

            //set the next alarm. for vibrate
            SchedulingIntentService.startActionSetAlarm(getApplicationContext(), true);
        }

        LOGD(TAG, "Current ringer mode: " + audioManager.getRingerMode());
        //sendNotification("Success");
    }

    // Post a notification indicating whether a ringer mode changed.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.star_on)
                        .setContentTitle(getString(R.string.alert_message))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
