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
import com.example.tommyhui.evcapplication.menu.MenuActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
        locateUserPosition();
        locateSameTypeChargingStationPosition();
    }
    public void locateUserPosition() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        myLocation = getLastKnownLocation();

        if(myLocation != null) {
            double latInDouble = myLocation.getLatitude();
            double lonInDouble = myLocation.getLongitude();

            LatLng latLng = new LatLng(latInDouble, lonInDouble);

            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getResources().getString(R.string.nearby_userLocation))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.usercar_icon)));
        }
    }

    public Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void locateSameTypeChargingStationPosition() {

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Bundle bundle = getIntent().getExtras();

        markers = new ArrayList<>();
        markersLatLng = new ArrayList<>();

        // Get the list of nearby charging stations with the specific type.
        db = new ItemCS_DBController(getApplicationContext());
        socketList = db.inputQueryCSes(this, new String[]{type}, 1);

        // Add all markers to the map according to the socketList
        for (int i = 0; i < socketList.size(); i++) {
            if (socketList.get(i).getType().contains(type)) {

                double latInDouble = Double.valueOf(socketList.get(i).getLatitude().trim()).doubleValue();
                double lonInDouble = Double.valueOf(socketList.get(i).getLongitude().trim()).doubleValue();

                LatLng latLng = new LatLng(latInDouble, lonInDouble);
                builder.include(latLng);

                // Add a marker to the map.
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(socketList.get(i).getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                for (ItemCS socket : MenuActivity.realTimeInfoList) {
                    if (socket.getLatitude().equals(socketList.get(i).getLatitude()) && socket.getLongitude().equals(socketList.get(i).getLongitude()))
                        marker.setSnippet(socket.getDistance() + " km " + socket.getTime() + " mins");
                }

                markers.add(marker);
                markersLatLng.add(latLng);

                if(latInDouble == Double.valueOf(bundle.getString("latitude")) && lonInDouble == Double.valueOf(bundle.getString("longitude")))
                    marker.showInfoWindow();
            }
        }
        if(myLocation != null) {
            builder.include(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
            final LatLngBounds bounds = builder.build();
            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    // Move camera to the position that shows all markers.
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    // Remove listener to prevent position reset on camera move.
                    googleMap.setOnCameraChangeListener(null);
                }
            });
        }
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