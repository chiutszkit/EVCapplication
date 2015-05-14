package com.example.tommyhui.evcapplication.overview;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.OverviewListViewAdapter;
import com.example.tommyhui.evcapplication.adapter.OverviewPagerAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.menu.MenuActivity;

import java.util.ArrayList;

public class OverviewActivity extends ActionBarActivity {

    public static MenuItem searchItem;
    public static SearchView searchView;
    public static String searchViewQuery = "";
    public static ArrayList<ItemCS> ItemCSes = new ArrayList<>();
    public static ArrayList<ItemCS> QueryItemCSes = new ArrayList<>();
    private OverviewPagerAdapter overviewPagerAdapter;
    private ViewPager mViewPager;
    private ItemCS_DBController db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        /*To Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*To Set Up Action Bar's Title*/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Overview");

        /*To Set Up Action Bar's Icon*/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.overview_icon);

        /*Get the Overview List from MenuActivity*/
        ItemCSes = MenuActivity.ItemCSes;
//        ItemCSes = getIntent().getParcelableArrayListExtra("list");

        /*To Set Up Viewpager*/
        overviewPagerAdapter = new OverviewPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.overview_pager);
        mViewPager.setAdapter(overviewPagerAdapter);

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.overview_tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(mViewPager);

        tabsStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if (position == 1) {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.overview_layout_status);
                    layout.setVisibility(View.GONE);
                }
            }
        });

        db = new ItemCS_DBController(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);

        if (menu != null) {
            menu.findItem(R.id.overview_action_delete).setVisible(false);
            menu.findItem(R.id.overview_action_search).setVisible(true);
        }

        /*To Expand Search Bar*/
        searchItem = menu.findItem(R.id.overview_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        /*To Set Up a Search Hint for Search Bar*/
        searchView.setQueryHint(getResources().getString(R.string.overview_search_view_title));

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*To Go back to the view page of LIST*/
                mViewPager.setCurrentItem(0);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            /*To Handle the Case When Texts on SearchView are changed*/
            public boolean onQueryTextChange(String query) {

//                setSuggestionForSearch(query);

                /*To Update the List of CS with the Query*/
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                QueryItemCSes = db.inputQueryCSes(OverviewActivity.this, query);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, QueryItemCSes));
                mListView.setTextFilterEnabled(true);

                return false;
            }

            @Override
            /*To Handle the Case When texts on SearchView are submit*/
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                /*To Go back to the view page of LIST*/
                mViewPager.setCurrentItem(0);

                /*To Put the Query on the Search Bar*/
                searchViewQuery = query;
                searchView.setQuery(query, false);
                searchView.clearFocus();

                /*To Update the List of CS with the Query*/
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                QueryItemCSes = db.inputQueryCSes(OverviewActivity.this, query);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, QueryItemCSes));
                mListView.setTextFilterEnabled(true);

                /*To Show the search result*/
                TextView status = (TextView) findViewById(R.id.overview_text_status);
                Spanned result = Html.fromHtml("<strong>" + QueryItemCSes.size() + "</strong>" + " charging stations for " + '"' + "<strong>" + query + "</strong>" + '"');
                status.setText(result);
                LinearLayout layout = (LinearLayout) findViewById(R.id.overview_layout_status);
                layout.setVisibility(View.VISIBLE);

                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            /*To Handle the Case When the User Loses Focus on SearchView*/
            public boolean onClose() {

                /*To Clear the Query on the Search Bar*/
                searchView.setQuery("", false);
                searchView.clearFocus();

                /*To Display back the List of CS with the Original List of CS*/
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, ItemCSes));
                mListView.setTextFilterEnabled(true);
                LinearLayout layout = (LinearLayout) findViewById(R.id.overview_layout_status);
                layout.setVisibility(View.GONE);

                return false;
            }
        });
        return true;

//        /*Hide dropdown pop up of  SearchView*/
//        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
//        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
//        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
//        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
//        autoComplete.setDropDownHeight(0);
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
//                String description = items.getDisplayCountry();
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