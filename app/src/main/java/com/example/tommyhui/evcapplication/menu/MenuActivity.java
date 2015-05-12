package com.example.tommyhui.evcapplication.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.about.AboutActivity;
import com.example.tommyhui.evcapplication.favourite.FavoriteActivity;
import com.example.tommyhui.evcapplication.nearby.NearbyActivity;
import com.example.tommyhui.evcapplication.overview.OverviewActivity;
import com.example.tommyhui.evcapplication.search.SearchActivity;

import java.lang.reflect.Method;


public class MenuActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        //        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

        /*Set Action Bar's Title*/
        TextView title = (TextView)findViewById(R.id.action_bar_title);
        title.setText("Menu");

        /*Set Action Bar's Icon*/
        ImageView myImgView = (ImageView)findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.evc_icon);

        /*Set Menu Page Button*/
        findViewById(R.id.menu_grid_overview).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_search).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_favourite).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_nearest).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_about).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.menu_grid_share).setOnClickListener(mGlobal_OnClickListener);
    }
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
                case R.id.menu_grid_nearest:
                    Intent nearestIntent = new Intent();
                    nearestIntent.setClass(MenuActivity.this, NearbyActivity.class);
                    startActivity(nearestIntent);
                    break;
                case R.id.menu_grid_about:
                    Intent aboutIntent = new Intent();
                    aboutIntent.setClass(MenuActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                    break;
                case R.id.menu_grid_share:
                    shareApp();
                    break;
                default:
            }
        }
    };
    public void shareApp() {
        //create the send intent
        Intent shareIntent =
                new Intent(android.content.Intent.ACTION_SEND);

        //set the type
        shareIntent.setType("text/plain");

        //add a subject
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Check out this app!");

        //build the body of the message to be shared
        String shareMessage = getString(R.string.app_name) + " - A easy way to find the nearest charging station in HK.";

        //add the message
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareMessage);

        //start the chooser for sharing
        startActivity(Intent.createChooser(shareIntent,
                "Share via"));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_options_menu, menu);
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
}