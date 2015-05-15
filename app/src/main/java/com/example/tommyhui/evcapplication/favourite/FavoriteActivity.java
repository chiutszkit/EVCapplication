package com.example.tommyhui.evcapplication.favourite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.FavoriteListViewAdapter;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS_DBController;
import com.example.tommyhui.evcapplication.menu.MenuActivity;
import com.example.tommyhui.evcapplication.overview.ItemCSActivity;
import com.example.tommyhui.evcapplication.overview.OverviewActivity;
import com.example.tommyhui.evcapplication.search.SearchActivity;

import java.util.ArrayList;

public class FavoriteActivity extends ActionBarActivity {

    private ArrayList<FavoriteItemCS> favoriteList = new ArrayList<>();
    private FavoriteListViewAdapter favoriteListViewAdapter;
    private FavoriteItemCS_DBController db;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_activity);

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*Set Action Bar's Title*/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Favourite");

        /*Set Action Bar's Icon*/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.favorite_icon);

        /*Set Up the Database of FavoriteItem*/
        db = new FavoriteItemCS_DBController(getApplicationContext());
        if (db.getFavoriteCSCount() == 0) {
            showEmptyListDialog();
        }
        favoriteList = db.getAllFavoriteCSes();
    }

    public void onResume() {
        super.onResume();

        listView = (ListView) this.findViewById(R.id.favourite_list_view);

        if (favoriteListViewAdapter == null) {
            favoriteListViewAdapter = new FavoriteListViewAdapter(FavoriteActivity.this, favoriteList);
            listView.setAdapter(favoriteListViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    // TODO Auto-generated method stub

                    FavoriteItemCS cs = favoriteList.get(position);

                    Intent intent = new Intent();
                    intent.setClass(FavoriteActivity.this, ItemCSActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putString("address", cs.getAddress());
                    bundle.putString("description", cs.getDescription());
                    bundle.putString("type", cs.getType());
                    bundle.putString("socket", cs.getSocket());
                    bundle.putInt("quantity", cs.getQuantity());

                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });
        } else {
            favoriteListViewAdapter.setList(favoriteList);
            favoriteListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showEmptyListDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.favorites_alertDialog_no_favorites_title);
        alertDialog.setMessage(R.string.favorites_alertDialog_no_favorites_text);
        alertDialog.setNeutralButton(R.string.favorites_alertDialog_ok_option, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                Intent intent = new Intent();
                intent.setClass(FavoriteActivity.this, MenuActivity.class);
                startActivity(intent);
                FavoriteActivity.this.finish();
            }
        });

        alertDialog.setNegativeButton(R.string.favorites_alertDialog_select_option, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                Intent intent = new Intent();
                intent.setClass(FavoriteActivity.this, OverviewActivity.class);
                startActivity(intent);
                FavoriteActivity.this.finish();
            }
        });

        alertDialog.setPositiveButton(R.string.favorites_alertDialog_search_option, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                Intent intent = new Intent();
                intent.setClass(FavoriteActivity.this, SearchActivity.class);
                startActivity(intent);
                FavoriteActivity.this.finish();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
