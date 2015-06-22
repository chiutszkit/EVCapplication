package com.example.tommyhui.evcapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.tommyhui.evcapplication.JSONParser.RealTimeStatusJSONParser;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS_DBController;
import com.example.tommyhui.evcapplication.database.HistoryItemCS;
import com.example.tommyhui.evcapplication.database.HistoryItemCS_DBController;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.menu.MenuActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import java.util.Set;

public class HomeActivity extends Activity implements LocationListener {

    public static ArrayList<ItemCS> socketVenueList = new ArrayList<ItemCS>();
    public static ArrayList <ItemCS> matchingList = new ArrayList<ItemCS>();
    public static ArrayList <String> realTimeStatusList = new ArrayList<String>();
    public static Polyline polyline;
    public static Location myLocation;
    public static String localhost = "192.168.0.104";

    // JSON Node names
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_STATUS = "realtimestatus";
    public static final String TAG_INDEX = "index_cs";
    public static final String TAG_AVAILABILITY = "availability";
    public static final String TAG_UPDATED = "updated_at";

    // url to update product
    private static String url_get_all_status = "http://" + localhost + "/com.example.tommyhui.evcapplication/get_all_status.php";

    // products JSONArray
    private JSONArray status = null;

    private ArrayList<String> list_ID_favorite;
    private ArrayList<String> list_ID_history;

    private ProgressBar mProgress;
    private RealTimeStatusJSONParser jsonParser = new RealTimeStatusJSONParser();
    private ItemCS_DBController db_ItemCS;
    private FavoriteItemCS_DBController db_FavoriteItemCS;
    private HistoryItemCS_DBController db_HistoryItemCS;
    private String[] address;
    private String[] district;
    private String[] description;
    private String[] type;
    private String[] socket;
    private int[] quantity;
    private String[] latitude;
    private String[] longitude;

    private List<LatLng> markersLatLng;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Set up the language of application */
        SharedPreferences sharedPref = getSharedPreferences("UserConfigs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(sharedPref.getString("language","no").equals("no")) {
            if (!Locale.getDefault().getLanguage().equals("zh"))
                setLocale("en");
            else
                setLocale("zh");
        }
        else
            setLocale(sharedPref.getString("language","no"));
        editor.apply();

        setContentView(R.layout.home_activity);

        /** Preload the data of Real Time Status and set up the database afterward**/
        new LoadAllStatus().execute();

        /** Preload the data of Google Map **/
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        locateUserPosition();
        locateAllChargingStationPosition();

        mProgress = (ProgressBar) findViewById(R.id.homepage_progressBar);
        new LoadRealTimeDataTask().execute((Void[])null);

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllStatus extends AsyncTask<String, String, String> {

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_get_all_status, "GET", params);

            // Check your log cat for JSON response
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    status = json.getJSONArray(TAG_STATUS);

                    // looping through All Products
                    for (int i = 0; i < status.length(); i++) {
                        JSONObject c = status.getJSONObject(i);

                        // Storing each json item in variable
                        String availability = c.getString(TAG_AVAILABILITY);

                        // adding HashList to ArrayList
                        realTimeStatusList.add(i, availability);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        // Called when doInBackground() is finished
        protected void onPostExecute(String file_url) {
            setUpDataBase();
        }
    }

