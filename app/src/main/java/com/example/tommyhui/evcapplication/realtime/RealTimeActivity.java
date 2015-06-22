package com.example.tommyhui.evcapplication.realtime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.HomeActivity;
import com.example.tommyhui.evcapplication.JSONParser.RealTimeStatusJSONParser;
import com.example.tommyhui.evcapplication.R;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RealTimeActivity extends ActionBarActivity {

    private Set<String> realTimeStatusSet = new HashSet<String>();
    private RealTimeStatusJSONParser jsonParser = new RealTimeStatusJSONParser();
    private ProgressDialog pDialog;
    private String index_cs;
    private Location myLocation;

    // url to update product
    public static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    // url to update product
    private static String url_update_status = "http://" + HomeActivity.localhost + "/com.example.tommyhui.evcapplication/update_status.php";

    // JSON Node names
    private static final String TAG_SUCCESS = HomeActivity.TAG_SUCCESS;
    private static final String TAG_STATUS = HomeActivity.TAG_STATUS;
    private static final String TAG_INDEX = HomeActivity.TAG_INDEX;
    private static final String TAG_AVAILABILITY = HomeActivity.TAG_AVAILABILITY;
    private static final String TAG_UPDATED = HomeActivity.TAG_UPDATED;

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

        /** Set up list of real time status **/
//        realTimeStatusList

        /** Load the real time data **/
        locateUserPosition();

        LinearLayout background=(LinearLayout)findViewById(R.id.homepage);
        background.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RealTimeActivity.this, RealTimeSortActivity.class);
                startActivity(intent);
            }
        });
    }

     /** Background Async Task to  Save status Details **/
     class SaveStatusDetails extends AsyncTask<String, String, String> {

        /** Saving Status **/
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_INDEX, index_cs));
            params.add(new BasicNameValuePair(TAG_AVAILABILITY, "1"));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_status,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.realtime_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.realTime_action_record:
                scanQR(this.findViewById(android.R.id.content));
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
            //on catch, show the download dialog
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

    /** Get the content of QR code from the scanner */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // Check if it is QR code
                if(format.equals("QR_CODE"))
                    QRCodeDecode(contents);
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
//                if (!HomeActivity.realTimeStatusList.get(index).equals("available")) {
//
//                    SharedPreferences sharedPref = getSharedPreferences("UserConfigs", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPref.edit();
//
//                    HomeActivity.realTimeStatusList.add(index, "available");
//                    realTimeStatusSet.add(Integer.toString(index));
//                    editor.putStringSet("realTimeStatusList", realTimeStatusSet);
//                    editor.commit();
                index_cs = Integer.toString(index);

                new SaveStatusDetails().execute();

//                }
            }
            else {
                Toast toast = Toast.makeText(this, "Content:" + contents + "Location problem ", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        else {
            // Invalid QR code
            Toast toast = Toast.makeText(this, "Content:" + contents + " Invalid QR code ", Toast.LENGTH_LONG);
            toast.show();
        }
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

        // To check if the QR code is scanned in a specified position
        if(getDistance(currentLocation, referenceLocationOfChargingStation) <= 20000)
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
}