package com.example.tommyhui.evcapplication.nearby;

import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
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
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NearbyActivity extends ActionBarActivity {

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
        setContentView(R.layout.nearby_activity);

        // Get the bundle passed by SocketListActivity.
        Bundle bundle = getIntent().getExtras();

        // Use customized action bar.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        // Set up the action bar's title.
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        if (bundle != null) {
            type = bundle.getString("type");
            title.setText(type + " Charger");
        } else
            title.setText("Nearby");

        // Set up the action bar's icon.
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.map_icon);

        // Set up the map.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nearby_map);
        googleMap = supportMapFragment.getMap();

        locateChargingStationPosition(type);
    }

    // Calculate the distances from charging stations to user position.
    public void calculateDistance() {
        float[] results = new float[1];
        if(myLocation != null) {
            for (int i = 0; i < markers.size(); i++) {
                Location.distanceBetween(myLocation.getLatitude(),
                        myLocation.getLongitude(), markersLatLng.get(i).latitude,
                        markersLatLng.get(i).longitude, results);
                String distance = NumberFormat.getInstance().format(results[0] / 1000);
                markers.get(i).setSnippet(distance + " km");
            }
        }
    }

    // Locate user current position.
    public void locateUserPosition() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        myLocation = getLastKnownLocation();
        if(myLocation != null) {
            double latInDouble = myLocation.getLatitude();
            double lonInDouble = myLocation.getLongitude();

            LatLng latLng = new LatLng(latInDouble, lonInDouble);

            // Delete the old marker of user location.
            if (myLocationMarker != null)
                myLocationMarker.remove();

            // Add a marker of user existing location.
            myLocationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getResources().getString(R.string.nearby_userLocation))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.usercar_icon)));

            myLocationMarker.showInfoWindow();

            googleMap.addCircle(
                    new CircleOptions()
                        .center(latLng)
                        .radius(1000)
                        .strokeWidth(10)
                        .strokeColor(Color.BLACK)
                        .fillColor(Color.TRANSPARENT));

            // Move camera to the position that shows user existing location.
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        else
            Toast.makeText(getBaseContext(), getResources().getString(R.string.nearby_alert_gpsOff), Toast.LENGTH_SHORT).show();
    }

    public Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
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

    // Locate all the markers of charging stations in the map.
    public void locateChargingStationPosition(String type) {

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        markers = new ArrayList<>();
        markersLatLng = new ArrayList<>();

        if (type == null) {
            // Get the list of ALL nearby charging stations.
            db = new ItemCS_DBController(getApplicationContext());
            socketList = db.inputQueryCSes(this, new String[]{null}, 1);
        } else
            // Get the list of the charging stations according to the type selected.
            socketList = this.getIntent().getParcelableArrayListExtra("socketList");

        // Add all markers to the map accroding to the socketList
        for (int i = 0; i < socketList.size(); i++) {
            if (socketList.get(i).getLatitude() != null || socketList.get(i).getLongitude() != null) {

                double latInDouble = Double.valueOf(socketList.get(i).getLatitude().trim()).doubleValue();
                double lonInDouble = Double.valueOf(socketList.get(i).getLongitude().trim()).doubleValue();

                LatLng latLng = new LatLng(latInDouble, lonInDouble);
                builder.include(latLng);

                // Add a marker to the map.
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(socketList.get(i).getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                markers.add(i, marker);
                markersLatLng.add(i, latLng);
            }
        }

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

        if(type != null) {

            String latInDouble = getIntent().getExtras().getString("latitude");
            String lonInDouble = getIntent().getExtras().getString("longitude");

            LatLng latLng = new LatLng(Double.parseDouble(latInDouble), Double.parseDouble(lonInDouble));

            // Focus on the click item charging station
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            Marker clickedMarker = markers.get(markersLatLng.indexOf(latLng));
            clickedMarker.showInfoWindow();

            locateUserPosition();
            calculateDistance();

//            onNavigate(myLocation, latLng);
        }
    }

    public void onNavigate(Location location, LatLng latlng) {

        double fromLat = location.getLatitude();
        double fromLng = location.getLongitude();
        double toLat = latlng.latitude;
        double toLng = latlng.longitude;

        String uriStr = String.format(Locale.ENGLISH,
                "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", fromLat,
                fromLng, toLat, toLng);

        Intent intent = new Intent();

        intent.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");

        intent.setAction(Intent.ACTION_VIEW);

        intent.setData(Uri.parse(uriStr));

        startActivity(intent);
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
                locateUserPosition();
                calculateDistance();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}