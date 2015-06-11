package com.example.tommyhui.evcapplication.socket;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

public class SocketListItemActivity extends ActionBarActivity implements LocationListener {

    private String type;
    private ArrayList<ItemCS> socketList;
    private ItemCS_DBController db;

    private GoogleMap googleMap;
    private List<Marker> markers;
    private List<LatLng> markersLatLng;
    private Location myLocation;
    private Marker myLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_list_item_activity);

        /** Get the data passed by SocketListActivity**/
        Bundle bundle = getIntent().getExtras();

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        if (bundle != null) {
            type = bundle.getString("type");
            title.setText(type + getString(R.string.socket_listitem_title));
        }

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.map_icon);

        /** Set up all the map fragment **/
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.socket_list_item_map);
        googleMap = supportMapFragment.getMap();

        /** Loading the real time data **/
        locateUserPosition();
        locateSameTypeChargingStationPosition();
    }

    /** Locate user current position **/
    public void locateUserPosition() {

        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String bestProvider = locationManager.getBestProvider(criteria, true);
        myLocation  = locationManager.getLastKnownLocation(bestProvider);
        if (myLocation != null) {
            onLocationChanged(myLocation);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
    }

    /** Handle the case when user position changes **/
    public void onLocationChanged(Location location) {

        double latInDouble = location.getLatitude();
        double lonInDouble = location.getLongitude();

        LatLng latLng = new LatLng(latInDouble, lonInDouble);

        // Delete the old marker of user location.
        if (myLocationMarker != null)
            myLocationMarker.remove();

        myLocationMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getResources().getString(R.string.nearby_userLocation))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usercar_icon)));

        myLocation = location;
        Log.v("Debug", "IN ON LOCATION CHANGE, lat=" + latInDouble + ", lon=" + lonInDouble);
    }
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    /** Locate all the markers of charging stations with specific type in the map **/
    public void locateSameTypeChargingStationPosition() {

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Bundle bundle = getIntent().getExtras();

        markers = new ArrayList<Marker>();
        markersLatLng = new ArrayList<LatLng>();

        // Get the list of nearby charging stations with the specific type.
        db = new ItemCS_DBController(getApplicationContext());
        socketList = db.inputQueryCSes(this, new String[]{type}, 1);

        // Add all markers to the map according to the socketVenueList
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
                        marker.setSnippet(socket.getDistance() + getString(R.string.snippet_distance) + " " + socket.getTime() + getString(R.string.snippet_time));
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
}