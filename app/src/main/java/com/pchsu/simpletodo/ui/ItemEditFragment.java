package com.pchsu.simpletodo.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.pchsu.simpletodo.R;
import com.pchsu.simpletodo.data.TaskItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemEditFragment extends DialogFragment {

    public static final String TAG_TITLE  = "TITLE";

    @Bind(R.id.edit_title) EditText mEditTitle;
    @Bind(R.id.priority_spinner) Spinner mSpinnerPriority;
    @Bind(R.id.edit_note) EditText mEditNote;
  //  @Bind(R.id.datePicker) DatePicker mDatePicker;
    @Bind(R.id.button_save) FloatingActionButton mButtonSave;

    TaskItem mItem;
    Communication mCallback;
    boolean mIsNew;

    static public ItemEditFragment newInstance (String title){
        ItemEditFragment f = new ItemEditFragment();

        // set up the passing parameter
        Bundle args = new Bundle();
        args.putString(TAG_TITLE, title);
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
        // set up the spinner for priority selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.priority_choices, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPriority.setAdapter(adapter);
        mSpinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        }) ;

        // read parameter and look up item instance in db
        String title = getArguments().getString(TAG_TITLE);
        if (title == null){  // (1) adding new Item case
            mItem = new TaskItem();
            mIsNew = true;
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
                //setDatePicker(mItem.getDate());
                mIsNew = false;
            }
        }

        // set up SAVE actions
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

                if(title.equals("")){
                    Toast.makeText(getActivity(), "Warning: please specify title!\nInsertion skipped.", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                // insertion happens here
                mItem.setTitle(mEditTitle.getText().toString());
                mItem.setNote(mEditNote.getText().toString());
                int priority = TaskItem.priority_string_to_index(mSpinnerPriority.getSelectedItem().toString());
                mItem.setPriority(priority);
                //mItem.setDate(getDateString());
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
