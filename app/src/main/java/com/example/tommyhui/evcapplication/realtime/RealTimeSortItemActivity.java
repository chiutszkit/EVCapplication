package com.example.tommyhui.evcapplication.realtime;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;

import java.util.ArrayList;

public class RealTimeSortItemActivity extends ActionBarActivity {

    private String sort;
    private ItemCS_DBController db;
    private TextView spinnerTitle;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtime_sort_item_activity);

        /** Get the data passed **/
        Bundle bundle = getIntent().getExtras();
        sort = bundle.getString("sort");

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(sort);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.realtime_icon);

        /** Set up the database of item of charging station **/
        db = new ItemCS_DBController(this);

        /** Set up the spinner **/
        spinnerTitle = (TextView) findViewById(R.id.realtime_sort_item_title_sort);
        spinner = (Spinner) findViewById(R.id.realtime_sort_item_spinner_sort);

        ArrayList<String> spinnerList = new ArrayList<String>();
        queryArrayListFromDb(db, sort, spinnerList);
        arrayListToSpinners(spinnerList,spinner);
    }

    public void arrayListToSpinners(ArrayList<String> list, Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void queryArrayListFromDb(ItemCS_DBController db, String column, ArrayList<String> list) {

        ArrayList<ItemCS> queryItemCSes;
        list.clear();
        queryItemCSes = db.inputQueryCSes(this, new String[] {column}, 1);
        for (int i = 0; i < queryItemCSes.size(); i++) {
            switch(column) {
                case "District":
                    spinnerTitle.setText("District:");
                    list.add(i, queryItemCSes.get(i).getDistrict());
                    break;
                case "Type":
                    spinnerTitle.setText("Type:");
                    list.add(i, queryItemCSes.get(i).getType());
                    break;
                case "Socket":
                    spinnerTitle.setText("Socket:");
                    list.add(i, queryItemCSes.get(i).getSocket());
                    break;
                case "Availability":
                    spinnerTitle.setText("Availability:");
//                    list.add(i, queryItemCSes.get(i).getSocket());
                    break;
                case "All":
                    LinearLayout spinnerLayout = (LinearLayout) findViewById(R.id.realtime_sort_item_layout_sort);
                    spinnerLayout.setVisibility(LinearLayout.GONE);
//                    list.add(i, queryItemCSes.get(i).getQuantity() + "");
                    break;
            }
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