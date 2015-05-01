package com.example.tommyhui.evcapplication.overview;

import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.tommyhui.evcapplication.database.MyDBHelper;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.utils.SearchUtil;

import java.util.ArrayList;
import java.util.List;
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

        /*Set Up Database*/
        MyDBHelper db = new MyDBHelper(this);
        db.getWritableDatabase();

        db.addCS(new ItemCS("2 Rumsey Street, Central","Rumsey Street Car Park","Standard - 13A","EV Charger with Single Socket",30));
        db.addCS(new ItemCS("2 Rumsey Street, Central","Rumsey Street Car Park","Standard - 13A","EV Charger with Dual Sockets",5));
        db.addCS(new ItemCS("2 Murray Road, Central","Murray Road Car Park","Standard - 13A","EV Charger with Single Socket",29));
        db.addCS(new ItemCS("2 Murray Road, Central","Murray Road Car Park","Standard - 13A","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("66 Queensway, Admiralty","Queensway Government Offices","Standard - 13A","EV Charger with Single Socket",14));
        db.addCS(new ItemCS("66 Queensway, Admiralty","Queensway Government Offices","Standard - 13A","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("Oi Tung Estate, Shau Kei Wan","Oi Tung Estate","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("Lau Sin Street & Electric Road, Tin Hau","Tin Hau Car Park","Standard - 13A","EV Charger with Single Socket",32));
        db.addCS(new ItemCS("Lau Sin Street & Electric Road, Tin Hau","Tin Hau Car Park","Standard - 13A","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("18 Taikoo Shing Road, Taikoo Shing","Cityplaza","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("148 Electric Road, North Point","148 Electric Road","Standard - 13A","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("50 Wing Tai Road, Chai Wan","Cornell Centre","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("28 Siu Sai Wan Road, Chai Wan","Island Resort Mall ","Standard - 13A","EV Charger with Single Socket",4));
        db.addCS(new ItemCS("68 Hing Man Street, Shau Kei Wan","Marina House","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("22 Westlands Road, Quarry Bay","Oxford House ","Standard - 13A","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("979 King's Road, Quarry Bay","Dorset House","Standard - 13A","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("Taikoo Place, Quarry Bay","One Island East ","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("1 Kornhill Road, Tai koo","Kornhill Plaza (south)","Standard - 13A","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("29-31 Cheung Lee Street, Chai Wan","Trend Centre ","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("33 Java Road, North Point","North Point Government Offices","Standard - 13A","EV Charger with Single Socket",9));
        db.addCS(new ItemCS("33 Java Road, North Point","North Point Government Offices","Standard - 13A","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("1 Po Man Street, Shau Kei Wan","Shau Kei Wan Car Park","Standard - 13A","EV Charger with Single Socket",30));
        db.addCS(new ItemCS("1 Po Man Street, Shau Kei Wan","Shau Kei Wan Car Park","Standard - 13A","EV Charger with Dual Sockets",5));
        db.addCS(new ItemCS("183 Electric Road, North Point","AIA Tower ","Standard - 13A","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("Shing Tai Road, Paradise Mall","Paradise Mall (West)","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("2 - 38 Lai Tak Tsuen Road, Tai Hang","Lai Tak Tsuen","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("100 Cyberport Road, Cyberport","Cyberport Tower 2","Standard - 13A","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("12 Broadwood Road, Happy Valley","Broadwood Twelve","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("23 Harbour Road, Wan Chai","Great Eagle Centre","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("183 Queen's Road East, Wan Chai","Hopewell Centre ","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("213 Queen's Road East, Wan Chai","Wu Chung House ","Standard - 13A","EV Charger with Single Socket",9));
        db.addCS(new ItemCS("1 Stadium Path, Causeway Bay","Olympic House ","Standard - 13A","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("255-257 Gloucester Road, Causeway Bay","Sino Plaza","Standard - 13A","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("1 Matheson Street, Causeway Bay","Times Square","Standard - 13A","EV Charger with Single Socket",16));
        db.addCS(new ItemCS("7 Gloucester Road, Wan Chai","Wanchai Tower and Immigration Tower","Standard - 13A","EV Charger with Single Socket",18));
        db.addCS(new ItemCS("7 Gloucester Road, Wan Chai","Wanchai Tower and Immigration Tower","Standard - 13A","EV Charger with Dual Sockets",12));
        db.addCS(new ItemCS("23 Harbour Road, Wan Chai","Convention Plaza (Harbour Road) Carpark ","Standard - 13A","EV Charger with Single Socket",3));
        db.addCS(new ItemCS("1 Expo Drive, Wanchai","Convention Plaza (Expo Drive Central Road) Carpark ","Standard - 13A","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("6-8 Harbour Road, Wan Chai","Shui On Centre","Standard - 13A","EV Charger with Dual Sockets",1));
        db.addCS(new ItemCS("9 Edinburgh Place, Central","Star Ferry Car Park","Medium - IEC 62196","EV Charger with Dual Sockets",8));
        db.addCS(new ItemCS("1 Edinburgh Place, Central","City Hall Car Park","Medium - IEC 62196","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("2 Rumsey Street, Sheung Wan","Rumsey Street Car Park","Medium - IEC 62196","EV Charger with Dual Sockets",5));
        db.addCS(new ItemCS("2 Murray Road, Central","Murray Road Car Park","Medium - IEC 62196","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("66 Queensway, Admiralty","Queensway Government Offices","Medium - IEC 62196","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("1 Harbour View Street, Central","One International Finance Centre","Medium - IEC 62196","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("8 Finance Street, Central","Two International Finance Centre","Medium - IEC 62196","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("Lau Sin Street & Electric Road, Tin Hau","Tin Hau Car Park","Medium - IEC 62196","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("683 King's Road, Quarry Bay","Kerry Centre","Medium - IEC 62196","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("33 Java Road, North Point","North Point Government Offices","Medium - IEC 62196","EV Charger with Dual Sockets",6));
        db.addCS(new ItemCS("1 Po Man Street, Shau Kei Wan","Shau Kei Wan Car Park","Medium - IEC 62196","EV Charger with Dual Sockets",5));
        db.addCS(new ItemCS("7 Gloucester Road, Wan Chai","Wanchai Tower and Immigration Tower","Medium - IEC 62196","EV Charger with Dual Sockets",12));
        db.addCS(new ItemCS("6-8 Harbour Road, Wan Chai","Shui On Centre","Medium - SAE J1772","EV Charger with Dual Sockets",1));
        db.addCS(new ItemCS("100 Cyberport Road, Cyberport 1","Le Meridien Cyberport","Medium - Others","EV Charger with Single Socket",1));
        db.addCS(new ItemCS("183 Queen's Road East, Wan Chai","Hopewell Centre","Medium - Others","EV Charger with Single Socket",2));
        db.addCS(new ItemCS("100 Cyberport Road, Cyberport","Cyberport Tower 2","Quick - Others","EV Charger with Single Socket",6));
        db.addCS(new ItemCS("183 Queen's Road East, Wan Chai","Hopewell Centre","Quick - Others","EV Charger with Single Socket",6));
        db.addCS(new ItemCS("2-38 Yun Ping Road, Causeway Bay","Lee Garden 2 ","Quick - Others","EV Charger with Single Socket",2));

        // get all CSes
        List<ItemCS> list = db.getAllCSes();
        Toast.makeText(getApplicationContext(), list.get(0).toString(), Toast.LENGTH_SHORT).show();
        //輔助類名

//        ListView mListView = (ListView) findViewById(R.id.list_view);
//        mListView.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                mStrings));
//        mListView.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);



        /*Expand Search Bar*/
        searchItem = menu.findItem(R.id.overview_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setMinimumWidth(10000);
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

            suggestionArray = getListOfChargingStations();

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

        private ArrayList<String> getListOfChargingStations() {
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

}