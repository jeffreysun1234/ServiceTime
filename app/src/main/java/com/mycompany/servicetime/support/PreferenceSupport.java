package com.mycompany.servicetime.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * A wrap class for accessing Preference. <br>
 * Usage:<br>
 * Get value: PreferenceSupport.getValue(String name, defalule_value) <br>
 * Set value: PreferenceSupport.setValue(String name, value)
 *
 * @author Jeffrey Sun
 */
public class PreferenceSupport {
    private static final String TAG = makeLogTag("PreferenceSupport");

    public static final String PROVIDER = "provider";
    public static final String ENCODED_EMAIL = "encoded_email";
    public static final String SIGNUP_EMAIL = "signup_email";

    public static final String NEXT_ALARM_DETAIL = "next_alarm_detail";

    public static final String FETCH_DAYS = "fetch_days";
    public static final String PREF_APP_VERSION = "app_version";
    /**
     * Per the design guidelines, you should show the drawer on launch until the user
     * manually expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /*--- provider ---*/
    public static void setProvider(final Context context, final String provider) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PROVIDER, provider).commit();
    }

    public static String getProvider(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PROVIDER, null);
    }

    /*--- signup_email ---*/
    public static void setSignupEmail(final Context context, final String signupEmail) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(SIGNUP_EMAIL, signupEmail).commit();
    }

    public static String getSignupEmail(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SIGNUP_EMAIL, null);
    }

    /*--- encoded_email ---*/
    public static void setEncodedEmail(final Context context, final String encodeEmail) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(ENCODED_EMAIL, encodeEmail).commit();
    }

    public static String getEncodedEmail(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(ENCODED_EMAIL, null);
    }

    /*--- next_alarm_detail ---*/
    public static void setNextAlarmDetail(final Context context, final String alarmDetail) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(NEXT_ALARM_DETAIL, alarmDetail).commit();
    }

    public static String getNextAlarmDetail(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(NEXT_ALARM_DETAIL, "");
    }

    /*--- fetch_days ---*/
    public static void setFetchDays(final Context context, final String days) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(FETCH_DAYS, days).commit();
    }

    public static String getFetchDays(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(FETCH_DAYS, "1");
    }

    /*--- navigation_drawer_learned ---*/
    public static boolean isUserLearnedDrawer(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    public static void setUserLearnedDrawer(final Context context,
                                            final boolean userLearnedDrawer) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, userLearnedDrawer).commit();
    }

    public static void registerOnSharedPreferenceChangeListener(final Context context,
                                                                SharedPreferences
                                                                        .OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unrgisterOnSharedPreferenceChangeListener(final Context context,
                                                                 SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
