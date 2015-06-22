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
import com.example.tommyhui.evcapplication.HomeActivity;
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

    public static ArrayList<ItemCS> ItemCSes = new ArrayList<ItemCS>();
    public static ArrayList<ItemCS> QueryItemCSes = new ArrayList<ItemCS>();

    private OverviewPagerAdapter overviewPagerAdapter;
    private ViewPager mViewPager;
    private ItemCS_DBController db = new ItemCS_DBController(this);
    private boolean searchBefore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.overview_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.overview_icon);

        /** Get the list of overview from MenuActivity **/
        ItemCSes = MenuActivity.ItemCSes;

        /** To set up the viewpager **/
        overviewPagerAdapter = new OverviewPagerAdapter(getSupportFragmentManager(), getBaseContext());

        mViewPager = (ViewPager) findViewById(R.id.overview_pager);
        mViewPager.setAdapter(overviewPagerAdapter);

        /** Give the PagerSlidingTabStrip the ViewPager **/
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.overview_tabs);
        tabsStrip.setViewPager(mViewPager);

        tabsStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if (position == 1) {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.overview_layout_status);
                    layout.setVisibility(View.GONE);
                }
                else if (position == 0 && searchBefore) {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.overview_layout_status);
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);

        if (menu != null) {
            menu.findItem(R.id.overview_action_delete).setVisible(false);
            menu.findItem(R.id.overview_action_search).setVisible(true);
        }

        // To expand search bar
        searchItem = menu.findItem(R.id.overview_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // To handle the case of clicking the Info Window in NearbyActivity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("query")) {
                String query = bundle.getString("query");
                searchView.setQuery(query, true);

                // To update the list of cs with the query
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                QueryItemCSes = db.inputQueryCSes(OverviewActivity.this, new String[]{query}, 1);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, QueryItemCSes));
                mListView.setTextFilterEnabled(true);
            }
        }

        // To set up a search hint for search bar
        searchView.setQueryHint(getResources().getString(R.string.overview_search_view_title));

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To go back to the view page of list
                mViewPager.setCurrentItem(0);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            /** To handle the case when texts on SearchView are changed*/
            public boolean onQueryTextChange(String query) {

                // To update the list of cs with the query
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                QueryItemCSes = db.inputQueryCSes(OverviewActivity.this, new String[]{query}, 1);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, QueryItemCSes));
                mListView.setTextFilterEnabled(true);

                return false;
            }

            @Override
            /** To handle the case when texts on SearchView are submit **/
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                // To go back to the view page of list
                mViewPager.setCurrentItem(0);

                // To put the query on the search bar
                searchViewQuery = query;
                searchView.setQuery(query, false);
                searchView.clearFocus();

                // To update the list of cs with the query
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                QueryItemCSes = db.inputQueryCSes(OverviewActivity.this, new String[]{query}, 1);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, QueryItemCSes));
                mListView.setTextFilterEnabled(true);

                // To show the search result
                TextView status = (TextView) findViewById(R.id.overview_text_status);
                Spanned result = Html.fromHtml("<strong>" + QueryItemCSes.size() + "</strong>" + getString(R.string.overview_search_result_text) + '"' + "<strong>" + query + "</strong>" + '"');
                status.setText(result);
                LinearLayout layout = (LinearLayout) findViewById(R.id.overview_layout_status);
                layout.setVisibility(View.VISIBLE);
                searchBefore = true;

                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            /** To handle the case when the user loses focus on SearchView **/
            public boolean onClose() {

                // To clear the query on the search bar
                searchView.setQuery("", false);
                searchView.clearFocus();

                // To display back the list of cs with the original list of cs
                ListView mListView = (ListView) findViewById(R.id.overview_list_view);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, ItemCSes));
                mListView.setTextFilterEnabled(true);
                LinearLayout layout = (LinearLayout) findViewById(R.id.overview_layout_status);
                layout.setVisibility(View.GONE);
                searchBefore = false;

                return false;
            }
        });
        return true;
    }
}