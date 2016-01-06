package com.pchsu.simpletodo.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.pchsu.simpletodo.Constant;
import com.pchsu.simpletodo.R;
import com.pchsu.simpletodo.data.TaskItem;
import com.pchsu.simpletodo.service.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemEditFragment extends DialogFragment
        implements CalendarDatePickerDialogFragment.OnDateSetListener,
                    RadialTimePickerDialogFragment.OnTimeSetListener{

    public static final String TAG_DATE_PICKER  = "DATE_PICKER";
    public static final String TAG_TIME_PICKER  = "TIME_PICKER";

    @Bind(R.id.edit_title) EditText mEditTitle;
    @Bind(R.id.priority_spinner) Spinner mSpinnerPriority;
    @Bind(R.id.alarm_spinner) Spinner mSpinnerAlarm;
    @Bind(R.id.edit_note) EditText mEditNote;
    @Bind(R.id.button_date) Button mButtonDate;
    @Bind(R.id.button_time) Button mButtonTime;
    @Bind(R.id.button_save) FloatingActionButton mButtonSave;
    @Bind(R.id.frame) LinearLayout mFrame;

    TaskItem mItem;
    Communication mCallback;
    boolean mIsNew;

    static public ItemEditFragment newInstance (String title){
        ItemEditFragment f = new ItemEditFragment();

        // set up the passing parameter
        Bundle args = new Bundle();
        args.putString(Constant.TAG_TITLE, title);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (Communication) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Communication");
        }
    }

    @Override
    @NonNull public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.item_editor, container, false);
        ButterKnife.bind(this, v);

        // set up the spinner for priority selection
        ArrayAdapter<CharSequence> adapter_priority = ArrayAdapter.createFromResource(getActivity(),
                R.array.priority_choices, R.layout.spinner_item);
        adapter_priority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPriority.setAdapter(adapter_priority);
        mSpinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }
                switch(position) {
                    case TaskItem.PRIORITY_LOW:
                        mFrame.setBackgroundResource(R.drawable.background_edit_title_priority_low);
                        break;
                    case TaskItem.PRIORITY_MED:
                        mFrame.setBackgroundResource(R.drawable.background_edit_title_priority_med);
                        break;
                    case TaskItem.PRIORITY_HIGH:
                        mFrame.setBackgroundResource(R.drawable.background_edit_title_priority_high);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // set up the spinner for alarm time selection
        ArrayAdapter<CharSequence> adapter_alarm = ArrayAdapter.createFromResource(getActivity(),
                R.array.alarm_choices, R.layout.spinner_item);
        adapter_alarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAlarm.setAdapter(adapter_alarm);
        mSpinnerAlarm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // read parameter and look up item instance in db
        // and then populate the UI display accordingly
        String title = getArguments().getString(Constant.TAG_TITLE);
        if (title == null){  // (1) adding new Item case
            mItem = new TaskItem();
            mIsNew = true;
            mButtonDate.setText(R.string.label_set_date);
            mButtonTime.setText(R.string.label_set_time);
        }else{               // (2) modifying old item case
            mItem = new Select()
                    .from(TaskItem.class)
                    .where("Title = ?", title)
                    .orderBy("RANDOM()")
                    .executeSingle();
            if (mItem == null) {
                Toast.makeText(getActivity(), "Error: cannot find \"" + title + "\" in db", Toast.LENGTH_LONG).show();
                mItem = new TaskItem();
            }else{ // initialize the fields in the fragment
                mEditTitle.setText(mItem.getTitle());
                mSpinnerPriority.setSelection(mItem.getPriority());
                mSpinnerAlarm.setSelection((mItem.getAlarmTime().getValue()));
                mEditNote.setText(mItem.getNote());
                mButtonDate.setText(mItem.getDate());
                mButtonTime.setText(mItem.getTime());
                mIsNew = false;
            }
        }

        // set up date-picker button action
        mButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                final String date_str = mButtonDate.getText().toString();
                final String str_set_date =  getResources().getText(R.string.label_set_date).toString();
                CalendarDatePickerDialogFragment calendarDatePickerDialogFragment;

                // For 1st time setting date-piker, use the current date as default
                if (date_str.equalsIgnoreCase(str_set_date)) {
                    calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                            .newInstance(ItemEditFragment.this, calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH));

                // For modifying setting in date-piker, use the set date as default
                }else{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                    try {
                        calendar.setTime(dateFormat.parse(date_str));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                            .newInstance(ItemEditFragment.this, year, month, day);
                }
                calendarDatePickerDialogFragment.setThemeDark(true);
                calendarDatePickerDialogFragment.show(fm, TAG_DATE_PICKER);
            }
        });

        // set up dial-time-picker button action
        mButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                //DateTime now = DateTime.now();
                final String time_str = mButtonTime.getText().toString();
                final String str_set_time =  getResources().getText(R.string.label_set_time).toString();
                RadialTimePickerDialogFragment timePickerDialog;

                // For 1st time setting time-piker, use the current time as default
                if (time_str.equalsIgnoreCase(str_set_time)){
                    timePickerDialog = RadialTimePickerDialogFragment
                            .newInstance(ItemEditFragment.this,
                                            calendar.get(Calendar.HOUR_OF_DAY),
                                            calendar.get(Calendar.MINUTE),
                                            DateFormat.is24HourFormat(getActivity()));

                // For modifying setting in time-piker, use the set time as default
                }else{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                    try {
                        calendar.setTime(dateFormat.parse(time_str));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    timePickerDialog = RadialTimePickerDialogFragment
                            .newInstance(ItemEditFragment.this, hour, minute,
                                    DateFormat.is24HourFormat(getActivity()));
                }
                timePickerDialog.setThemeDark(true);
                timePickerDialog.show(fm, TAG_TIME_PICKER);
            }
        });

        // set up floating action button action (SAVE)
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the newly added item title already exists in db
                // if so, skip the insertion and post warning message
                String title = mEditTitle.getText().toString();
                if (mIsNew){
                    TaskItem item = new Select()
                            .from(TaskItem.class)
                            .where("Title = ?", title)
                            .orderBy("RANDOM()")
                            .executeSingle();
                    if (item != null){
                        Toast.makeText(getActivity(), "Warning: " + title + " already in db!\nInsertion skipped.", Toast.LENGTH_SHORT).show();
                        dismiss();
                        return;
                    }
                }
                // check if the title is empty
                if(title.equals("")){
                    Toast.makeText(getActivity(), "Warning: please specify title!\nInsertion skipped.", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }
                // check if alarm is set and date/time not set, abort the save and warn the user
                if (!alarmSettingOK()){ return;}

                // all the checks pass ; now the data is valid and ready to be inserted into db
                // db item insertion happens here
                mItem.setTitle(mEditTitle.getText().toString());
                mItem.setNote(mEditNote.getText().toString());

                int priority = TaskItem.priority_string_to_index(mSpinnerPriority.getSelectedItem().toString());
                mItem.setPriority(priority);

                TaskItem.AlarmTime alarmTime = TaskItem.alarm_string_to_index(mSpinnerAlarm.getSelectedItem().toString());
                mItem.setAlarmTime(alarmTime);
                if(alarmTime != TaskItem.AlarmTime.NO_SETTING){
                    setAlarm();
                }

                final String date_str = mButtonDate.getText().toString();
                final String str_set_date =  getResources().getText(R.string.label_set_date).toString();
                if ( ! date_str.equalsIgnoreCase(str_set_date)){
                    mItem.setDate(date_str);
                }
                final String time_str = mButtonTime.getText().toString();
                final String str_set_time =  getResources().getText(R.string.label_set_time).toString();
                if ( ! time_str.equalsIgnoreCase(str_set_time)){
                    mItem.setTime(time_str);
                }
                mItem.save();

                List<TaskItem> items = new Select()
                        .from(TaskItem.class)
                        .orderBy("Priority DESC")
                        .execute();
                //Toast.makeText(getActivity(), items.size() +"" , Toast.LENGTH_SHORT).show();
                mCallback.updateItemList(items);

                dismiss();
            }
        });

        return v;
    }

    // make the dialog full screen
    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // reattaching to the fragment
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment
                = (CalendarDatePickerDialogFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }
        RadialTimePickerDialogFragment rtpd
                = (RadialTimePickerDialogFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(TAG_TIME_PICKER);
        if (rtpd != null) {
            rtpd.setOnTimeSetListener(this);
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        dateFormat.setTimeZone(calendar.getTimeZone());
        mButtonDate.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        dateFormat.setTimeZone(calendar.getTimeZone());
        mButtonTime.setText(dateFormat.format(calendar.getTime()));
    }

    // if alarm is set and date/time is not set, warn the user
    private boolean alarmSettingOK(){
        final String date_str = mButtonDate.getText().toString();
        final String time_str = mButtonTime.getText().toString();
        final String str_set_date =  getResources().getText(R.string.label_set_date).toString();
        final String str_set_time =  getResources().getText(R.string.label_set_time).toString();
        TaskItem.AlarmTime alarmTime = TaskItem.alarm_string_to_index(mSpinnerAlarm.getSelectedItem().toString());
        if( alarmTime != TaskItem.AlarmTime.NO_SETTING){
            if (date_str.equalsIgnoreCase(str_set_date) || time_str.equalsIgnoreCase(str_set_time)){
                Toast.makeText(getActivity(), "Warning: to use alarm. Please set date/time!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void setAlarm() {
        if (mItem == null) {
            //TODO error msg
            return;
        } else if (mItem.getTitle() == null || mItem.getNote() == null) {
            //TODO error msg
            return;
        }

        final String title = mItem.getTitle();
        final String note = mItem.getNote();
        Long alertTime = new GregorianCalendar().getTimeInMillis()+3*1000;
        Intent alertIntent = new Intent(getActivity(), AlarmReceiver.class);
        alertIntent.putExtra(Constant.TAG_TITLE,title);
        alertIntent.putExtra(Constant.TAG_NOTE, note);
        alertIntent.setAction(title);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(getActivity(), 0, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime+10000,
                PendingIntent.getBroadcast(getActivity(),0 , alertIntent,PendingIntent.FLAG_UPDATE_CURRENT));

    }

    /*
        private String getDateString(){
            int   day  = mDatePicker.getDayOfMonth();
            int   month= mDatePicker.getMonth();
            int   year = mDatePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            dateFormat.setTimeZone(calendar.getTimeZone());
            return dateFormat.format(calendar.getTime());
        }

        private void setDatePicker(String str){

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(dateFormat.parse(str));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            mDatePicker.updateDate( year, month, day);

        }
    */
    public interface Communication {
        void updateItemList (List<TaskItem> items);
    }
}
