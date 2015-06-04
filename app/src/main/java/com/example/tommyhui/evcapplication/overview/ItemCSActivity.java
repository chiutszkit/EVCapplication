package com.example.tommyhui.evcapplication.overview;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

import java.util.List;


public class ItemCSActivity extends ActionBarActivity {

    private String address;
    private String district;
    private String description;
    private String type;
    private String socket;
    private Integer quantity;
    private String latitude;
    private String longitude;

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Location myLocation;
    private Criteria criteria;
    private Marker chargingStationMarker;

    private FavoriteItemCS_DBController db;
    private FavoriteItemCS favouriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chargingstation_item_activity);

//        /*Hide Action Bar*/
//        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();

        address = bundle.getString("address");
        district = bundle.getString("district");
        description = bundle.getString("description");
        type = bundle.getString("type");
        socket = bundle.getString("socket");
        quantity = bundle.getInt("quantity");
        latitude = bundle.getString("latitude");
        longitude = bundle.getString("longitude");

        // Use customized action bar.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        // Set up the action bar's title.
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(description);

        // Set up the action bar's icon.
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.chargingstation_icon);

        TextView addressText = (TextView) findViewById(R.id.chargingstation_item_text_chargingStationAddress);
        addressText.setText(address);
        TextView typeText = (TextView) findViewById(R.id.chargingstation_item_text_type);
        typeText.setText(type);
        TextView socketText = (TextView) findViewById(R.id.chargingstation_item_text_socket);
        socketText.setText(socket);
        TextView quantityText = (TextView) findViewById(R.id.chargingstation_item_text_quantity);
        quantityText.setText("Total: " + quantity.toString());


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.chargingstation_item_map);
        googleMap = supportMapFragment.getMap();
        locateUserPosition();
        locateChargingStationPosition();
    }
    @Override
    protected void onResume() {
        super.onResume();
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 10000, 10, myLocationListener);
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

    public void locateChargingStationPosition() {

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if(latitude != null || longitude != null){
            double latInDouble = Double.valueOf(latitude.trim()).doubleValue();
            double lonInDouble = Double.valueOf(longitude.trim()).doubleValue();

            LatLng latLng = new LatLng(latInDouble, lonInDouble);
            builder.include(latLng);

            chargingStationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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
        for (ItemCS socket : MenuActivity.realTimeInfoList) {
            if (socket.getLatitude().equals(latitude) && socket.getLongitude().equals(longitude))
                chargingStationMarker.setSnippet(socket.getDistance() + " km " + socket.getTime() + " mins");
        }
        chargingStationMarker.showInfoWindow();
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

    private LocationListener myLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            myLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch(status) {
//                case LocationProvider.AVAILABLE:
//                    Toast.makeText(getBaseContext(), provider + "IS OK", Toast.LENGTH_SHORT).show();
//                    break;
//                case LocationProvider.OUT_OF_SERVICE:
//                    Toast.makeText(getBaseContext(), provider + "IS NOT OK", Toast.LENGTH_SHORT).show();
//                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "The GPS is ON now.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(), "The GPS is OFF now.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.itemcs_options_menu, menu);
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
                if (db.addFavoriteCS(favouriteItem) == null)
                    Toast.makeText(getApplicationContext(), R.string.item_toast_addToFavorites_before, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), R.string.item_toast_addToFavorites, Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}