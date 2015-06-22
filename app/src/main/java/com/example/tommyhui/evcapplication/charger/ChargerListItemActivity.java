package com.example.tommyhui.evcapplication.charger;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.HomeActivity;
import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.JSONParser.DirectionsJSONDrawPath;
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

public class ChargerListItemActivity extends ActionBarActivity implements LocationListener {

    private String type;
    private ArrayList<ItemCS> chargerList;
    private ItemCS_DBController db;

    private GoogleMap googleMap;
    private String latitude;
    private String longitude;
    private List<Marker> markers;
    private List<LatLng> markersLatLng;
    private Location myLocation;
    private Marker myLocationMarker;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charger_list_item_activity);

        /** Get the data passed by ChargerListActivity**/
        Bundle bundle = getIntent().getExtras();

        latitude = bundle.getString("latitude");
        longitude = bundle.getString("longitude");

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        if (bundle != null) {
            type = bundle.getString("type");
            title.setText(type + getString(R.string.charger_list_item_title));
        }

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.map_icon);

        /** Set up all the map fragment **/
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.charger_list_item_map);
        googleMap = supportMapFragment.getMap();

        /** Loading the real time data **/
        locateUserPosition();

        /** Draw the travelling path **/
        DirectionsJSONDrawPath directionsJSONDrawPath = new DirectionsJSONDrawPath(googleMap, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        directionsJSONDrawPath.drawDirectionPath();

        locateSameTypeChargingStationPosition();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
                /** Draw the travelling path **/
                DirectionsJSONDrawPath directionsJSONDrawPath = new DirectionsJSONDrawPath(googleMap, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                directionsJSONDrawPath.drawDirectionPath();
                marker.showInfoWindow();
                return true;
            }
        });
    }

    /** Locate user current position **/
    public void locateUserPosition() {

        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        myLocation = HomeActivity.myLocation;

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

        markers = new ArrayList<Marker>();
        markersLatLng = new ArrayList<LatLng>();

        // Get the list of nearby charging stations with the specific type.
        db = new ItemCS_DBController(this);
        chargerList = db.inputQueryCSes(this, new String[]{type}, 1);

        // Add all markers to the map according to the socketVenueList
        for (int i = 0; i < chargerList.size(); i++) {
            if (chargerList.get(i).getType().contains(type)) {

                double latInDouble = Double.valueOf(chargerList.get(i).getLatitude().trim()).doubleValue();
                double lonInDouble = Double.valueOf(chargerList.get(i).getLongitude().trim()).doubleValue();

                LatLng latLng = new LatLng(latInDouble, lonInDouble);
                builder.include(latLng);

                // Add a marker to the map.
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(chargerList.get(i).getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                for (ItemCS socket : MenuActivity.realTimeInfoList) {
                    if (socket.getLatitude().equals(chargerList.get(i).getLatitude()) && socket.getLongitude().equals(chargerList.get(i).getLongitude()))
                        marker.setSnippet(socket.getDistance() + getString(R.string.snippet_distance) + " " + socket.getTime() + getString(R.string.snippet_time));
                }

                markers.add(marker);
                markersLatLng.add(latLng);

                if(latInDouble == Double.valueOf(latitude) && lonInDouble == Double.valueOf(longitude))
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