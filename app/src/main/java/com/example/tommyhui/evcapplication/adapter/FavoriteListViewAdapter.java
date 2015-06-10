package com.example.tommyhui.evcapplication.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS_DBController;

import java.util.ArrayList;

public class FavoriteListViewAdapter extends BaseAdapter {
    private final Context context;
    private FavoriteItemCS_DBController db;
    private ArrayList<FavoriteItemCS> itemList;

    public FavoriteListViewAdapter(Context context, ArrayList<FavoriteItemCS> itemList) {
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

    public ArrayList<FavoriteItemCS> getList() {
        return itemList;
    }

    public void setList(ArrayList<FavoriteItemCS> itemList) {
        this.itemList = itemList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.favorite_list_item_layout, parent, false);

        TextView address = (TextView) rowView.findViewById(R.id.favorite_list_view_item_address);
        TextView chargingStation = (TextView) rowView.findViewById(R.id.favorite_list_view_item_chargingStation);
        TextView type = (TextView) rowView.findViewById(R.id.favorite_list_view_item_type);
        TextView socket = (TextView) rowView.findViewById(R.id.favorite_list_view_item_socket);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.favorite_list_view_icon_favorite);

        address.setText(itemList.get(position).getAddress());
        chargingStation.setText(itemList.get(position).getDescription());
        type.setText(itemList.get(position).getType());
        socket.setText(itemList.get(position).getSocket());

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDeleteConfirmDialog(rowView, imageView, position);
            }
        });

        return rowView;
    }

    /** Remove the favorite item selected **/
    protected void removeListItem(View rowView, final int position) {
        final Animation animation = AnimationUtils.loadAnimation(
                context, android.R.anim.fade_out);
        rowView.startAnimation(animation);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {

            public void run() {
                itemList.remove(position);
                notifyDataSetChanged();
            }
        }, 1000);
    }

    /** Show confirm dialog for deletion **/
    private void showDeleteConfirmDialog(final View rowView, final ImageView imageView, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.favorites_alertDialog_delete_favorites_title);
        alertDialog.setMessage(R.string.favorites_alertDialog_delete_favorites_text);
        alertDialog.setNegativeButton(R.string.favorites_alertDialog_cancel_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton(R.string.favorites_alertDialog_remove_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                db = new FavoriteItemCS_DBController(context);
                db.deleteFavoriteCS(itemList.get(position));
                imageView.setImageResource(R.drawable.delfavorite_icon);
                removeListItem(rowView, position);

                Toast.makeText(context, R.string.item_toast_deleteFromFavorites, Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}

