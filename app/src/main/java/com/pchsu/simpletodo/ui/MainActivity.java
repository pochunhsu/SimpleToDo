package com.pchsu.simpletodo.ui;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.pchsu.simpletodo.R;
import com.pchsu.simpletodo.adapter.ItemListAdapter;
import com.pchsu.simpletodo.data.TaskItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ItemEditFragment.Communication{

    @Bind(R.id.button_add) FloatingActionButton mButtonAdd;
    //@Bind(R.id.itemCount) TextView mItemCount;

    List<TaskItem> mItems;
    ItemListAdapter mAdapter;
    SwipeMenuListView mListView;
    SwipeMenuCreator mCreator;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mItems = new Select()
                .from(TaskItem.class)
                .orderBy("Priority DESC")
                .execute();

        mListView = (SwipeMenuListView) findViewById(R.id.listView);
        mAdapter = new ItemListAdapter(this, mItems);
        mListView.setAdapter(mAdapter);

        mCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
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
                TextView textView_title = (TextView) v.findViewById(R.id.text_title);
                String title = textView_title.getText().toString();
                showEditView(title);
            }
        });

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu swipeMenu, int index) {
                switch (index) {
                    case 0:
                        new Delete().from(TaskItem.class).where("Title = ?", mItems.get(position).getTitle()).execute();
                        mItems.remove(position);
                        mAdapter.notifyDataSetChanged();
                        //mItemCount.setText(mItems.size() + "");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        // set button action
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditView(null);
            }
        });
    }

    private void showEditView(String title){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ItemEditFragment.newInstance(title);
        newFragment.show(getSupportFragmentManager(), "dialog" );
    }

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

        int id = item.getItemId();

        if (id == R.id.menu_deleteAll) {   // delete all the items in db
            new Delete().from(TaskItem.class).execute();
            mItems.clear();
            mAdapter.notifyDataSetChanged();
            //mItemCount.setText(mItems.size()+"");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateItemList (List<TaskItem> items){
        mItems.clear();
        mItems.addAll(items);
        //mItemCount.setText(mItems.size()+"");
        mAdapter.notifyDataSetChanged();
    }
}


