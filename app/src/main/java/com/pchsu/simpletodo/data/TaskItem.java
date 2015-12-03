package com.pchsu.simpletodo.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Items")
public class TaskItem extends Model{

    @Column(name = "Title", index = true, unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    String mTitle;
    @Column(name = "Note", index = true)
    String mNote;
    @Column(name = "Date", index = true)
    String mDate;
    @Column(name = "Priority", index = true)
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

    public static int priority_string_to_index (String str){
        int priority;
        switch(str){
            case "LOW":
                priority = PRIORITY_LOW;
                break;
            case "MEDIUM":
                priority = PRIORITY_MED;
                break;
            case "HIGH":
                priority = PRIORITY_HIGH;
                break;
            default:
                priority = PRIORITY_LOW;
        }
        return priority;
    }
}
