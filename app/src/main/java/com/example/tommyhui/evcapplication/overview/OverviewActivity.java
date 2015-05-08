package com.example.tommyhui.evcapplication.overview;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.SlidingTab.SlidingTabLayout;
import com.example.tommyhui.evcapplication.adapter.OverviewListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.MyDBHelper;
import com.example.tommyhui.evcapplication.search.SearchItemActivity;

import java.util.ArrayList;

public class OverviewActivity extends ActionBarActivity{

    private MenuItem searchItem;
    private SearchView searchView;

    private MyDBHelper db;

    private String[] address;
    private String[] district;
    private String[] description;
    private String[] type;
    private String[] socket;
    private int[] quantity;

    private ArrayList<ItemCS> ItemCSes = new ArrayList<>();
    private ArrayList<ItemCS> QueryItemCSes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        /*To Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*To Set Action Bar's Title*/
        TextView title = (TextView)findViewById(R.id.action_bar_title);
        title.setText("Overview");

        /*To Set Action Bar's Icon*/
        ImageView myImgView = (ImageView)findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.overview_icon);

        /** Getting a reference to ViewPager from the layout */
        final ViewPager pager = (ViewPager) findViewById(R.id.overview_pager);
        SlidingTabLayout tab= (SlidingTabLayout) findViewById(R.id.overview_sliding_tabs);

        MyFragmentPagerAdapter adapter= new MyFragmentPagerAdapter();
        pager.setAdapter(adapter);
        tab.setViewPager(pager);

        final int[] colors={0xFF123456,0xFF654321};

        tab.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.light_green);
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(R.color.black);
            }
        });
        /*To Set Up Database*/
        db = new MyDBHelper(this);
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

        QueryItemCSes = ItemCSes;

        /*To Display the List of Overview*/
        ListView listview = (ListView) findViewById(R.id.overview_list_view);
        listview.setAdapter(new OverviewListViewAdapter(this, ItemCSes));
        listview.setTextFilterEnabled(true);

        /*To Handle the Click of item of a CS*/
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ItemCS cs = QueryItemCSes.get(position);

                Intent searchItemIntent = new Intent();
                searchItemIntent.setClass(OverviewActivity.this, SearchItemActivity.class);

                Bundle bundle = new Bundle();

                bundle.putString("address", cs.getAddress());
                bundle.putString("description", cs.getDescription());
                bundle.putString("type", cs.getType());
                bundle.putString("socket", cs.getSocket());
                bundle.putInt("quantity", cs.getQuantity());

                searchItemIntent.putExtras(bundle);
                startActivity(searchItemIntent);
            }
        });
    }

    public class MyFragmentPagerAdapter extends PagerAdapter {

        String[] titles={"List","History"};

        ArrayList<LinearLayout> layouts=new ArrayList<LinearLayout>();
        MyFragmentPagerAdapter() {

            int[] colors={0xFF123456,0xFF654321};
            for (int i = 0; i < 2; i++) {
                LinearLayout l=new LinearLayout(OverviewActivity.this);
                l.setBackgroundColor(colors[i]);
                l.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                layouts.add(l);
            }

        }

        @Override
        public int getCount() {
            return layouts.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view==o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LinearLayout l=layouts.get(position);
            container.addView(l);
            return l;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(layouts.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
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