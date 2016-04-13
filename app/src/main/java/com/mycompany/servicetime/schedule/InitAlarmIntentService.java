package com.mycompany.servicetime.schedule;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class InitAlarmIntentService extends IntentService {

    public static final String ACTION_INIT = "com.mycompany.servicetime.schedule.action.INIT";

    public InitAlarmIntentService() {
        super("InitAlarmIntentService");
    }

    public static void startActionInit(Context context) {
        Intent intent = new Intent(context, InitAlarmIntentService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT.equals(action)) {
                handleActionInit();
            }
}
        }

private void handleActionInit() {
        SchedulingIntentService.startActionSetAlarm(getApplicationContext(), true);
        SchedulingIntentService.startActionSetAlarm(getApplicationContext(), false);
        }
        }
