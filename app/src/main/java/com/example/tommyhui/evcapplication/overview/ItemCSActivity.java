package com.example.tommyhui.evcapplication.overview;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS_DBController;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.map.DirectionsJSONDrawPath;
import com.example.tommyhui.evcapplication.map.DirectionsJSONParser;
import com.example.tommyhui.evcapplication.menu.MenuActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ItemCSActivity extends ActionBarActivity implements LocationListener{

    private String address;
    private String district;
    private String description;
    private String type;
    private String socket;
    private Integer quantity;
    private String latitude;
    private String longitude;

    private GoogleMap googleMap;
    private Location myLocation;
    private Marker chargingStationMarker;
    private Marker myLocationMarker;

    private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    private FavoriteItemCS_DBController db;
    private FavoriteItemCS favouriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chargingstation_item_activity);

        /** Get the data passed **/
        Bundle bundle = getIntent().getExtras();

        address = bundle.getString("address");
        district = bundle.getString("district");
        description = bundle.getString("description");
        type = bundle.getString("type");
        socket = bundle.getString("socket");
        quantity = bundle.getInt("quantity");
        latitude = bundle.getString("latitude");
        longitude = bundle.getString("longitude");

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(description);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.chargingstation_icon);

        TextView addressText = (TextView) findViewById(R.id.chargingstation_item_text_chargingStationAddress);
        addressText.setText(address);
        TextView typeText = (TextView) findViewById(R.id.chargingstation_item_text_type);
        typeText.setText(type);
        TextView socketText = (TextView) findViewById(R.id.chargingstation_item_text_socket);
        socketText.setText(socket);
        TextView quantityText = (TextView) findViewById(R.id.chargingstation_item_text_quantity);
        quantityText.setText(getString(R.string.item_title_availability_text) + quantity.toString());

        /** Set up the map fragment **/
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.chargingstation_item_map);
        googleMap = supportMapFragment.getMap();

        locateUserPosition();

        /** Draw the travelling path **/
        DirectionsJSONDrawPath directionsJSONDrawPath = new DirectionsJSONDrawPath(googleMap, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        directionsJSONDrawPath.drawDirectionPath();

        locateChargingStationPosition();
    }
    /** Locate the marker of charging stations selected in the map **/
    public void locateChargingStationPosition() {

        if(latitude != null || longitude != null){
            double latInDouble = Double.valueOf(latitude.trim()).doubleValue();
            double lonInDouble = Double.valueOf(longitude.trim()).doubleValue();

            LatLng latLng = new LatLng(latInDouble, lonInDouble);
            builder.include(latLng);

            chargingStationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

        if(myLocation != null) {
            builder.include(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
            final LatLngBounds bounds = builder.build();
            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    // Move camera to the position that shows all markers.
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
                    // Remove listener to prevent position reset on camera move.
                    googleMap.setOnCameraChangeListener(null);
                }
            });
        }
        for (ItemCS socket : MenuActivity.realTimeInfoList) {
            if (socket.getLatitude().equals(latitude) && socket.getLongitude().equals(longitude))
                chargingStationMarker.setSnippet(socket.getDistance() + getString(R.string.snippet_distance) + " " + socket.getTime() + getString(R.string.snippet_time));
        }
        chargingStationMarker.showInfoWindow();
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


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.itemcs_options_menu, menu);

        db = new FavoriteItemCS_DBController(getApplicationContext());
        favouriteItem = new FavoriteItemCS(address, district, description, type, socket, quantity, latitude, longitude);
        if (db.checkFavoriteCSExist(favouriteItem)) {
            menu.findItem(R.id.itemCS_action_favorite).setIcon(R.drawable.del_favorite_icon);
            menu.findItem(R.id.itemCS_action_favorite).setTitle(R.string.item_button_deleteFromFavorites);
        }
        else {
            menu.findItem(R.id.itemCS_action_favorite).setIcon(R.drawable.add_favorite_icon);
            menu.findItem(R.id.itemCS_action_favorite).setTitle(R.string.item_button_addToFavorites);
        }
            return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.itemCS_action_favorite:
                db = new FavoriteItemCS_DBController(getApplicationContext());
                favouriteItem = new FavoriteItemCS(address, district, description, type, socket, quantity, latitude, longitude);
                if (db.checkFavoriteCSExist(favouriteItem)) {
                    db.deleteFavoriteCS(db.getFavoriteCS(db.getFavoriteCSId(favouriteItem)));
                    Toast.makeText(getApplicationContext(), R.string.item_toast_deleteFromFavorites, Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.add_favorite_icon);
                    item.setTitle(R.string.item_button_addToFavorites);
                }
                else {
                    db.addFavoriteCS(favouriteItem);
                    Toast.makeText(getApplicationContext(), R.string.item_toast_addToFavorites, Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.del_favorite_icon);
                    item.setTitle(R.string.item_button_deleteFromFavorites);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}