package com.pchsu.simpletodo.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.pchsu.simpletodo.R;
import com.pchsu.simpletodo.data.TaskItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemEditFragment extends DialogFragment {

    public static final String TAG_TITLE  = "TITLE";

    @Bind(R.id.edit_title)
    EditText mEditTitle;
    @Bind(R.id.priority_spinner)
    Spinner mSpinnerPriority;
    @Bind(R.id.edit_note) EditText mEditNote;
    @Bind(R.id.datePicker)
    DatePicker mDatePicker;
    @Bind(R.id.button_save)
    Button mButtonSave;

    String mDateString;
    TaskItem mItem;
    Communication mCallback;

    static public ItemEditFragment newInstance (String title){
        ItemEditFragment f = new ItemEditFragment();

        // set up the passing parameter
        Bundle args = new Bundle();
        args.putString(TAG_TITLE, title);
        f.setArguments(args);

        return f;
    }

    // TODO timing ??? before SAVE ?
    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker datePicker, int selectedYear,
                              int selectedMonth, int selectedDay) {

            int   day  = datePicker.getDayOfMonth();
            int   month= datePicker.getMonth();
            int   year = datePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year + 1900, month, day);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setTimeZone(calendar.getTimeZone());
            mDateString = dateFormat.format(calendar.getTime());
        }
    };

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.item_editor, container, false);
        ButterKnife.bind(this, v);

        // set up the spinner for priority selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.priority_choices, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerPriority.setAdapter(adapter);

        // read parameter and look up item instance in db
        String title = getArguments().getString(TAG_TITLE);
        if (title == null){  // (1) adding new Item case
            mItem = new TaskItem();
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
                mEditNote.setText(mItem.getNote());
                setDatePicker(mItem.getDate());
            }
        }

        // set up SAVE actions
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem.setTitle(mEditTitle.getText().toString());
                mItem.setNote(mEditNote.getText().toString());
                int priority = TaskItem.priority_string_to_index(mSpinnerPriority.getSelectedItem().toString());
                mItem.setPriority(priority);
                mItem.setDate(getDateString());
                mItem.save();
                mCallback.notify_data_change();
                dismiss();
            }
        });
        return v;
    }

    private String getDateString(){
        int   day  = mDatePicker.getDayOfMonth();
        int   month= mDatePicker.getMonth();
        int   year = mDatePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year + 1900, month, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setTimeZone(calendar.getTimeZone());
        return dateFormat.format(calendar.getTime());
    }

    private void setDatePicker(String str){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int year = calendar.get(Calendar.YEAR) - 1990;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.updateDate( year, month, day);

    }

    public interface Communication {
        void notify_data_change();
    }
}
