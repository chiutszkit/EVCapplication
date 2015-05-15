package com.example.tommyhui.evcapplication.search;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.SearchResultListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.overview.ItemCSActivity;

import java.util.ArrayList;

public class SearchResultActivity extends ActionBarActivity {

    private String district;
    private String description;
    private String type;
    private String socket;
    private String quantity;

    private ArrayList<ItemCS> searchResultList = new ArrayList<>();
    private ItemCS_DBController db;

    private SearchResultListViewAdapter searchResultListViewAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*Set Action Bar's Title*/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Search Result");

        /*Set Action Bar's Icon*/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.filter_icon);

        Bundle bundle = getIntent().getExtras();
        district = bundle.getString("district");
        description = bundle.getString("description");
        type = bundle.getString("type");
        socket = bundle.getString("socket");
        quantity = bundle.getString("quantity");

        db = new ItemCS_DBController(getApplicationContext());
        searchResultList = db.inputQueryCSes(this, new String[] {district, description, type, socket, quantity}, 1);

        listView = (ListView) this.findViewById(R.id.searchresult_list_view);

        searchResultListViewAdapter = new SearchResultListViewAdapter(SearchResultActivity.this, searchResultList);
        listView.setAdapter(searchResultListViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // TODO Auto-generated method stub

                ItemCS cs = searchResultList.get(position);

                Intent intent = new Intent();
                intent.setClass(SearchResultActivity.this, ItemCSActivity.class);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
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