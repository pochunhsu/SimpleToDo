package com.pchsu.simpletodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pchsu.simpletodo.R;
import com.pchsu.simpletodo.data.TaskItem;

import java.util.List;

public class ItemListAdapter extends BaseAdapter {

    private Context mContext;
    private List<TaskItem> mItems;

    public ItemListAdapter(Context context, List<TaskItem> items){
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public TaskItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate (R.layout.item, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        TaskItem item = getItem(position);
        holder.mTitle.setText(item.getTitle());
        holder.mDate.setText(item.getDate());
        holder.setBackgroudColor(item.getPriority());
        return convertView;
    }

    class ViewHolder {
        TextView mTitle;
        TextView mDate;
        RelativeLayout mLayout;

        public ViewHolder(View view) {
            mTitle = (TextView) view.findViewById(R.id.text_title);
            mDate = (TextView) view.findViewById(R.id.text_date);
            mLayout = (RelativeLayout) view.findViewById(R.id.item_layout);
            view.setTag(this);
        }

        public void setBackgroudColor(Integer priority){
            switch(priority){
                case TaskItem.PRIORITY_LOW:
                    mLayout.setBackgroundResource(R.drawable.item_low);
                    break;
                case TaskItem.PRIORITY_MED:
                    mLayout.setBackgroundResource(R.drawable.item_medium);
                    break;
                case TaskItem.PRIORITY_HIGH:
                    mLayout.setBackgroundResource(R.drawable.item_hign);
                    break;
            }

        }
    }
}