package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS_DBController;

import java.util.ArrayList;

public class FavoriteListViewAdapter extends BaseAdapter{
    private final Context context;
    private FavoriteItemCS_DBController db;
    private ArrayList<FavoriteItemCS> itemList;
    private ArrayList<FavoriteItemCS> tempList;

    public FavoriteListViewAdapter(Context context, ArrayList<FavoriteItemCS> itemList){
        this.context = context;
        this.itemList = itemList;
        tempList = new ArrayList<>(itemList.size());
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
//   @Override
//    public boolean isEnabled(int position) {
//        return false;
//    }
//    @Override
//    public boolean areAllItemsEnabled() {
//        return true;
//    }

    public ArrayList<FavoriteItemCS> getList(){
        return itemList;
    }

    public void setList(ArrayList<FavoriteItemCS> itemList){
        this.itemList = itemList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.favorite_list_item_layout, parent, false);

        TextView address = (TextView) rowView.findViewById(R.id.favorite_list_view_item_address);
        TextView district = (TextView) rowView.findViewById(R.id.favorite_list_view_item_district);
        TextView chargingStation = (TextView) rowView.findViewById(R.id.favorite_list_view_item_chargingStation);
        TextView type = (TextView) rowView.findViewById(R.id.favorite_list_view_item_type);
        TextView socket = (TextView) rowView.findViewById(R.id.favorite_list_view_item_socket);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.favorite_list_view_icon_favorite);

        address.setText(itemList.get(position).getAddress());
        district.setText(itemList.get(position).getDistrict());
        chargingStation.setText(itemList.get(position).getDescription());
        type.setText(itemList.get(position).getType());
        socket.setText(itemList.get(position).getSocket());

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    imageView.setImageResource(R.drawable.delfavorite_icon);

//                    isEnabled(position);

                    db = new FavoriteItemCS_DBController(context);
                    db.deleteFavoriteCS(itemList.get(position));
                    itemList.remove(position);
//                    notifyDataSetChanged();

                    Toast.makeText(context, R.string.item_toast_deleteFromFavorites, Toast.LENGTH_SHORT).show();
            }
        });

        return rowView;
    }

}

