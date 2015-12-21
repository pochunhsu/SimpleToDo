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

    public enum AlarmTime {
        NO_SETTING(0), MIN_30(1), HR_1(2), HR_2(3), HR_6(4), HR_12(5), DAY_1(6);
        private final int value;
        AlarmTime(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }
    }

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
    AlarmTime mAlarmTime;

    static public TaskItem newInstance(String title, String note, String date, String time, int priority, AlarmTime alarmTime){
        TaskItem item = new TaskItem();
        item.mTitle = title;
        item.mNote = note;
        item.mDate = date;
        item.mTime = time;
        item.mPriority = priority;
        item.mAlarmTime = alarmTime;
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

    public AlarmTime getAlarmTime() {
        return mAlarmTime;
    }

    public void setAlarmTime(AlarmTime alarmTime) {
        mAlarmTime = alarmTime;
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

    public static AlarmTime alarm_string_to_index (String str){
        AlarmTime alarmTime;
        switch(str){
            case "----":
                alarmTime = AlarmTime.NO_SETTING;
                break;
            case "30 min":
                alarmTime = AlarmTime.MIN_30;
                break;
            case "1 hr":
                alarmTime = AlarmTime.HR_1;
                break;
            case "2 hr":
                alarmTime = AlarmTime.HR_2;
                break;
            case "6 hr":
                alarmTime = AlarmTime.HR_6;
                break;
            case "12 hr":
                alarmTime = AlarmTime.HR_12;
                break;
            case "1 day":
                alarmTime = AlarmTime.DAY_1;
                break;
            default:
                alarmTime = AlarmTime.NO_SETTING;
        }
        return alarmTime;
    }
}
