package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.ItemCS;

import java.util.ArrayList;

public class OverviewListViewAdapter extends BaseAdapter{
    private final Context context;
    private final ArrayList<ItemCS> itemList;

    public OverviewListViewAdapter(Context context, ArrayList<ItemCS> itemList){
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.overview_list_view_list_item_layout, parent, false);

        TextView address = (TextView) rowView.findViewById(R.id.overview_list_view_item_address);
        TextView district = (TextView) rowView.findViewById(R.id.overview_list_view_item_district);
        TextView chargingStation = (TextView) rowView.findViewById(R.id.overview_list_view_item_chargingStation);
        TextView type = (TextView) rowView.findViewById(R.id.overview_list_view_item_type);
        TextView socket = (TextView) rowView.findViewById(R.id.overview_list_view_item_socket);
//        TextView quantity = (TextView) rowView.findViewById(R.id.overview_list_view_item_quantity);

        address.setText(itemList.get(position).getAddress());
        district.setText(itemList.get(position).getDistrict());
        chargingStation.setText(itemList.get(position).getDescription());
        type.setText(itemList.get(position).getType());
        socket.setText(itemList.get(position).getSocket());
//        quantity.setText(Integer.toString(itemList.get(position).getQuantity()));

        return rowView;
    }

}

