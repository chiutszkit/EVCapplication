package com.example.tommyhui.evcapplication.nearby;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NearbyActivity extends ActionBarActivity {

    public static ArrayList<ItemCS> socketList = new ArrayList<>();
    private ItemCS_DBController db;

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Criteria criteria;
    private List<Marker> markers;
    private List<LatLng> markersLatLng;
    private int count = 1;
    private Location myLocation;
    private Marker myLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_activity);

        // Use customized action bar.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        // Set up the action bar's title.
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Nearby");

        // Set up the action bar's icon.
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.map_icon);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

//        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD)
//        {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//        }
        // Set up the map.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nearby_map);
        googleMap = supportMapFragment.getMap();

        locateChargingStationPosition();
        locateUserPosition();
        calculateRealTimeData();
    }

    /***************************************/
    public void calculateRealTimeData() {
//        float[] results = new float[1];
        if(myLocation != null) {
            for (int i = 0; i < markers.size(); i++) {
                StringBuilder stringBuilder = new StringBuilder();
                double lat1 = myLocation.getLatitude();
                double lng1 = myLocation.getLongitude();
                double lat2 = markersLatLng.get(i).latitude;
                double lng2 = markersLatLng.get(i).longitude;
                try {
                    String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lng1 + "&destination=" + lat2 + "," + lng2 + "&mode=driving";

                    HttpPost httppost = new HttpPost(url);

                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response;
                    stringBuilder = new StringBuilder();

                    response = client.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    InputStream stream = entity.getContent();
                    int b;
                    while ((b = stream.read()) != -1) {
                        stringBuilder.append((char) b);
                    }
                } catch (ClientProtocolException e) {
                } catch (IOException e) {
                }
                String distance = calculateDistance(stringBuilder);
                String time = calculateTime(stringBuilder);
                if(Double.parseDouble(socketList.get(i).getLatitude()) == markersLatLng.get(i).latitude &&
                        Double.parseDouble(socketList.get(i).getLongitude()) == markersLatLng.get(i).longitude) {
                    socketList.get(i).setDistance(distance);
                    socketList.get(i).setTime(time);
                    markers.get(i).setSnippet(distance + " km " + time + " mins");
                }
            }
        }
        MenuActivity.realTimeInfoList = socketList;
    }
    // Calculate the distances from charging stations to user position.
    public String calculateDistance(StringBuilder stringBuilder) {

//        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
//        String distance = NumberFormat.getInstance().format(results[0] / 1000);
        String fDistance = "";
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray array = jsonObject.getJSONArray("routes");
            for(int i = 0; i < array.length(); i++) {
                JSONObject routes = array.getJSONObject(i);
                JSONArray legs = routes.getJSONArray("legs");
                for (int j = 0; j < legs.length(); j++) {
                    JSONObject steps = legs.getJSONObject(j);
                    JSONObject distance = steps.getJSONObject("distance");

                    Log.i("Distance" + count, distance.toString());
                    count ++;
                    fDistance = distance.getString("text").split(" ")[0];
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fDistance;
    }

    // Calculate the driving time from charging stations to user position.
    public String calculateTime(StringBuilder stringBuilder) {

        String fTime = "";
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray array = jsonObject.getJSONArray("routes");
            for(int i = 0; i < array.length(); i++) {
                JSONObject routes = array.getJSONObject(i);
                JSONArray legs = routes.getJSONArray("legs");
                for (int j = 0; j < legs.length(); j++) {
                    JSONObject steps = legs.getJSONObject(j);
                    JSONObject time = steps.getJSONObject("duration");

                    Log.i("Time" + count, time.toString());
                    count ++;
                    fTime = time.getString("text").split(" ")[0];
                }
            }
//        try {
//            jsonObject = new JSONObject(stringBuilder.toString());
//            JSONArray array = jsonObject.getJSONArray("routes");
//            JSONObject routes = array.getJSONObject(0);
//            JSONArray legs = routes.getJSONArray("legs");
//            JSONObject steps = legs.getJSONObject(0);
//            JSONObject time = steps.getJSONObject("duration");
//
//            Log.i("Time", time.toString());
//            travelTime = time.getString("text");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fTime;
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

            // Move camera to the position that shows user existing location.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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
    public void locateChargingStationPosition() {

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        markers = new ArrayList<>();
        markersLatLng = new ArrayList<>();

        // Get the list of ALL nearby charging stations.
        db = new ItemCS_DBController(getApplicationContext());
        socketList = db.inputQueryCSes(this, new String[]{}, 1);

        // Add all markers to the map according to the socketList
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

//        if(type != null) {
//
//            String latInDouble = getIntent().getExtras().getString("latitude");
//            String lonInDouble = getIntent().getExtras().getString("longitude");
//
//            LatLng latLng = new LatLng(Double.parseDouble(latInDouble), Double.parseDouble(lonInDouble));
//
//            // Focus on the click item charging station
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//            Marker clickedMarker = markers.get(markersLatLng.indexOf(latLng));
//            clickedMarker.showInfoWindow();
//
//            locateUserPosition();
//            calculateRealTimeData();
//
//            onNavigate(myLocation, latLng);
//        }
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
    /***************************************/
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
                calculateRealTimeData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}