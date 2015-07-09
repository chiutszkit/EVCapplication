package com.example.tommyhui.evcapplication.nearby;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.HomeActivity;
import com.example.tommyhui.evcapplication.JSONParser.DirectionsJSONDrawPath;
import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.menu.MenuActivity;
import com.example.tommyhui.evcapplication.overview.OverviewActivity;
import com.example.tommyhui.evcapplication.util.ConnectionDetector;
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

public class NearbyActivity extends ActionBarActivity implements LocationListener {

    public static ArrayList<ItemCS> socketVenueList = new ArrayList<ItemCS>();
    private ItemCS_DBController db;
    private ProgressDialog progressDialog;
    private int count = 1;
    private boolean firstTime = true;

    private GoogleMap googleMap;
    private List<Marker> markers;
    private List<LatLng> markersLatLng;
    private Location myLocation;
    private Marker myLocationMarker;

    private ConnectionDetector cd = new ConnectionDetector(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.nearby_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.map_icon);

        /** Set up all the map fragment **/
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nearby_map);
        googleMap = supportMapFragment.getMap();

        /** Load the real time data **/
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        locateAllChargingStationPosition();

        if (cd.isConnectingToInternet() && cd.isConnectingToGPS()) {

            locateUserPosition();

            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
            progressDialog.setIcon(R.drawable.location_icon);
            progressDialog.setTitle(getResources().getString(R.string.nearby_progressDialog_title));
            progressDialog.setMessage(getResources().getString(R.string.nearby_progressDialog_content));
            progressDialog.show();

            new LoadGoogleMapTask().execute();

            /** Draw the travelling path **/
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker marker) {
                    for (int i = 0; i < markersLatLng.size(); i++) {
                        if (marker.getPosition().latitude == markersLatLng.get(i).latitude &&
                                marker.getPosition().longitude == markersLatLng.get(i).longitude) {
                            String distance = socketVenueList.get(i).getDistance();
                            String time = socketVenueList.get(i).getTime();
                            if (!distance.isEmpty() && !time.isEmpty())
                                marker.setSnippet(distance + getString(R.string.snippet_distance) + " " + time + getString(R.string.snippet_time));
                        }
                    }
                    DirectionsJSONDrawPath directionsJSONDrawPath = new DirectionsJSONDrawPath(googleMap, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                    if (cd.isConnectingToInternet())
                        directionsJSONDrawPath.drawDirectionPath();
                    marker.showInfoWindow();
                    return true;
                }
            });

            /** Display the list of charging station of clicked marker **/
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent();
                    intent.setClass(NearbyActivity.this, OverviewActivity.class);

                    if (!marker.getTitle().equals("My Current Location")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("query", marker.getTitle());

                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    /** Fetch data from Google Map **/
    private class LoadGoogleMapTask extends AsyncTask<Void,Integer,Integer> {
        // Do the long-running work in here
        protected Integer doInBackground(Void... params) {

            if (myLocation != null) {

                for (int i = 0; i < markersLatLng.size(); i++) {

                    final double lat1 = myLocation.getLatitude();
                    final double lng1 = myLocation.getLongitude();
                    final double lat2 = markersLatLng.get(i).latitude;
                    final double lng2 = markersLatLng.get(i).longitude;

                    String realTimeData[] = connectToGoogleMap(lat1, lat2, lng1, lng2);
                    String distance = realTimeData[0];
                    String time = realTimeData[1];

                    // Update the list of socketVenueList.
                    if (Double.parseDouble(socketVenueList.get(i).getLatitude()) == markersLatLng.get(i).latitude &&
                            Double.parseDouble(socketVenueList.get(i).getLongitude()) == markersLatLng.get(i).longitude) {
                        socketVenueList.get(i).setDistance(distance);
                        socketVenueList.get(i).setTime(time);
                    }

                    // Take a pause in order to prevent blocking from Google Map.
                    if(i%5 == 0) {
                        try {
                            Thread.sleep(1800);
                            Log.i("Pause","Pause");
                        } catch (InterruptedException ex) {
                        }
                    }

                    float file = i;
                    float noOfFile = socketVenueList.size();
                    publishProgress((int) ((file / noOfFile) * 100));
                    Log.i("Progress", (int) ((file / noOfFile) * 100) + "");
                }
            }
            // Pass the real time data of distance and travelling time to list of realTimeInfoList for further processing.
            MenuActivity.realTimeInfoList = socketVenueList;
            return null;
        }

        // Called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        // Called when doInBackground() is finished
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
        }
    }

    /** Calculate the distance and travelling time between user and charging stations **/
    public String[] connectToGoogleMap(double lat1, double lat2, double lng1, double lng2) {

        StringBuilder stringBuilder = new StringBuilder();

        // Set up the connection to Google Map.
        try {
            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lng1 +
                    "&destination=" + lat2 + "," + lng2 + "&mode=driving";

            HttpPost httppost = new HttpPost(url);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
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

        return new String[] {distance, time};
    }

    /** Get the distances from charging stations to user position **/
    public String calculateDistance(StringBuilder stringBuilder) {

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
            e.printStackTrace();
        }
        return fDistance;
    }

    /** Get the travelling time from charging stations to user position **/
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

                    fTime = time.getString("text").split(" ")[0];
                }
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
        return fTime;
    }

    /** Locate user current position **/
    public void locateUserPosition() {

        if(cd.isConnectingToGPS()) {
            googleMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            myLocation = HomeActivity.myLocation;

            if (myLocation != null) {
                onLocationChanged(myLocation);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 10, this);
        }
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

        if(firstTime) {
            myLocationMarker.showInfoWindow();
            firstTime = false;
        }
        HomeActivity.myLocation = location;

        Log.v("Location", "Location Change to, lat=" + latInDouble + ", lon=" + lonInDouble);
    }
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /** Locate all the markers of charging stations in the map **/
    public void locateAllChargingStationPosition() {

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        markers = new ArrayList<Marker>();
        markersLatLng = new ArrayList<LatLng>();

        // Get the list of ALL nearby charging stations.
        db = new ItemCS_DBController(getApplicationContext());
        socketVenueList = db.inputQueryCSes(this, new String[]{}, 1);

        // Add all markers to the map according to the socketVenueList
        for (int i = 0; i < socketVenueList.size(); i++) {
            if (socketVenueList.get(i).getLatitude() != null || socketVenueList.get(i).getLongitude() != null) {

                double latInDouble = Double.valueOf(socketVenueList.get(i).getLatitude().trim()).doubleValue();
                double lonInDouble = Double.valueOf(socketVenueList.get(i).getLongitude().trim()).doubleValue();

                LatLng latLng = new LatLng(latInDouble, lonInDouble);
                builder.include(latLng);

                // Add a marker to the map.
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);
                markerOptions.title(socketVenueList.get(i).getDescription());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                Marker marker = googleMap.addMarker(markerOptions);

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
    }
}