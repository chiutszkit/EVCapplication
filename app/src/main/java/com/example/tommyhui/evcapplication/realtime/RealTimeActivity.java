package com.example.tommyhui.evcapplication.realtime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.HomeActivity;
import com.example.tommyhui.evcapplication.JSONParser.RealTimeStatusJSONParser;
import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.RealTimeListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.menu.MenuActivity;
import com.example.tommyhui.evcapplication.util.AESencryption;
import com.example.tommyhui.evcapplication.util.ConnectionDetector;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RealTimeActivity extends ActionBarActivity implements LocationListener {

    private RealTimeStatusJSONParser jsonParser = new RealTimeStatusJSONParser();
    private String index_cs;
    private String district;
    private String description;
    private String address;
    private String type;
    private String socket;
    private Location myLocation;
    private String decrypted;

    private ConnectionDetector cd = new ConnectionDetector(this);

    // URL to download QR code's scanner
    public static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtime_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.realtime_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.realtime_icon);

        /** Load the real time data **/
        locateUserPosition();

        /** Display the list of socket **/
        final ListView listview = (ListView) findViewById(R.id.realtime_list_view);

        final int[] realTimeTitle = new int[]{
                R.string.realtime_scan_title, R.string.realtime_sort_title, R.string.realtime_log_title
        };

        int[] realTimeSubtitle = new int[]{
                R.string.realtime_scan_subtitle, R.string.realtime_sort_subtitle, R.string.realtime_log_subtitle
        };

        int[] realTimeIcon = new int[]{
                R.drawable.qr_icon, R.drawable.inquiry_icon, R.drawable.log_icon
        };

        RealTimeListViewAdapter adapter = new RealTimeListViewAdapter(this, realTimeIcon, realTimeTitle, realTimeSubtitle);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (cd.isConnectingToInternet())
                    new LoadAllStatus().execute();
                switch (position) {
                    case 0:
                        scanQR(findViewById(android.R.id.content));
                        break;
                    case 1:
                        Intent realTimeSortintent = new Intent();
                        realTimeSortintent.setClass(RealTimeActivity.this, RealTimeSortActivity.class);
                        startActivity(realTimeSortintent);
                        break;
                    case 2:
                        Intent realTimeLogintent = new Intent();
                        realTimeLogintent.setClass(RealTimeActivity.this, RealTimeLogActivity.class);
                        startActivity(realTimeLogintent);
                        break;
                    default:
                }
            }
        });
    }

     /** Background Async Task to Save status details and log **/
     class UpdateDetails extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            /** Update record in realtimechargingstation table **/
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(HomeActivity.TAG_CHARGINGSTATION_INDEX, index_cs));


            // sending modified data through http request
            // Notice that update real time status url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(HomeActivity.url_update_charging_station,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(HomeActivity.TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about charging station update
                    setResult(100, i);
                    Log.v("debug", "charging station OK");
                } else {
                    // failed to update charging station
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /** Create record in realtimestatus table **/
            // Building Parameters
            params.clear();
            params.add(new BasicNameValuePair(HomeActivity.TAG_STATUS_INDEX, index_cs));
            params.add(new BasicNameValuePair(HomeActivity.TAG_STATUS_TYPE, type));

            // getting JSON Object
            // Note that create status url accepts POST method
            json = jsonParser.makeHttpRequest(HomeActivity.url_create_status,
                    "POST", params);

            // check for success tag
            try {
                int success = json.getInt(HomeActivity.TAG_SUCCESS);

                if (success == 1) {
                    // successfully created status
                    Log.v("debug", "status OK");
                } else {
                    // failed to create log
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /** Create record in realtimelog table **/
            // Building Parameters
            params.clear();
            params.add(new BasicNameValuePair(HomeActivity.TAG_LOG_DISTRICT, district));
            params.add(new BasicNameValuePair(HomeActivity.TAG_LOG_DESCRIPTION, description));
            params.add(new BasicNameValuePair(HomeActivity.TAG_LOG_TYPE, type));
            params.add(new BasicNameValuePair(HomeActivity.TAG_LOG_SOCKET, socket));

            // getting JSON Object
            // Note that create log url accepts POST method
            json = jsonParser.makeHttpRequest(HomeActivity.url_create_log,
                    "POST", params);

            // check for success tag
            try {
                int success = json.getInt(HomeActivity.TAG_SUCCESS);

                if (success == 1) {
                    // successfully created log
                    Log.v("debug", "log OK");
                } else {
                    // failed to create log
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
         protected void onPostExecute(String file_url) {
             runOnUiThread(new java.lang.Runnable() {
                 public void run() {
                     showSuccessDialog();
                 }
             });
         }
    }
    /** Background Async Task to Load all real time status by making HTTP Request **/
    class LoadAllStatus extends AsyncTask<String, String, String> {

        /** Getting All real time status from url **/
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            MenuActivity.realTimeQuantityList.clear();

            jsonParser.makeHttpRequest(HomeActivity.url_check_status, "GET", params);
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(HomeActivity.url_get_all_charging_station, "GET", params);

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(HomeActivity.TAG_SUCCESS);

                if (success == 1) {
                    // real time charging station found
                    // Getting Array of real time status
                    JSONArray status = json.getJSONArray(HomeActivity.TAG_TABLE_CHARGING_STATION);

                    // looping through All real time status
                    for (int i = 0; i < status.length(); i++) {
                        JSONObject c = status.getJSONObject(i);

                        // Storing each json item in variable
                        String quantity = c.getString(HomeActivity.TAG_CHARGINGSTATION_QUANTITY);

                        // adding to ArrayList
                        MenuActivity.realTimeQuantityList.add(i, quantity);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    /** Enable QR scanning mode */
    private void scanQR(View v) {
        try {
            // start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            // on catch, show the download dialog
            showDownloadDialog(this);
        }
    }

    /** Show alert dialog for not having scanner application **/
    private void showDownloadDialog(final Activity act) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);
        alertDialog.setTitle(R.string.realtime_alertDialog_no_scanner_title);
        alertDialog.setMessage(R.string.realtime_alertDialog_no_scanner_text);
        alertDialog.setNegativeButton(R.string.realtime_alertDialog_no_scanner_no_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.setPositiveButton(R.string.realtime_alertDialog_no_scanner_yes_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /** Show confirm dialog for scanning a QR code **/
    private void showConfirmDialog(final Activity act) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);
        alertDialog.setTitle(R.string.realtime_confirmDialog_scan_title);
        String message = getString(R.string.realtime_confirmDialog_scan_text_description) + description + "\n"
                            + getString(R.string.realtime_confirmDialog_scan_text_address) + address + "\n"
                            + getString(R.string.realtime_confirmDialog_scan_text_district) + district + "\n"
                            + getString(R.string.realtime_confirmDialog_scan_text_type) + type + "\n"
                            + getString(R.string.realtime_confirmDialog_scan_text_socket) + socket;
        // + decrypted + "\n"
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(R.string.realtime_confirmDialog_scan_no_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.setPositiveButton(R.string.realtime_confirmDialog_scan_yes_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                new UpdateDetails().execute();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /** Get the content of QR code from the scanner */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        setLocale(HomeActivity.language);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // Check if it is QR code
                if(format.equals("QR_CODE")) {

                    String strToDecrypt = contents;
                    String strPssword = "tommyhui";

                    // Decrypt the content of QR code
                    AESencryption AESencryption = new AESencryption(strPssword);
                    decrypted = AESencryption.decrypt(strToDecrypt.trim());
                    QRCodeDecode(decrypted);
                }
            }
        }
    }

    /** Decode the content of QR code from the scanner */
    private void QRCodeDecode(String contents) {
        if(validQRCode(contents)) {
            // Valid QR code
            contents = contents.trim();
            int index = Integer.parseInt(contents.split(":")[1]);
            if(validLocation(contents)) {
                // Dig out the data of the charging station and put into database
                index_cs = Integer.toString(index);

                ItemCS scannedCS = HomeActivity.matchingList.get(index);
                district = scannedCS.getDistrict();
                description = scannedCS.getDescription();
                address = scannedCS.getAddress();
                type = scannedCS.getType();
                socket = scannedCS.getSocket();

                showConfirmDialog(this);
            }
            else
                // Invalid location
                showUnmatchedLocationDialog();
        }
        else
            // Invalid QR code
            showInvalidQRDialog();
    }

    private void showUnmatchedLocationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.realtime_alertDialog_unmatched_location_title);
        alertDialog.setMessage(R.string.realtime_alertDialog_unmatched_location_text);
        alertDialog.setNeutralButton(R.string.realtime_alertDialog_unmatched_ok_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showInvalidQRDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.realtime_alertDialog_invalid_qr_title);
        alertDialog.setMessage(R.string.realtime_alertDialog_invalid_qr_text);
        alertDialog.setNeutralButton(R.string.realtime_alertDialog_invalid_qr_ok_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showSuccessDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.realtime_alertDialog_success_register_title);
        alertDialog.setMessage(R.string.realtime_alertDialog_success_register_text);
        alertDialog.setNeutralButton(R.string.realtime_alertDialog_success_register_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    /** Check whether it is a valid QR code */
    private boolean validQRCode(String contents) {

        boolean result = true;

        if(!contents.contains("com.example.tommyhui.evcapplication/index:"))
            result = false;
        else {
            if(Integer.parseInt(contents.split(":")[1].trim()) >= HomeActivity.matchingList.size() || Integer.parseInt(contents.split(":")[1].trim()) < 0)
                result = false;
        }
        return result;
    }

    /** Check whether the QR code is matched with user location */
    private boolean validLocation(String contents) {

        boolean result = false;

        contents = contents.trim();
        int index = Integer.parseInt(contents.split(":")[1]);

        LatLng currentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        LatLng referenceLocationOfChargingStation = new LatLng(Double.parseDouble(HomeActivity.matchingList.get(index).getLatitude()), Double.parseDouble(HomeActivity.matchingList.get(index).getLongitude()));

        // To check if the QR code is scanned in a specified position *****
        if(getDistance(currentLocation, referenceLocationOfChargingStation) <= 50)
            result = true;

        Log.v("Distance", getDistance(currentLocation, referenceLocationOfChargingStation) + "");
        return result;
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 10, this);

    }

    /** Handle the case when user position changes **/
    public void onLocationChanged(Location location) {

        double latInDouble = location.getLatitude();
        double lonInDouble = location.getLongitude();

        myLocation = location;

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

    /** Calculate the displacement between user current position and specific location of charging station **/
    public double getDistance(LatLng LatLng1, LatLng LatLng2) {

        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);

        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);

        double distance = locationA.distanceTo(locationB);
        return distance;
    }

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