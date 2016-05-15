package com.mycompany.servicetime.data.source.model;

/**
 * Created by szhx on 12/13/2015.
 */
public class TimeSlot {
    public String timeSlotId;
    public String name;
    public int beginTimeHour;
    public int beginTimeMinute;
    public int endTimeHour;
    public int endTimeMinute;
    public String days;
    public boolean repeatFlag;
    public boolean serviceFlag;

    public TimeSlot() {
    }

    /**
     * Use this constructor to create a new TimeSlot
     */
    public TimeSlot(String name, int beginTimeHour, int beginTimeMinute, int endTimeHour, int endTimeMinute,
                    String days, boolean repeatFlag) {
        this.beginTimeHour = beginTimeHour;
        this.beginTimeMinute = beginTimeMinute;
        this.days = days;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
        this.name = name;
        this.repeatFlag = repeatFlag;
    }

    /**
     * Use this constructor to create a TimeSlot for update if the TimeSlot already has a timeSlotId.
     */
    public TimeSlot(String timeSlotId, String name, int beginTimeHour, int beginTimeMinute,
                    int endTimeHour, int endTimeMinute, String days, boolean repeatFlag) {
        this(name, beginTimeHour, beginTimeMinute, endTimeHour, endTimeMinute, days, repeatFlag);
        this.timeSlotId = timeSlotId;
    }
}
