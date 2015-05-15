package com.example.tommyhui.evcapplication.socket;

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
import com.example.tommyhui.evcapplication.adapter.SocketListPageListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.nearby.NearbyActivity;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SocketListActivity extends ActionBarActivity {

    private String type = "";
    private ArrayList<ItemCS> socketList = new ArrayList<>();
    private SocketListPageListViewAdapter socketListPageListViewAdapter;
    private ItemCS_DBController db;
    private StickyListHeadersListView  listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_listpage_activity);

        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*Set Action Bar's Title*/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(type + " Charger");

        /*Set Action Bar's Icon*/
        int resId = 0;
        switch (type) {
            case "Standard":
                resId = R.drawable.standard_icon;
                break;
            case "Medium":
                resId = R.drawable.medium_icon;
                break;
            case "Quick":
                resId = R.drawable.quick_icon;
                break;
            default:
                break;
        }
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(resId);

        db = new ItemCS_DBController(getApplicationContext());
        socketList = db.inputQueryCSes(this, new String[] {type}, 1);

        listView = (StickyListHeadersListView) this.findViewById(R.id.socket_listpage_list_view);

        if (socketListPageListViewAdapter == null) {
            socketListPageListViewAdapter = new SocketListPageListViewAdapter(SocketListActivity.this, socketList);
            listView.setAdapter(socketListPageListViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    // TODO Auto-generated method stub

                    ItemCS cs = socketList.get(position);

                    Intent intent = new Intent();
                    intent.setClass(SocketListActivity.this, NearbyActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putString("type", type);
                    bundle.putString("address", cs.getAddress());
                    bundle.putString("description", cs.getDescription());

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_options_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
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
