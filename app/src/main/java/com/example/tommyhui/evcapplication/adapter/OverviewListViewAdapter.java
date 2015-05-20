package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.HistoryItemCS;
import com.example.tommyhui.evcapplication.database.HistoryItemCS_DBController;
import com.example.tommyhui.evcapplication.database.ItemCS;

import java.util.ArrayList;

public class OverviewListViewAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<ItemCS> itemList;
    private HistoryItemCS_DBController db;
    private boolean exist;

    public OverviewListViewAdapter(Context context, ArrayList<ItemCS> itemList) {
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
        View rowView = inflater.inflate(R.layout.overview_list_fragment_list_item_layout, parent, false);

        TextView address = (TextView) rowView.findViewById(R.id.overview_list_fragment_list_view_item_address);
        TextView chargingStation = (TextView) rowView.findViewById(R.id.overview_list_fragment_list_view_item_chargingStation);
        TextView type = (TextView) rowView.findViewById(R.id.overview_list_fragment_list_view_item_type);
        TextView socket = (TextView) rowView.findViewById(R.id.overview_list_fragment_list_view_item_socket);
//        TextView quantity = (TextView) rowView.findViewById(R.id.overview_list_fragment_list_view_item_quantity);

        address.setText(itemList.get(position).getAddress());
        chargingStation.setText(itemList.get(position).getDescription());
        type.setText(itemList.get(position).getType());
        socket.setText(itemList.get(position).getSocket());
//        quantity.setText(Integer.toString(itemList.get(position).getQuantity()));

//        db = new HistoryItemCS_DBController(context);
//        ArrayList<HistoryItemCS> historyList = db.getAllHistoryCSes();
//        HistoryItemCS clickItem = new HistoryItemCS(itemList.get(position).getAddress(), itemList.get(position).getDistrict(), itemList.get(position).getDescription(),
//                                                    itemList.get(position).getType(), itemList.get(position).getSocket(), itemList.get(position).getQuantity());
//        exist = false;
//
//        for(int i = 0; i < historyList.size(); i++) {
//            if(checkRecordExist(historyList.get(i), clickItem))
//                exist = true;
//        }
//        if(exist) {
//            address.setTextColor(context.getResources().getColor(R.color.dark_purple));
//            chargingStation.setTextColor(context.getResources().getColor(R.color.dark_purple));
//            type.setTextColor(context.getResources().getColor(R.color.dark_purple));
//            socket.setTextColor(context.getResources().getColor(R.color.dark_purple));
//            .notifyDataSetChanged();
//        }

        return rowView;
    }
//    private boolean checkRecordExist(HistoryItemCS item1, HistoryItemCS item2) {
//
//        boolean exists = false;
//
//        if(item1.getAddress().equals(item2.getAddress()) &&  item1.getDistrict().equals(item2.getDistrict())
//                && item1.getDescription().equals(item2.getDescription()) && item1.getSocket().equals(item2.getSocket())
//                && item1.getType().equals(item2.getType()) && item1.getQuantity() == item2.getQuantity())
//            exists = true;
//        return exists;
//    }
}

