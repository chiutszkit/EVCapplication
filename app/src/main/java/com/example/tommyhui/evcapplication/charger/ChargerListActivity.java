package com.example.tommyhui.evcapplication.charger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.ChargerListPageListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.menu.MenuActivity;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ChargerListActivity extends ActionBarActivity {

    private String type;
    private ArrayList<ItemCS> chargerList = new ArrayList<ItemCS>();
    private ChargerListPageListViewAdapter chargerListPageListViewAdapter;
    private ItemCS_DBController db;
    private StickyListHeadersListView  listView;
    private ArrayList<ItemCS> realTimeInfoList = new ArrayList<ItemCS>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charger_listpage_activity);

        /** Get the data passed **/
        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(type + getString(R.string.charger_list_title));

        /** Set up action bar's icon **/
        int resId = 0;

        String standard = getString(R.string.charger_list_title_standard);
        String medium = getString(R.string.charger_list_title_medium);
        String quick = getString(R.string.charger_list_title_quick);

        if (type.equals(standard))
            resId = R.drawable.standard_icon;
        else if(type.equals(medium))
            resId = R.drawable.medium_icon;
        else if(type.equals(quick))
            resId = R.drawable.quick_icon;

        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(resId);

        db = new ItemCS_DBController(getApplicationContext());
        chargerList = db.inputQueryCSes(this, new String[] {type}, 1);
        listView = (StickyListHeadersListView) this.findViewById(R.id.charger_listpage_list_view);
        realTimeInfoList = MenuActivity.realTimeInfoList;

        if (chargerListPageListViewAdapter == null) {
            chargerListPageListViewAdapter = new ChargerListPageListViewAdapter(ChargerListActivity.this, chargerList, realTimeInfoList);
            listView.setAdapter(chargerListPageListViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    ItemCS cs = chargerList.get(position);

                    Intent intent = new Intent();
                    intent.setClass(ChargerListActivity.this, ChargerListItemActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putString("type", type);
                    bundle.putString("latitude", cs.getLatitude());
                    bundle.putString("longitude", cs.getLongitude());

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
