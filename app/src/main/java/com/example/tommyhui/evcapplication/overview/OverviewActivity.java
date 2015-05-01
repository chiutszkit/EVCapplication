package com.example.tommyhui.evcapplication.overview;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.ChargingStationListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.MyDBHelper;

import java.util.ArrayList;

public class OverviewActivity extends ActionBarActivity {

    private MenuItem searchItem;
    private SearchView searchView;

    private MyDBHelper db;

    private String[] address;
    private String[] district;
    private String[] chargingStation;
    private String[] type;
    private String[] socket;
    private int[] quantity;

    private ArrayList<ItemCS> ItemCSes = new ArrayList<>();

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

        /*Set Up Database*/
        db = new MyDBHelper(this);
        db.getWritableDatabase();

        address = getResources().getStringArray(R.array.address);
        district = getResources().getStringArray(R.array.district);
        chargingStation = getResources().getStringArray(R.array.chargingStation);
        type = getResources().getStringArray(R.array.type);
        socket = getResources().getStringArray(R.array.socket);
        quantity = getResources().getIntArray(R.array.quantity);

        for (int i = 0; i < address.length; i++) {
            db.addCS(new ItemCS(address[i], district[i], chargingStation[i], type[i], socket[i], quantity[i]));
            ItemCSes.add(i, new ItemCS(address[i], district[i], chargingStation[i], type[i], socket[i], quantity[i]));
        }

        /*Display the list of overview*/
        ListView mListView = (ListView) findViewById(R.id.overview_list_view);
        mListView.setAdapter(new ChargingStationListViewAdapter(this, ItemCSes));
        mListView.setTextFilterEnabled(true);
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);


        /*Expand Search Bar*/
        searchItem = menu.findItem(R.id.overview_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        /*Hide dropdown pop up*/
        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
//        autoComplete.setDropDownHeight(0);

        searchView.setQueryHint(getResources().getString(R.string.overview_search_view_title));

//        Log.v("test-logging", "R.id.overview_action_search : " +  menu.findItem(R.id.overview_action_search));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            /*When texts are changed*/
            public boolean onQueryTextChange(String query) {
//                setSuggestionForSearch(query);
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                ArrayList<ItemCS> SearchResult = db.searchListCSes(OverviewActivity.this, query);
                mListView.setAdapter(new ChargingStationListViewAdapter(OverviewActivity.this, SearchResult));
                mListView.setTextFilterEnabled(true);
                return true;
            }
            @Override
            /*When texts are submit*/
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                /*Clear the Query and Display the Result*/
                searchView.setQuery(query, false);
                searchView.clearFocus();
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                ArrayList<ItemCS> SearchResult = db.searchListCSes(OverviewActivity.this, query);
                mListView.setAdapter(new ChargingStationListViewAdapter(OverviewActivity.this, SearchResult));
                mListView.setTextFilterEnabled(true);
                Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return true;
    }


//        protected void setSuggestionForSearch(String query) {
//            // TODO Auto-generated method stub
//
//            ArrayList<ItemCS> suggestionArray;
//            suggestionArray = getListOfChargingStations();
//
//            searchView.setSuggestionsAdapter(SearchUtil
//                    .getCursorAdapter(OverviewActivity.this, suggestionArray,
//                            query));
//
//            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
//
//                @Override
//                public boolean onSuggestionClick(int position) {
//
//                    showSearchResult(SearchUtil
//                            .getItemTag((MatrixCursor) searchView
//                                    .getSuggestionsAdapter().getItem(position)));
//                    return false;
//                }
//
//                @Override
//                public boolean onSuggestionSelect(int position) {
//                    return false;
//                }
//            });
//
//        }
//
//        private ArrayList<ItemCS> getListOfChargingStations() {
//            // TODO Auto-generated method stub
//
//            ArrayList<ItemCS> items = ItemCSes;
//
//            String[] isoCountries = Locale.getISOCountries();
//            for (int i = 0; i < items.size(); i++)
//                ItemCS item = items.get(i);
//
//                String address = items.get();
//                String chargingStation = items.getDisplayCountry();
//
//                if (!"".equals(name)) {
//                    items.add(name);
//                }
//            }
//
//            return items;
//
//        }
//        protected void showSearchResult(String itemTag) {
//            // TODO Auto-generated method stub
//
//            /*Clear the Query and Display the Result*/
//            searchView.setQuery("", false);
//            searchView.clearFocus();
//            searchView.onActionViewCollapsed();
//
//            Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
//        }

}