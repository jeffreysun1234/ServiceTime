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

    public static final String BAR_LIMIT = "bar_limit";
    public static final String TIME_FRAME = "time_frame";
    public static final String CURRENT_SYMBOL = "current_symbol";
    public static final String FETCH_DAYS = "fetch_days";
    public static final String PREF_APP_VERSION = "app_version";
    /**
     * Per the design guidelines, you should show the drawer on launch until the user
     * manually expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /*--- bar_limit ---*/
    public static void setBarLimit(final Context context, final int barLimit) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(BAR_LIMIT, barLimit).commit();
    }

    public static int getBarLimit(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(BAR_LIMIT, 100);
    }

    /*--- app_version ---*/
    public static void setAppVersion(final Context context, final int appVersion) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_APP_VERSION, appVersion).commit();
    }

    public static int getAppVersion(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_APP_VERSION, Integer.MIN_VALUE);
    }

    /*--- time_frame ---*/
    public static void setTimeFrame(final Context context, final String timeFrame) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(TIME_FRAME, timeFrame).commit();
    }

    public static String getTimeFrame(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(TIME_FRAME, "5 Mins");
    }

    /*--- current_symbol ---*/
    public static void setCurrentSymbol(final Context context, final String symbol) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(CURRENT_SYMBOL, symbol).commit();
    }

    public static String getCurrentSymbol(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(CURRENT_SYMBOL, "");
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
