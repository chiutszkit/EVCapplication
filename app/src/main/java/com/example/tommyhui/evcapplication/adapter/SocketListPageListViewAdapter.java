package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.ItemCS;

import java.util.ArrayList;

public class SocketListPageListViewAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private final Context context;
    private ArrayList<ItemCS> itemList;

    public SocketListPageListViewAdapter(Context context, ArrayList<ItemCS> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public ArrayList<ItemCS> getList() {
        return itemList;
    }

    public void setList(ArrayList<ItemCS> itemList) {
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.socket_listpage_list_item_layout, parent, false);

        TextView address = (TextView) rowView.findViewById(R.id.socket_listpage_list_view_item_address);
        TextView chargingStation = (TextView) rowView.findViewById(R.id.socket_listpage_list_view_item_chargingStation);
        TextView distance = (TextView) rowView.findViewById(R.id.socket_listpage_list_view_item_distance);
        TextView time = (TextView) rowView.findViewById(R.id.socket_listpage_list_view_item_time);

        address.setText(itemList.get(position).getAddress());
        chargingStation.setText(itemList.get(position).getDescription());
//        distance.setText(itemList.get(position).getType());
//        time.setText(itemList.get(position).getSocket());

        return rowView;
    }
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.sticky_list_item_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.sticky_list_header_text);
            convertView.setTag(holder);

        } else {
            holder = (HeaderViewHolder) convertView.getTag();

        }
        //set header text as first char in name
        String headerText = "" + itemList.get(position).getDistrict();

        holder.text.setText(headerText);

        return convertView;
    }
    @Override
    public long getHeaderId(int position) {

            String district = itemList.get(position).getDistrict();
            long id = 0;
            for(int i = 0; i < district.length(); i++)
                id += (long)district.charAt(i);
            return id;
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
    }
}

