package com.example.tommyhui.evcapplication.realtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.ChargerListViewAdapter;
import com.example.tommyhui.evcapplication.charger.ChargerListActivity;

public class RealTimeSortActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtime_sort_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.realtime_sort_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.realtime_icon);

        /** Set up menu page's button **/
        final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
            public void onClick(final View v) {

                Bundle bundle = new Bundle();

                switch (v.getId()) {
                    case R.id.realtime_sort_grid_district:
                        bundle.putString("sort", "District");
                        break;
                    case R.id.realtime_sort_grid_type:
                        bundle.putString("sort", "Type");
                        break;
                    case R.id.realtime_sort_grid_socket:
                        bundle.putString("sort", "Socket");
                        break;
                    case R.id.realtime_sort_grid_availability:
                        bundle.putString("sort", "Availability");
                        break;
                    case R.id.realtime_sort_grid_all:
                        bundle.putString("sort", "All");
                        break;
                    default:
                }
                Intent sortIntent = new Intent();
                sortIntent.setClass(RealTimeSortActivity.this, RealTimeSortItemActivity.class);
                sortIntent.putExtras(bundle);
                startActivity(sortIntent);
            }
        };

        findViewById(R.id.realtime_sort_grid_district).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.realtime_sort_grid_type).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.realtime_sort_grid_socket).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.realtime_sort_grid_availability).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.realtime_sort_grid_all).setOnClickListener(mGlobal_OnClickListener);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}