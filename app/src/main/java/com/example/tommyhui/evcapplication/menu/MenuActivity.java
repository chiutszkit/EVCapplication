package com.example.tommyhui.evcapplication.menu;

import android.content.Intent;
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

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.about.AboutActivity;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.favourite.FavoriteActivity;
import com.example.tommyhui.evcapplication.nearby.NearbyActivity;
import com.example.tommyhui.evcapplication.overview.OverviewActivity;
import com.example.tommyhui.evcapplication.realtime.RealTimeActivity;
import com.example.tommyhui.evcapplication.search.SearchActivity;
import com.example.tommyhui.evcapplication.setting.SettingActivity;
import com.example.tommyhui.evcapplication.socket.SocketActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class MenuActivity extends ActionBarActivity {

    public static ArrayList<ItemCS> ItemCSes = new ArrayList<>();

    private ItemCS_DBController db;
    private String[] address;
    private String[] district;
    private String[] description;
    private String[] type;
    private String[] socket;
    private int[] quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        //        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

        /*Set Action Bar's Title*/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Menu");

        /*Set Action Bar's Icon*/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.evc_icon);

        /*To Set Up ItemCS_DBController*/
        db = new ItemCS_DBController(this);

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
        final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.menu_grid_overview:
                        Intent overviewIntent = new Intent();
                        overviewIntent.setClass(MenuActivity.this, OverviewActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("list", ItemCSes);
                        overviewIntent.putExtras(bundle);

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
                        socketIntent.setClass(MenuActivity.this, SocketActivity.class);
                        startActivity(socketIntent);
                        break;
                    case R.id.menu_grid_icon_realTime:
                        Intent realTimeIntent = new Intent();
                        realTimeIntent.setClass(MenuActivity.this, RealTimeActivity.class);
                        startActivity(realTimeIntent);
                        break;
                    default:
                }
            }
        };
        /*Set Menu Page Button*/
        findViewById(R.id.menu_grid_overview).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_search).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_favourite).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_nearby).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_socket).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_icon_realTime).setOnClickListener(mGlobal_OnClickListener);
    }

    public void shareApp() {
        //create the send intent
        Intent shareIntent =
                new Intent(android.content.Intent.ACTION_SEND);

        //set the type
        shareIntent.setType("text/plain");

        //add a subject
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                getString(R.string.share_subject));

        //build the body of the message to be shared
        String shareMessage = getString(R.string.app_name) + getString(R.string.share_message);

        //add the message
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareMessage);

        //start the chooser for sharing
        startActivity(Intent.createChooser(shareIntent,
                getString(R.string.share_hint)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu, menu);
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
        switch (item.getItemId()) {
            case R.id.menu_options_share:

                shareApp();
                return true;

            case R.id.menu_options_settings:

                Intent settingIntent = new Intent();
                settingIntent.setClass(MenuActivity.this, SettingActivity.class);
                startActivity(settingIntent);
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