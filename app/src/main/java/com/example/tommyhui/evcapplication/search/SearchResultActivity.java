package com.example.tommyhui.evcapplication.search;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.SearchResultListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.menu.MenuActivity;
import com.example.tommyhui.evcapplication.overview.ItemCSActivity;

import java.util.ArrayList;

public class SearchResultActivity extends ActionBarActivity {

    private String district;
    private String description;
    private String type;
    private String socket;
    private String quantity;
    private Boolean nearest;
    private Boolean availability;

    private ArrayList<ItemCS> searchResultList = new ArrayList<ItemCS>();
    private ItemCS_DBController db;

    private SearchResultListViewAdapter searchResultListViewAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.searchresult_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.filter_icon);

        /** Get the data passed **/
        Bundle bundle = getIntent().getExtras();

        district = bundle.getString("district");
        description = bundle.getString("description");
        type = bundle.getString("type");
        socket = bundle.getString("socket");
        quantity = bundle.getString("quantity");
        nearest = bundle.getBoolean("nearest");
        availability = bundle.getBoolean("availability");

        ArrayList<ItemCS> tempList;

        db = new ItemCS_DBController(getApplicationContext());
        tempList = db.inputQueryCSes(this, new String[] {district, description, type, socket, quantity}, 1);

        if(nearest || availability)
            searchResultList = Sorting(tempList);
        else
            searchResultList = tempList;

        if(searchResultList.size() == 0)
            showEmptyListDialog();

        Log.v("Debug", searchResultList.size() + "");

        listView = (ListView) this.findViewById(R.id.searchresult_list_view);

        searchResultListViewAdapter = new SearchResultListViewAdapter(SearchResultActivity.this, searchResultList);
        listView.setAdapter(searchResultListViewAdapter);

        // To show the search result
        TextView status = (TextView) findViewById(R.id.searchresult_text_status);
        Spanned result = Html.fromHtml("<strong>" + searchResultList.size() + "</strong>" + getString(R.string.searchresult_search_result_text));
        status.setText(result);
        LinearLayout layout = (LinearLayout) findViewById(R.id.searchresult_layout_status);
        layout.setVisibility(View.VISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                ItemCS cs = searchResultList.get(position);

                Intent intent = new Intent();
                intent.setClass(SearchResultActivity.this, ItemCSActivity.class);

                Bundle bundle = new Bundle();

                bundle.putString("address", cs.getAddress());
                bundle.putString("description", cs.getDescription());
                bundle.putString("type", cs.getType());
                bundle.putString("socket", cs.getSocket());
                bundle.putInt("quantity", cs.getQuantity());
                bundle.putString("latitude", cs.getLatitude());
                bundle.putString("longitude", cs.getLongitude());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /** Handle the case of sorting by nearest and available **/
    private ArrayList Sorting(ArrayList<ItemCS> tempList) {

        ArrayList<ItemCS> resultNearest = new ArrayList<ItemCS>();
        ArrayList<ItemCS> resultAvailability = new ArrayList<ItemCS>();
        ArrayList<ItemCS> result = new ArrayList<ItemCS>();

        double min = 0;
        boolean firstItem = true;
        if(MenuActivity.realTimeInfoList.size() > 0) {
            for (ItemCS socket : MenuActivity.realTimeInfoList) {
                for (ItemCS temp : tempList) {
                    if (socket.getLatitude().equals(temp.getLatitude()) && socket.getLongitude().equals(temp.getLongitude())) {
                        temp.setDistance(socket.getDistance());
                        temp.setTime(socket.getTime());
                        if (firstItem || Double.parseDouble(temp.getDistance()) < min) {
                            min = Double.parseDouble(temp.getDistance());
                            firstItem = false;
                        }
                    }
                }
            }
        }

        if(nearest) {
            for (ItemCS temp : tempList) {
                if (Double.parseDouble(temp.getDistance()) == min)
                    resultNearest.add(temp);
            }
        }
        if(availability) {
            for (ItemCS temp : tempList) {
                if(!MenuActivity.realTimeQuantityList.get(temp.getMatching_index()).equals("0"))
                    resultAvailability.add(temp);
            }
        }

        if(nearest | !availability)
            result = resultNearest;
        else if(!nearest | availability)
            result = resultAvailability;
        else {
            for(ItemCS nearestItem : resultNearest)
                for(ItemCS availabilityItem : resultAvailability)
                    if(nearestItem.equals(availabilityItem))
                        result.add(nearestItem);
        }

        return result;
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

    /** Show alert dialog for empty list of search item **/
    private void showEmptyListDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.searchresult_alertDialog_no_result_title);
        alertDialog.setMessage(R.string.searchresult_alertDialog_no_result_text);
        alertDialog.setNeutralButton(R.string.searchresult_alertDialog_revise_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                SearchResultActivity.this.finish();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}