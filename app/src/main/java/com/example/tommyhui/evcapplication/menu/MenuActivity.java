package com.example.tommyhui.evcapplication.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.HomeActivity;
import com.example.tommyhui.evcapplication.JSONParser.RealTimeStatusJSONParser;
import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.about.AboutActivity;
import com.example.tommyhui.evcapplication.charger.ChargerActivity;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS_DBController;
import com.example.tommyhui.evcapplication.database.HistoryItemCS;
import com.example.tommyhui.evcapplication.database.HistoryItemCS_DBController;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.favourite.FavoriteActivity;
import com.example.tommyhui.evcapplication.nearby.NearbyActivity;
import com.example.tommyhui.evcapplication.overview.OverviewActivity;
import com.example.tommyhui.evcapplication.realtime.RealTimeActivity;
import com.example.tommyhui.evcapplication.search.SearchActivity;
import com.example.tommyhui.evcapplication.util.ConnectionDetector;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MenuActivity extends ActionBarActivity {

    public static ArrayList<ItemCS> ItemCSes = new ArrayList<ItemCS>();
    public static ArrayList<ItemCS> realTimeInfoList = new ArrayList<ItemCS>();
    public static ArrayList <String> realTimeQuantityList = new ArrayList<String>();

    private Set<String> list_ID_favorite = new HashSet<String>();
    private Set<String> list_ID_history = new HashSet<String>();
    private RealTimeStatusJSONParser jsonParser = new RealTimeStatusJSONParser();

    private ConnectionDetector cd = new ConnectionDetector(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.menu_title_menu);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.evc_icon);

        /** Set up menu page's button **/
        final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.menu_grid_overview:
                        Intent overviewIntent = new Intent();
                        overviewIntent.setClass(MenuActivity.this, OverviewActivity.class);
                        startActivity(overviewIntent);
                        break;
                    case R.id.menu_grid_search:
                        Intent searchPlaceIntent = new Intent();
                        searchPlaceIntent.setClass(MenuActivity.this, SearchActivity.class);
                        startActivity(searchPlaceIntent);
                        break;
                    case R.id.menu_grid_favourite:
                        Intent favoriteIntent = new Intent();
                        favoriteIntent.setClass(MenuActivity.this, FavoriteActivity.class);
                        startActivity(favoriteIntent);
                        break;
                    case R.id.menu_grid_nearby:
                        Intent nearestIntent = new Intent();
                        nearestIntent.setClass(MenuActivity.this, NearbyActivity.class);
                        startActivity(nearestIntent);
                        break;
                    case R.id.menu_grid_socket:
                        Intent socketIntent = new Intent();
                        socketIntent.setClass(MenuActivity.this, ChargerActivity.class);
                        startActivity(socketIntent);
                        break;
                    case R.id.menu_grid_realTime:
                        Intent realTimeIntent = new Intent();
                        realTimeIntent.setClass(MenuActivity.this, RealTimeActivity.class);
                        startActivity(realTimeIntent);
                        break;
                    default:
                }
            }
        };

        findViewById(R.id.menu_grid_overview).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_search).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_favourite).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_nearby).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_socket).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_realTime).setOnClickListener(mGlobal_OnClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** Load the data of Real Time Status **/
        if (cd.isConnectingToInternet())
            new LoadAllStatus().execute();
    }

    /** Background Async Task to Load all real time status by making HTTP Request **/
    class LoadAllStatus extends AsyncTask<String, String, String> {

        /**
         * Getting All real time status from url *
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            realTimeQuantityList.clear();

            jsonParser.makeHttpRequest(HomeActivity.url_check_status, "GET", params);
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(HomeActivity.url_get_all_charging_station, "GET", params);

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(HomeActivity.TAG_SUCCESS);

                if (success == 1) {
                    // real time charging station found
                    // Getting Array of real time status
                    JSONArray status = json.getJSONArray(HomeActivity.TAG_TABLE_CHARGING_STATION);

                    // looping through All real time status
                    for (int i = 0; i < status.length(); i++) {
                        JSONObject c = status.getJSONObject(i);

                        // Storing each json item in variable
                        String quantity = c.getString(HomeActivity.TAG_CHARGINGSTATION_QUANTITY);

                        // adding to ArrayList
                        realTimeQuantityList.add(i, quantity);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /** Share the information of this application to others **/
    public void shareApp() {
        // Create the send intent
        Intent shareIntent =
                new Intent(android.content.Intent.ACTION_SEND);

        // Set the type
        shareIntent.setType("text/plain");

        // Add a subject
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                getString(R.string.share_subject));

        // Build the body of the message to be shared
        String shareMessage = getString(R.string.app_name) + getString(R.string.share_message);

        // Add the message
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareMessage);

        // Start the chooser for sharing
        startActivity(Intent.createChooser(shareIntent,
                getString(R.string.share_hint)));
    }

    /** Save the current user data **/
    public void saveUserSetting() {

        SharedPreferences sharedPref = getSharedPreferences("UserConfigs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Save user's favorite items
        FavoriteItemCS_DBController db_FavoriteItemCS = new FavoriteItemCS_DBController(this);
        ArrayList<FavoriteItemCS> favoriteItemCS_list = db_FavoriteItemCS.getAllFavoriteCSes();
        list_ID_favorite = new HashSet<String>();

        for(int i = 0; i < favoriteItemCS_list.size(); i++)
            for(int j = 0; j < HomeActivity.matchingList.size(); j++) {
                ItemCS cs1 = HomeActivity.matchingList.get(j);
                ItemCS cs2 = favoriteItemCS_list.get(i);
                if (cs1.getAddress().equals(cs2.getAddress()) && cs1.getDistrict().equals(cs2.getDistrict())
                        && cs1.getDescription().equals(cs2.getDescription()) && cs1.getSocket().equals(cs2.getSocket())
                        && cs1.getType().equals(cs2.getType()) && cs1.getQuantity() == cs2.getQuantity())
                    list_ID_favorite.add(j + "");
            }
        if(list_ID_favorite.size() > 0) {
            editor.putStringSet("favoriteList", list_ID_favorite);
            editor.commit();
        }

        // Save user's history items
        HistoryItemCS_DBController db_HistoryItemCS = new HistoryItemCS_DBController(this);
        ArrayList<HistoryItemCS> historyItemCS_list = db_HistoryItemCS.getAllHistoryCSes();
        list_ID_history = new HashSet<String>();

        for(int i = 0; i < historyItemCS_list.size(); i++)
            for(int j = 0; j < HomeActivity.matchingList.size(); j++) {
                ItemCS cs1 = HomeActivity.matchingList.get(j);
                ItemCS cs2 = historyItemCS_list.get(i);
                if (cs1.getAddress().equals(cs2.getAddress()) && cs1.getDistrict().equals(cs2.getDistrict())
                        && cs1.getDescription().equals(cs2.getDescription()) && cs1.getSocket().equals(cs2.getSocket())
                        && cs1.getType().equals(cs2.getType()) && cs1.getQuantity() == cs2.getQuantity())
                    list_ID_history.add(j + "");
            }
        if(list_ID_history.size() > 0) {
            editor.putStringSet("historyList", list_ID_history);
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu, menu);

        SharedPreferences sharedPref = getSharedPreferences("UserConfigs", Context.MODE_PRIVATE);

        if(sharedPref.getString("language","no").equals("en")) {
            menu.findItem(R.id.menu_options_language).setTitle(getString(R.string.menu_optionsmenu_language) + ": English");
        }
        else {
            menu.findItem(R.id.menu_options_language).setTitle(getString(R.string.menu_optionsmenu_language) + ": 繁體中文");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        SharedPreferences sharedPref = getSharedPreferences("UserConfigs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (item.getItemId()) {
            case R.id.menu_options_share:
                shareApp();
                return true;

            case R.id.menu_options_language:
                if(!sharedPref.getString("language","no").equals("en"))
                    editor.putString("language", "en");
                else
                    editor.putString("language", "zh");

                editor.apply();

                saveUserSetting();

                Intent homeIntent = new Intent();
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                homeIntent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(homeIntent);
                finish();
                return true;

            case R.id.menu_options_about:
                Intent aboutIntent = new Intent();
                aboutIntent.setClass(MenuActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}