package com.example.tommyhui.evcapplication.overview;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import android.widget.SearchView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;

public class OverviewActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*Set Action Bar's Title*/
        TextView title = (TextView)findViewById(R.id.action_bar_title);
        title.setText("Overview");

        /*Set Action Bar's Icon*/
        ImageView myImgView = (ImageView)findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.overview_icon);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);

        // Get the SearchView and set the overviewSearchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchItem = menu.findItem(R.id.overview_action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        SearchView searchView = (SearchView) menu.findItem(R.id.overview_action_search);
        Log.v("test-logging", "R.id.overview_action_search : " +  menu.findItem(R.id.overview_action_search));


        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

//        // Associate overviewSearchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.overview_action_search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.overview_action_search) {
            onSearchRequested();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}