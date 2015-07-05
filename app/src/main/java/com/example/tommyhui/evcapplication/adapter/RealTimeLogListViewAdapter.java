
package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.HomeActivity;
import com.example.tommyhui.evcapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RealTimeLogListViewAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<HashMap<String, String>> logsList;

    public RealTimeLogListViewAdapter(Context context, ArrayList<HashMap<String, String>> logsList) {
        this.context = context;
        this.logsList = logsList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return logsList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return logsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public ArrayList<HashMap<String, String>> getList() {
        return logsList;
    }

    public void setList(ArrayList<HashMap<String, String>> logsList) {
        this.logsList = logsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.realtime_log_list_item_layout, parent, false);

        TextView district = (TextView) rowView.findViewById(R.id.realtime_log_list_view_item_district);
        TextView chargingStation = (TextView) rowView.findViewById(R.id.realtime_log_list_view_item_chargingStation);
        TextView time = (TextView) rowView.findViewById(R.id.realtime_log_list_view_item_time);
        TextView type = (TextView) rowView.findViewById(R.id.realtime_log_list_view_item_type);
        TextView socket = (TextView) rowView.findViewById(R.id.realtime_log_list_view_item_socket);

        district.setText(logsList.get(position).get(HomeActivity.TAG_LOG_DISTRICT));
        chargingStation.setText(logsList.get(position).get(HomeActivity.TAG_LOG_DESCRIPTION));
        time.setText(logsList.get(position).get(HomeActivity.TAG_LOG_TIME));
        type.setText(logsList.get(position).get(HomeActivity.TAG_LOG_TYPE));
        socket.setText(logsList.get(position).get(HomeActivity.TAG_LOG_SOCKET));

        return rowView;
    }
}



