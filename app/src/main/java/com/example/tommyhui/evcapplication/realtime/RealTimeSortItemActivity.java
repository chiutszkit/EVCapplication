package com.example.tommyhui.evcapplication.realtime;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.RealTimeSortItemListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.menu.MenuActivity;

import java.util.ArrayList;

public class RealTimeSortItemActivity extends ActionBarActivity {

    private String title;
    private String sort;
    private ItemCS_DBController db;
    private TextView spinnerTitle;
    private Spinner spinner;

    private String district = "district";
    private String type = "type";
    private String socket = "socket";
    private String availability = "availability";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtime_sort_item_activity);

        /** Get the data passed **/
        Bundle bundle = getIntent().getExtras();
        sort = bundle.getString("sort");
        title = bundle.getString("title");

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(this.title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.inquiry_icon);

        /** Set up the database of item of charging station **/
        db = new ItemCS_DBController(this);

        /** Set up the spinner **/
        spinnerTitle = (TextView) findViewById(R.id.realtime_sort_item_title_sort);
        spinner = (Spinner) findViewById(R.id.realtime_sort_item_spinner_sort);

        ArrayList<String> spinnerList = new ArrayList<String>();
        queryArrayListFromDb(db, sort, spinnerList);
        arrayListToSpinners(spinnerList, spinner);
        addListenerOnSpinners();

    }

    public void arrayListToSpinners(ArrayList<String> list, Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinners() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!sort.equals(availability)) {

                    String query = spinner.getItemAtPosition(position).toString();
                    ArrayList<ItemCS> queryItemCSes = db.inputQueryCSes(RealTimeSortItemActivity.this, new String[]{sort.toLowerCase(), query}, 2);

                    final ListView listview = (ListView) findViewById(R.id.realtime_sort_item_list_view);
                    RealTimeSortItemListViewAdapter adapter = new RealTimeSortItemListViewAdapter(RealTimeSortItemActivity.this, queryItemCSes);
                    listview.setAdapter(adapter);

                    TextView total = (TextView) findViewById(R.id.realtime_sort_item_text_total);
                    total.setText(Integer.toString(queryItemCSes.size()));

                    TextView available = (TextView) findViewById(R.id.realtime_sort_item_text_available);
                    available.setText(Integer.toString(getAvailableNumber(queryItemCSes)));

                    TextView unavailable = (TextView) findViewById(R.id.realtime_sort_item_text_unavailable);
                    unavailable.setText(Integer.toString(queryItemCSes.size() - getAvailableNumber(queryItemCSes)));
                }
                else {
                    ArrayList<ItemCS> tempList = db.inputQueryCSes(RealTimeSortItemActivity.this, new String[]{}, 3);
                    ArrayList<ItemCS> queryItemCSes = new ArrayList<ItemCS>();

                    String option = spinner.getItemAtPosition(position).toString();

                    if(option.equals(getString(R.string.realtime_sort_item_text_available))) {
                        for (ItemCS temp : tempList) {
                            if (!MenuActivity.realTimeQuantityList.get(temp.getMatching_index()).equals("0"))
                            queryItemCSes.add(temp);
                        }
                    }
                    else {
                        for (ItemCS temp : tempList) {
                            if (MenuActivity.realTimeQuantityList.get(temp.getMatching_index()).equals("0"))
                                queryItemCSes.add(temp);
                        }
                    }

                    final ListView listview = (ListView) findViewById(R.id.realtime_sort_item_list_view);
                    RealTimeSortItemListViewAdapter adapter = new RealTimeSortItemListViewAdapter(RealTimeSortItemActivity.this, queryItemCSes);
                    listview.setAdapter(adapter);

                    LinearLayout layout = (LinearLayout) findViewById(R.id.realtime_sort_item_layout_statistic);
                    layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public int getAvailableNumber(ArrayList<ItemCS> itemList) {

        int count = 0;

        for (int i = 0; i < itemList.size(); i++) {
            String quantity = MenuActivity.realTimeQuantityList.get(itemList.get(i).getMatching_index());

            if (!quantity.equals("0"))
                count++;
        }
        return count;
    }

    public void queryArrayListFromDb(ItemCS_DBController db, String column, ArrayList<String> list) {

        ArrayList<ItemCS> queryItemCSes;
        list.clear();

        spinnerTitle.setText(title + ":");

        if(!column.equals(availability)) {

            queryItemCSes = db.inputQueryCSes(this, new String[]{column}, 1);

            for (int i = 0; i < queryItemCSes.size(); i++) {

                if (column.equals(district))
                    list.add(i, queryItemCSes.get(i).getDistrict());
                else if (column.equals(type))
                    list.add(i, queryItemCSes.get(i).getType());
                else if (column.equals(socket))
                    list.add(i, queryItemCSes.get(i).getSocket());
            }
        }
        else {
            list.add(getString(R.string.realtime_sort_item_text_available));
            list.add(getString(R.string.realtime_sort_item_text_unavailable));
        }
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