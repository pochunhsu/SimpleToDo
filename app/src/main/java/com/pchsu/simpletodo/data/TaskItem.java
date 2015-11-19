package com.pchsu.simpletodo.data;

public class TaskItem {

    String mTitle;
    String mNote;
    String mDate;
    int mPriority;

    // constants
    public final static int PRIORITY_LOW = 0;
    public final static int PRIORITY_MED = 1;
    public final static int PRIORITY_HIGH = 2;

    static public TaskItem newInstance(String title, String note, String date, int priority){
        TaskItem item = new TaskItem();
        item.mTitle = title;
        item.mNote = note;
        item.mDate = date;
        item.mPriority = priority;
        return item;
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

    public void setPriority(Integer i) {
        this.mPriority = i;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }
}
