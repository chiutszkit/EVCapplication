package com.example.tommyhui.evcapplication.realtime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.HomeActivity;
import com.example.tommyhui.evcapplication.JSONParser.RealTimeStatusJSONParser;
import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.RealTimeLogListViewAdapter;
import com.example.tommyhui.evcapplication.util.ConnectionDetector;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RealTimeLogActivity extends ActionBarActivity {

    private RealTimeStatusJSONParser jsonParser = new RealTimeStatusJSONParser();
    private ArrayList<HashMap<String, String>> logsList = new ArrayList<HashMap<String, String>>();;
    private JSONArray logs = null;
    private RealTimeLogListViewAdapter realTimeLogListViewAdapter;
    private ListView listView;
    private ConnectionDetector cd = new ConnectionDetector(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtime_log_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.realtime_log_page_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.log_icon);

        /** Load the logs of Real Time Status **/
        if (cd.isConnectingToInternet())
            new LoadAllLogs().execute();
    }

    /** Background Async Task to Load all logs by making HTTP Request **/
    class LoadAllLogs extends AsyncTask<String, String, String> {

        /** Getting All logs from url **/
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(HomeActivity.url_get_all_log, "GET", params);

            // Check your log cat for JSON response
            Log.d("All Log: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(HomeActivity.TAG_SUCCESS);

                if (success == 1) {
                    // logs found
                    // Getting Array of logs
                    logs = json.getJSONArray(HomeActivity.TAG_TABLE_LOG);

                    // looping through All logs
                    for (int i = 0; i < logs.length(); i++) {
                        JSONObject c = logs.getJSONObject(i);

                        // Storing each json item in variable
                        String district = c.getString(HomeActivity.TAG_LOG_DISTRICT);
                        String description = c.getString(HomeActivity.TAG_LOG_DESCRIPTION);
                        String type = c.getString(HomeActivity.TAG_LOG_TYPE);
                        String socket = c.getString(HomeActivity.TAG_LOG_SOCKET);
                        String created_at = c.getString(HomeActivity.TAG_LOG_TIME);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(HomeActivity.TAG_LOG_DISTRICT, district);
                        map.put(HomeActivity.TAG_LOG_DESCRIPTION, description);
                        map.put(HomeActivity.TAG_LOG_TYPE, type);
                        map.put(HomeActivity.TAG_LOG_SOCKET, socket);
                        map.put(HomeActivity.TAG_LOG_TIME, created_at);

                        // adding HashList to ArrayList
                        logsList.add(i, map);
                    }
                } else {
                    // no logs found
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showEmptyLogDialog();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        // Called when doInBackground() is finished
        protected void onPostExecute(String file_url) {
            // Updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Updating parsed JSON data into ListView
                    realTimeLogListViewAdapter = new RealTimeLogListViewAdapter(RealTimeLogActivity.this, logsList);
                    listView = (ListView) findViewById(R.id.realtime_log_list_view);
                    listView.setAdapter(realTimeLogListViewAdapter);
                }
            });
        }
    }
    /** Show alert dialog for empty list of search item **/
    private void showEmptyLogDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.realtime_log_alertDialog_no_log_title);
        alertDialog.setMessage(R.string.realtime_log_alertDialog_no_log_text);
        alertDialog.setNeutralButton(R.string.realtime_log_alertDialog_ok_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                RealTimeLogActivity.this.finish();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}