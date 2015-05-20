package com.example.tommyhui.evcapplication.overview;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS_DBController;

import java.util.List;


public class ItemCSActivity extends FragmentActivity {

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
//    private float[] markerColor = {BitmapDescriptorFactory.HUE_AZURE,
//            BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN,
//            BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA,
//            BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_RED,
//            BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_VIOLET,
//            BitmapDescriptorFactory.HUE_YELLOW};

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

        TextView addressText = (TextView) findViewById(R.id.chargingstation_item_text_chargingStationAddress);
        addressText.setText(address);
        TextView descriptionText = (TextView) findViewById(R.id.chargingstation_item_text_description);
        descriptionText.setText(description);
        TextView typeText = (TextView) findViewById(R.id.chargingstation_item_text_type);
        typeText.setText("Type: " + type);
        TextView socketText = (TextView) findViewById(R.id.chargingstation_item_text_socket);
        socketText.setText("Socket: " + socket);
        TextView quantityText = (TextView) findViewById(R.id.chargingstation_item_text_quantity);
        quantityText.setText("Quantity: " + quantity.toString());

        Button favoriteButton = (Button) findViewById(R.id.chargingstation_item_button_favorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                db = new FavoriteItemCS_DBController(getApplicationContext());
                favouriteItem = new FavoriteItemCS(address, district, description, type, socket, quantity, latitude, longitude);
                if (db.addFavoriteCS(favouriteItem) == null)
                    Toast.makeText(getApplicationContext(), R.string.item_toast_addToFavorites_before, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), R.string.item_toast_addToFavorites, Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.chargingstation_item_map);
        googleMap = supportMapFragment.getMap();
        locateChargingStationPosition();
        locateUserPosition();
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

        String provider = locationManager.getBestProvider(criteria, true);

        myLocation = getLastKnownLocation();

        double latInDouble = myLocation.getLatitude();
        double lonInDouble = myLocation.getLongitude();

        LatLng latLng = new LatLng(latInDouble, lonInDouble);

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }


    public void locateChargingStationPosition() {
        if(latitude != null || longitude != null){
            double latInDouble = Double.valueOf(latitude.trim()).doubleValue();
            double lonInDouble = Double.valueOf(longitude.trim()).doubleValue();

            LatLng latLng = new LatLng(latInDouble, lonInDouble);

            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    private Location getLastKnownLocation() {
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
                case LocationProvider.AVAILABLE:
                    Toast.makeText(getBaseContext(), provider + "IS OK", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(getBaseContext(), provider + "IS NOT OK", Toast.LENGTH_SHORT).show();
                    break;
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
        inflater.inflate(R.menu.menu_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}