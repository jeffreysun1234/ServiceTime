package com.mycompany.servicetime.firebase.model;

/**
 * Created by szhx on 3/24/2016.
 */
public class TimeSlotItem {
    private String timeSlotId;
    private String name;
    private int beginTimeHour;
    private int beginTimeMinute;
    private int endTimeHour;
    private int endTimeMinute;
    private String days;
    private boolean repeatFlag;
    private boolean serviceFlag;

    public TimeSlotItem() {
    }

    public TimeSlotItem(int beginTimeHour, int beginTimeMinute, String days, int endTimeHour,
                        int endTimeMinute, String name, boolean repeatFlag, boolean serviceFlag,
                        String timeSlotId) {
        this.beginTimeHour = beginTimeHour;
        this.beginTimeMinute = beginTimeMinute;
        this.days = days;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
        this.name = name;
        this.repeatFlag = repeatFlag;
        this.serviceFlag = serviceFlag;
        this.timeSlotId = timeSlotId;
    }

    public int getBeginTimeHour() {
        return beginTimeHour;
    }

    public void setBeginTimeHour(int beginTimeHour) {
        this.beginTimeHour = beginTimeHour;
    }

    public int getBeginTimeMinute() {
        return beginTimeMinute;
    }

    public void setBeginTimeMinute(int beginTimeMinute) {
        this.beginTimeMinute = beginTimeMinute;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getEndTimeHour() {
        return endTimeHour;
    }

    public void setEndTimeHour(int endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public int getEndTimeMinute() {
        return endTimeMinute;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(boolean repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    public boolean isServiceFlag() {
        return serviceFlag;
    }

    public void setServiceFlag(boolean serviceFlag) {
        this.serviceFlag = serviceFlag;
    }

    public String getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }
}
