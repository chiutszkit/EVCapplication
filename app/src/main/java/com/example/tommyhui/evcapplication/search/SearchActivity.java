package com.example.tommyhui.evcapplication.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;

import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity {

    private Spinner spinnerDescription, spinnerDistrict, spinnerType, spinnerSocket, spinnerQuantity;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*Set Action Bar's Title*/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Search");

        /*Set Action Bar's Icon*/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.search_icon);

        /*Set Up the Spinner*/
        addItemsOnSpinners();

        Button button = (Button) findViewById(R.id.search_button_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.setClass(SearchActivity.this, SearchResultActivity.class);
                startActivity(resultIntent);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_options_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    public void addItemsOnSpinners() {

        spinnerDescription = (Spinner) findViewById(R.id.search_spinner_description);
        spinnerDistrict = (Spinner) findViewById(R.id.search_spinner_district);
        spinnerType = (Spinner) findViewById(R.id.search_spinner_type);
        spinnerSocket = (Spinner) findViewById(R.id.search_spinner_socket);
        spinnerQuantity = (Spinner) findViewById(R.id.search_spinner_quantity);

        ArrayList<String> descriptionList = new ArrayList<String>();
        ArrayList<String> districtList = new ArrayList<String>();
        ArrayList<String> typeList = new ArrayList<String>();
        ArrayList<String> socketList = new ArrayList<String>();
        ArrayList<String> quantityList = new ArrayList<String>();

        ItemCS_DBController db = new ItemCS_DBController(this);

        queryArrayListFromDb(db, "description", descriptionList);
        queryArrayListFromDb(db,"district", districtList);
        queryArrayListFromDb(db, "type", typeList);
        queryArrayListFromDb(db, "socket", socketList);
        queryArrayListFromDb(db, "quantity", quantityList);

        arrayListToSpinners(descriptionList,spinnerDescription);
        arrayListToSpinners(districtList,spinnerDistrict);
        arrayListToSpinners(typeList,spinnerType);
        arrayListToSpinners(socketList,spinnerSocket);
        arrayListToSpinners(quantityList,spinnerQuantity);


    }

    public void arrayListToSpinners(ArrayList<String> list, Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void queryArrayListFromDb(ItemCS_DBController db, String column, ArrayList<String> list) {

        ArrayList<ItemCS> ItemCSes;
        list.clear();
        ItemCSes = db.inputQueryCSes(this, column);
        for (int i = 0; i < ItemCSes.size(); i++) {
            switch(column) {
                case "description":
                    list.add(i, ItemCSes.get(i).getDescription());
                    break;
                case "district":
                    list.add(i, ItemCSes.get(i).getDistrict());
                    break;
                case "type":
                    list.add(i, ItemCSes.get(i).getType());
                    break;
                case "socket":
                    list.add(i, ItemCSes.get(i).getSocket());
                    break;
                case "quantity":
                    list.add(i, ItemCSes.get(i).getQuantity() + "");
                    break;
            }
        }
        list.add(0,"ALL");
    }
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}