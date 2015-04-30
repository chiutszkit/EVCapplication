package com.example.tommyhui.evcapplication.overview;

import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.utils.SearchUtil;

import java.util.ArrayList;
import java.util.Locale;

public class OverviewActivity extends ActionBarActivity {

    private MenuItem searchItem;
    private SearchView searchView;

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


//        mListView = (ListView) findViewById(R.id.list_view);
//        mListView.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                mStrings));
//        mListView.setTextFilterEnabled(true);
//        setupSearchView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);

//        // Get the SearchView and set the overview_searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        /*Expand Search Bar*/
        searchItem = menu.findItem(R.id.overview_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.overview_search_view_title));

//        Log.v("test-logging", "R.id.overview_action_search : " +  menu.findItem(R.id.overview_action_search));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            /*When texts are changed*/
            public boolean onQueryTextChange(String query) {
                setSuggestionForSearch(query);
                return true;
            }
            @Override
            /*When texts are submit*/
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                /*Clear the Query and Display the Result*/
                searchView.setQuery("", false);
                searchView.clearFocus();

                Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return true;
    }


        protected void setSuggestionForSearch(String query) {
            // TODO Auto-generated method stub
            ArrayList<String> suggestionArray;

            suggestionArray = getListOfCountries();

            searchView.setSuggestionsAdapter(SearchUtil
                    .getCursorAdapter(OverviewActivity.this, suggestionArray,
                            query));

            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

                @Override
                public boolean onSuggestionClick(int position) {

                    showSearchResult(SearchUtil
                            .getItemTag((MatrixCursor) searchView
                                    .getSuggestionsAdapter().getItem(position)));
                    return false;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }
            });

        }

        private ArrayList<String> getListOfCountries() {
            // TODO Auto-generated method stub

            ArrayList<String> items = new ArrayList<String>();

            String[] isoCountries = Locale.getISOCountries();
            for (String country : isoCountries) {
                Locale locale = new Locale("en", country);
        //          String iso = locale.getISO3Country();
        //          String code = locale.getCountry();
                String name = locale.getDisplayCountry();

                if (!"".equals(name)) {
                    items.add(name);
                }
            }

            return items;

        }
        protected void showSearchResult(String itemTag) {
            // TODO Auto-generated method stub

            /*Clear the Query and Display the Result*/
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.onActionViewCollapsed();

            Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        }

//        // Associate overview_searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.overview_action_search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));


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