    /** Fetches data from Google Map */
    private class LoadRealTimeDataTask extends AsyncTask<Void,Integer,Integer> {
        // Do the long-running work in here
        protected Integer doInBackground(Void... params) {

            if (myLocation != null) {

                for (int i = 0; i < markersLatLng.size()-46; i++) {

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
            mProgress.setProgress(progress[0]);
        }

        // Called when doInBackground() is finished
        protected void onPostExecute(Integer result) {
            Intent menuIntent = new Intent();
            menuIntent.setClass(HomeActivity.this, MenuActivity.class);
            startActivity(menuIntent);
        }
    }

    /** Set up all the database **/
    public void setUpDataBase() {

        db_ItemCS = new ItemCS_DBController(this);
        db_FavoriteItemCS = new FavoriteItemCS_DBController(this);
        db_HistoryItemCS = new HistoryItemCS_DBController(this);

        db_ItemCS.removeAll();
        db_FavoriteItemCS.removeAll();
        db_HistoryItemCS.removeAll();

        // Get resources from array.xml
        address = getResources().getStringArray(R.array.address);
        district = getResources().getStringArray(R.array.district);
        description = getResources().getStringArray(R.array.description);
        type = getResources().getStringArray(R.array.type);
        socket = getResources().getStringArray(R.array.socket);
        quantity = getResources().getIntArray(R.array.quantity);
        latitude = getResources().getStringArray(R.array.latitude);
        longitude = getResources().getStringArray(R.array.longitude);

        matchingList.clear();

        // Set up database for ItemCS
        for (int i = 0; i < address.length; i++) {
            db_ItemCS.addCS(new ItemCS(address[i], district[i], description[i], type[i], socket[i], quantity[i], latitude[i], longitude[i], realTimeStatusList.get(i)));
            matchingList.add(new ItemCS(address[i], district[i], description[i], type[i], socket[i], quantity[i], latitude[i], longitude[i], realTimeStatusList.get(i)));
        }
        MenuActivity.ItemCSes = db_ItemCS.inputQueryCSes(this, new String[]{}, 1);

        SharedPreferences sharedPref = getSharedPreferences("UserConfigs", Context.MODE_PRIVATE);
        Set mySetFavorite = sharedPref.getStringSet("favoriteList", null);
        Set mySetHistory = sharedPref.getStringSet("historyList", null);
        Set mySetRealTimeStatus = sharedPref.getStringSet("realTimeStatusList", null);

        // Set up database for FavoriteItemCS
        if(mySetFavorite != null) {
            list_ID_favorite = new ArrayList<String>(mySetFavorite);
            if (list_ID_favorite.size() > 0) {
                for (String index : list_ID_favorite) {
                    int i = Integer.parseInt(index);
                    ItemCS cs = new ItemCS(address[i], district[i], description[i], type[i], socket[i], quantity[i], latitude[i], longitude[i], realTimeStatusList.get(i));
                    FavoriteItemCS favoriteItemCS = new FavoriteItemCS(cs.getAddress(), cs.getDistrict(), cs.getDescription(),
                            cs.getType(), cs.getSocket(), cs.getQuantity(), cs.getLatitude(), cs.getLongitude(), cs.getAvailability());
                    db_FavoriteItemCS.addFavoriteCS(favoriteItemCS);
                }
            }
        }

        // Set up database for HistoryItemCS
        if(mySetHistory != null) {
            list_ID_history = new ArrayList<String>(mySetHistory);
            if (list_ID_history.size() > 0) {
                for (String index : list_ID_history) {
                    int i = Integer.parseInt(index);
                    ItemCS cs = new ItemCS(address[i], district[i], description[i], type[i], socket[i], quantity[i], latitude[i], longitude[i], realTimeStatusList.get(i));
                    HistoryItemCS historyItemCS = new HistoryItemCS(cs.getAddress(), cs.getDistrict(), cs.getDescription(),
                            cs.getType(), cs.getSocket(), cs.getQuantity(), cs.getLatitude(), cs.getLongitude(), cs.getAvailability());
                    db_HistoryItemCS.addHistoryCS(historyItemCS);
                }
            }
        }

        // Set up database for Real Time Status
        if(mySetRealTimeStatus != null) {
            realTimeStatusList = new ArrayList<String>(mySetRealTimeStatus);
        }
    }

    /** Locate user current position **/
    public void locateUserPosition() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String bestProvider = locationManager.getBestProvider(criteria, true);
        myLocation  = locationManager.getLastKnownLocation(bestProvider);
        if (myLocation != null) {
            onLocationChanged(myLocation);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, this);
    }

    /** Handle the case when user position changes **/
    public void onLocationChanged(Location location) {

        double latInDouble = location.getLatitude();
        double lonInDouble = location.getLongitude();

        myLocation = location;

        Log.v("Home", "IN ON LOCATION CHANGE, lat=" + latInDouble + ", lon=" + lonInDouble);
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

    /** Locate all the markers of charging stations **/
    public void locateAllChargingStationPosition() {

        markersLatLng = new ArrayList<LatLng>();

        // Get the list of ALL nearby charging stations.
        db_ItemCS = new ItemCS_DBController(getApplicationContext());
        socketVenueList = db_ItemCS.inputQueryCSes(this, new String[]{}, 2);

        // Save all position of markers to the list of markersLatLng.
        for (int i = 0; i < socketVenueList.size(); i++) {
            if (socketVenueList.get(i).getLatitude() != null || socketVenueList.get(i).getLongitude() != null) {

                double latInDouble = Double.valueOf(socketVenueList.get(i).getLatitude().trim()).doubleValue();
                double lonInDouble = Double.valueOf(socketVenueList.get(i).getLongitude().trim()).doubleValue();

                LatLng latLng = new LatLng(latInDouble, lonInDouble);
                markersLatLng.add(i, latLng);
            }
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

                    Log.i("Distance " + count, distance.toString());
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fTime;
    }

//    private void showNoGPSDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//        alertDialog.setTitle(R.string.searchresult_alertDialog_no_gps_title);
//        alertDialog.setMessage(R.string.searchresult_alertDialog_no_gps_text);
//        alertDialog.setNeutralButton(R.string.searchresult_alertDialog_revise_option, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                SearchResultActivity.this.finish();
//            }
//        });
//
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//    }

    /** Set up the language of application **/
    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();

        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        SharedPreferences sharedPref = getSharedPreferences("UserConfigs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("language", lang);
        editor.apply();
    }
}