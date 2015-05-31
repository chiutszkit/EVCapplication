package com.example.tommyhui.evcapplication.socket;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class SocketListItemActivity extends ActionBarActivity {

    private String type;
    private ArrayList<ItemCS> socketList;
    private ItemCS_DBController db;

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Criteria criteria;
    private List<Marker> markers;
    private List<LatLng> markersLatLng;
    private Location myLocation;
    private Marker myLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_list_item_activity);

        // Get the bundle passed by SocketListActivity.
        Bundle bundle = getIntent().getExtras();

        // Use customized action bar.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        // Set up the action bar's title.
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Nearby");
        if (bundle != null) {
            type = bundle.getString("type");
            title.setText(type + " Charger");
        }

        // Set up the action bar's icon.
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.map_icon);

        // Set up the map.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.socket_list_item_map);
        googleMap = supportMapFragment.getMap();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nearby_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.nearby_action_locate:
//                locateUserPosition();
//                calculateDistance();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}