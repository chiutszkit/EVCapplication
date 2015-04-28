package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;

import java.util.ArrayList;

public class OverviewSearchAutoCompleteAdapter extends BaseAdapter implements Filterable {

    class GroupSearchListViewHolder {

        public TextView displayNameView;
        private LinearLayout dropdownItemLayout;

        public GroupSearchListViewHolder(View v) {
            displayNameView = (TextView) v.findViewById(R.id.listitem_group_displayname);
            dropdownItemLayout = (LinearLayout) v.findViewById(R.id.group_search_dropdown_item_layout);
        }
    }

    private ArrayList<String> resultList;
    private ArrayList<String> originalList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int leftPaddingWidth = -1;
    private int dropdownHeight = -1;

    public OverviewSearchAutoCompleteAdapter(Context context, ArrayList<String> originalList) {
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.originalList = originalList;


    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    public ArrayList<String> getmList(){
        return resultList;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            v = mLayoutInflater.inflate(R.layout.list_item_overview_search, parent, false);

            GroupSearchListViewHolder viewHolder = new GroupSearchListViewHolder(v);
            v.setTag(viewHolder);
        }
        bindView(v, position);
        return v;
    }

    private void bindView(View v, int position) {

        String item = getItem(position);

        final GroupSearchListViewHolder viewHolder = (GroupSearchListViewHolder) v.getTag();

        viewHolder.displayNameView.setText(item);

        if(leftPaddingWidth != -1){
            viewHolder.displayNameView.setPadding(leftPaddingWidth,0,0,0);
        }

    }

    public void setLeftPaddingWidth(int width){
        this.leftPaddingWidth = width;
    }

    public int getGroupSearchItemHeight(){
        return dropdownHeight;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    //---SearchView user input filtering---//
    public ArrayList<String> autocomplete(String s){
        if(!s.isEmpty()) {
            ArrayList<String> newItemList = new ArrayList<String>();

            for (String item : originalList) {

                if (item.toLowerCase().contains(s.toLowerCase())) {
                    newItemList.add(item);
                }
            }
            return newItemList;
        }
        else{
            return originalList;
        }
    }
}
