package com.example.tommyhui.evcapplication.overview;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.OverviewListViewAdapter;
import com.example.tommyhui.evcapplication.database.DatabaseCS;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.search.SearchActivity;
import com.example.tommyhui.evcapplication.search.SearchItemActivity;
import com.example.tommyhui.evcapplication.adapter.OverviewPagerAdapter;

import java.util.ArrayList;

public class OverviewActivity extends ActionBarActivity{

    private MenuItem searchItem;
    private SearchView searchView;

    private OverviewPagerAdapter overviewPagerAdapter;
    private ToggleButton listToggle;
    private ToggleButton historyToggle;
    private ViewPager mViewPager;

    private DatabaseCS db;

    private String[] address;
    private String[] district;
    private String[] description;
    private String[] type;
    private String[] socket;
    private int[] quantity;

    public static ArrayList<ItemCS> ItemCSes = new ArrayList<>();
    public static ArrayList<ItemCS> QueryItemCSes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        /*To Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*To Set Up Action Bar's Title*/
        TextView title = (TextView)findViewById(R.id.action_bar_title);
        title.setText("Overview");

        /*To Set Up Action Bar's Icon*/
        ImageView myImgView = (ImageView)findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.overview_icon);



        /*To Set Up DatabaseCS*/
        db = new DatabaseCS(this);
        db.getWritableDatabase();

        address = getResources().getStringArray(R.array.address);
        district = getResources().getStringArray(R.array.district);
        description = getResources().getStringArray(R.array.description);
        type = getResources().getStringArray(R.array.type);
        socket = getResources().getStringArray(R.array.socket);
        quantity = getResources().getIntArray(R.array.quantity);

        for (int i = 0; i < address.length; i++) {

            db.addCS(new ItemCS(address[i], district[i], description[i], type[i], socket[i], quantity[i]));
            ItemCSes.add(i, new ItemCS(address[i], district[i], description[i], type[i], socket[i], quantity[i]));
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", ItemCSes);

//        Intent intent = new Intent(this, SearchActivity.class);
//
//        intent.putParcelableArrayListExtra("list", ItemCSes);
//        startActivity(intent);

        OverviewListFragmentActivity frag = new OverviewListFragmentActivity();
        frag.setArguments(bundle);

        /*To Set Up Viewpager*/
        overviewPagerAdapter = new OverviewPagerAdapter(
                getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.overview_pager);
        mViewPager.setAdapter(overviewPagerAdapter);

        listToggle = (ToggleButton) findViewById(R.id.overview_toggle_list);
        historyToggle = (ToggleButton) findViewById(R.id.overview_toggle_history);

        listToggle.setChecked(true);

        listToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {

                    historyToggle.setChecked(false);
                    mViewPager.setCurrentItem(0);
                    listToggle.setClickable(false);
                    historyToggle.setClickable(true);
                }
            }
        });

        historyToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {

                    listToggle.setChecked(false);
                    mViewPager.setCurrentItem(1);
                    historyToggle.setClickable(false);
                    listToggle.setClickable(true);
                }
            }
        });

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                if(position == 0){
                    listToggle.setChecked(true);
                }
                else{
                    historyToggle.setChecked(true);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);

        /*To Expand Search Bar*/
        searchItem = menu.findItem(R.id.overview_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(getResources().getString(R.string.overview_search_view_title));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            /*To Handle the Case When Texts on SearchView are changed*/
            public boolean onQueryTextChange(String query) {

//                setSuggestionForSearch(query);

                ListView mListView = (ListView) findViewById(R.id.overview_list_view);

                QueryItemCSes = db.searchListCSes(OverviewActivity.this, query);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, QueryItemCSes));
                mListView.setTextFilterEnabled(true);

                return false;
            }

            @Override
            /*To Handle the Case When texts on SearchView are submit*/
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                /*Clear the Query and Display the Result*/
                searchView.setQuery(query, false);
                searchView.clearFocus();

                ListView mListView = (ListView) findViewById(R.id.overview_list_view);

                QueryItemCSes = db.searchListCSes(OverviewActivity.this, query);
                mListView.setAdapter(new OverviewListViewAdapter(OverviewActivity.this, QueryItemCSes));
                mListView.setTextFilterEnabled(true);

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

                searchView.setQuery("", false);
                searchView.clearFocus();

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