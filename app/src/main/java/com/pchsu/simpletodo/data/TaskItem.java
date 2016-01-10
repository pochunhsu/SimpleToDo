package com.pchsu.simpletodo.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Items")
public class TaskItem extends Model{

    // constants
    public final static int PRIORITY_LOW = 0;
    public final static int PRIORITY_MED = 1;
    public final static int PRIORITY_HIGH = 2;

    public final static int NO_SETTING = 0;
    public final static int MIN_30 = 1;
    public final static int HR_1 = 2;
    public final static int HR_2 = 3;
    public final static int HR_6 = 4;
    public final static int HR_12 = 5;
    public final static int DAY_1 = 6;

    @Column(name = "Title", index = true, unique = true, onUniqueConflict = Column.ConflictAction.FAIL)
    String mTitle;
    @Column(name = "Note", index = true)
    String mNote;
    @Column(name = "Date", index = true)
    String mDate;
    @Column(name = "Time", index = true)
    String mTime;
    @Column(name = "Priority", index = true)
    int mPriority;
    @Column(name = "AlarmTime", index = true)
    int mAlarmIndex;
    @Column(name = "AlarmMilis", index = true)
    Long mAlarmMilis;

    static public TaskItem newInstance(String title, String note, String date, String time,
                                       int priority, int alarmIndex, Long alarmMilis){
        TaskItem item = new TaskItem();
        item.mTitle = title;
        item.mNote = note;
        item.mDate = date;
        item.mTime = time;
        item.mPriority = priority;
        item.mAlarmIndex = alarmIndex;
        item.mAlarmMilis = alarmMilis;
        return item;
    }

    public TaskItem(){
        super();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int i) {
        this.mPriority = i;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public int getAlarmIndex() {
        return mAlarmIndex;
    }

    public void setAlarmIndex(int alarmIndex) {
        mAlarmIndex = alarmIndex;
    }

    public Long getAlarmMilis() {
        return mAlarmMilis;
    }

    public void setAlarmMilis(Long alarmMilis) {
        mAlarmMilis = alarmMilis;
    }

    public static int priority_string_to_index (String str){
        int priority;
        switch(str){
            case "Low":
                priority = PRIORITY_LOW;
                break;
            case "Medium":
                priority = PRIORITY_MED;
                break;
            case "High":
                priority = PRIORITY_HIGH;
                break;
            default:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    public static int alarm_string_to_index (String str){
        int alarmIndex;
        switch(str){
            case "----":
                alarmIndex = NO_SETTING;
                break;
            case "30 min":
                alarmIndex = MIN_30;
                break;
            case "1 hr":
                alarmIndex = HR_1;
                break;
            case "2 hr":
                alarmIndex = HR_2;
                break;
            case "6 hr":
                 alarmIndex = HR_6;
                break;
            case "12 hr":
                 alarmIndex = HR_12;
                break;
            case "1 day":
                 alarmIndex = DAY_1;
                break;
            default:
                 alarmIndex = NO_SETTING;
        }
        return alarmIndex;
    }

    public static Long alarm_string_to_milis (String str){
        int oneHourInMilis = 60 * 60 * 1000;
        int milis;
        switch(str){
            case "----":
                milis = 0;
                break;
            case "30 min":
                milis = oneHourInMilis / 2;
                break;
            case "1 hr":
                milis = oneHourInMilis;
                break;
            case "2 hr":
                milis = oneHourInMilis * 2;
                break;
            case "6 hr":
                milis = oneHourInMilis * 6;
                break;
            case "12 hr":
                milis = oneHourInMilis * 12;
                break;
            case "1 day":
                milis = oneHourInMilis * 24;
                break;
            default:
                milis = 0;
        }

        return Long.valueOf(milis);
    }
}
