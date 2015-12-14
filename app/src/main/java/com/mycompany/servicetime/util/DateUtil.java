package com.mycompany.servicetime.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static final String date_format = "yyyyMMdd:HHmm";
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();

    public static DateFormat getDateFormat() {
        DateFormat df = threadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(date_format);
            threadLocal.set(df);
        }
        return df;
    }

    public static String format(long timestamp) throws ParseException {
        return getDateFormat().format(timestamp);
    }

    public static String format(Date date) throws ParseException {
        return getDateFormat().format(date);
    }

    public static Date parse(String strDate) throws ParseException {
        return getDateFormat().parse(strDate);
    }

    /**
     * @return the offset in Seconds.
     */
    public static int getLocalTimeZoneOffset() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.DST_OFFSET) + calendar.get(Calendar.ZONE_OFFSET)) / 1000;
    }

    /**
     * Returns this current time value in seconds.
     *
     * @return the current time as UTC milliseconds from the epoch.
     */
    public static long getCurrentTimestamp() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis() / 1000;
    }
}








