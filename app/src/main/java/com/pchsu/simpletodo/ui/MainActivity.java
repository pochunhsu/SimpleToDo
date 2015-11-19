package com.pchsu.simpletodo.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.pchsu.simpletodo.R;
import com.pchsu.simpletodo.adapter.ItemListAdapter;
import com.pchsu.simpletodo.data.TaskItem;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    ArrayList<TaskItem> mItems;
    ItemListAdapter mAdapter;
    SwipeMenuListView mListView;
    SwipeMenuCreator mCreator;

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            //Do whatever you want
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItems = new ArrayList<>();

        TaskItem a = TaskItem.newInstance("buy book", "no note", "2015-12-12", TaskItem.PRIORITY_HIGH);
        TaskItem b = TaskItem.newInstance("take exam", "no note", "2016-03-01", TaskItem.PRIORITY_LOW);
        TaskItem c = TaskItem.newInstance("xmas", "no note", "2015-12-25", TaskItem.PRIORITY_MED);

        mItems.add(a); mItems.add(b); mItems.add(c);

        mListView = (SwipeMenuListView) findViewById(R.id.listView);
        mAdapter = new ItemListAdapter(this, mItems);
        mListView.setAdapter(mAdapter);

        mCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_white_48dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        mListView.setMenuCreator(mCreator);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                DatePickerDialog dialog = new DatePickerDialog(v.getContext(), datePickerListener,
                        2000, 1, 1);
                dialog.show();
            }
         });

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int index) {
                switch (index) {
                    case 0:
                        showEditView();
                        break;
                    case 1:
                        break;
                }
                return false;
            }
        });

    }

    private void showEditView(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ItemEditFragment.newInstance();
        newFragment.show(getSupportFragmentManager(), "dialog" );
    }


    /*
        public void onAddItem(View v){
            EditText newItem = (EditText) findViewById(R.id.newItem);
            String itemText = newItem.getText().toString();
            mAdapter.add(itemText);
            newItem.setText("");
        }
    */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ItemEditFragment extends DialogFragment {

        static public ItemEditFragment newInstance (){
            ItemEditFragment view = new ItemEditFragment();
            return view;
        }

        private DatePickerDialog.OnDateSetListener datePickerListener
                = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                //Do whatever you want
            }
        };

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
            View v = inflater.inflate(R.layout.item_editor, container, false);
            Button setDateButton = (Button) v.findViewById(R.id.button_set_date);

            setDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), datePickerListener,
                            2000, 1,1);
                    dialog.show();
                }
            });
            return v;
        }
    }
}


