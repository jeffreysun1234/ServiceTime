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

    /**
     * Returns this current time value in millisecond.
     * @param currentTime24 In 24 hour format, for example, 920 means 9:20am
     * @return
     */
    public static long getCurrentTimestamp(int currentTime24) {
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, (int) Math.floor(currentTime24 / 100.0d));
        calendar.set(Calendar.MINUTE, currentTime24 % 100);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     *
     * @param timestamp
     * @return
     * @throws ParseException
     */
    public static int getHHmm(long timestamp) throws ParseException {
        String date_format = "HHmm";
        return Integer.parseInt(new SimpleDateFormat(date_format).format(timestamp));
    }
}








