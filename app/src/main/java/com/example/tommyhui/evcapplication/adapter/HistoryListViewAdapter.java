package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.HistoryItemCS;

import java.util.ArrayList;

public class HistoryListViewAdapter extends BaseAdapter{
    private final Context context;
    private ArrayList<HistoryItemCS> itemList;

    public HistoryListViewAdapter(Context context, ArrayList<HistoryItemCS> itemList){
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

    public ArrayList<HistoryItemCS> getList(){
        return itemList;
    }

    public void setList(ArrayList<HistoryItemCS> itemList){
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.overview_history_fragment_list_item_layout, parent, false);

        TextView address = (TextView) rowView.findViewById(R.id.overview_history_fragment_list_view_item_address);
        TextView district = (TextView) rowView.findViewById(R.id.overview_history_fragment_list_view_item_district);
        TextView chargingStation = (TextView) rowView.findViewById(R.id.overview_history_fragment_list_view_item_chargingStation);
        TextView type = (TextView) rowView.findViewById(R.id.overview_history_fragment_list_view_item_type);
        TextView socket = (TextView) rowView.findViewById(R.id.overview_history_fragment_list_view_item_socket);

        address.setText(itemList.get(position).getAddress());
        district.setText(itemList.get(position).getDistrict());
        chargingStation.setText(itemList.get(position).getDescription());
        type.setText(itemList.get(position).getType());
        socket.setText(itemList.get(position).getSocket());

        return rowView;
    }

}

