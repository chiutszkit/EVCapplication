package com.example.tommyhui.evcapplication.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;

import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity {

    private Spinner spinnerDescription, spinnerDistrict, spinnerType, spinnerSocket, spinnerQuantity;
    private CheckBox checkBoxNearest, checkBoxAvailability;
    private Button btnSearch, btnClear;
    private String all;

    ItemCS_DBController db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.search_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.search_icon);

        /** Set up the database of item of charging station **/
        db = new ItemCS_DBController(this);

        all = getString(R.string.search_text_all);

        /** Set up the spinner **/
        addListenerOnSpinners();

        /** Set up the checkbox, search and clear button **/
        addListenerOnButton();
    }

    public void addListenerOnSpinners() {

        addItemsOnSpinners();
        spinnerDescription = (Spinner) findViewById(R.id.search_spinner_description);
        spinnerDistrict = (Spinner) findViewById(R.id.search_spinner_district);

        spinnerDescription.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String description = spinnerDescription.getItemAtPosition(position).toString();

                if(!description.equals(all)) {
                    ArrayList<ItemCS> queryItemCSes = db.inputQueryCSes(SearchActivity.this, new String[] {description}, 2);

                    String district = queryItemCSes.get(0).getDistrict();
                    spinnerDistrict.setSelection(getIndexByValue(spinnerDistrict, district));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String description = spinnerDistrict.getItemAtPosition(position).toString();

                if(description.equals(all))
                    spinnerDescription.setSelection(getIndexByValue(spinnerDescription, all));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private int getIndexByValue(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void queryArrayListFromDb(ItemCS_DBController db, String column, ArrayList<String> list) {

        ArrayList<ItemCS> queryItemCSes;
        list.clear();
        queryItemCSes = db.inputQueryCSes(this, new String[] {column}, 1);
        for (int i = 0; i < queryItemCSes.size(); i++) {
            switch(column) {
                case "description":
                    list.add(i, queryItemCSes.get(i).getDescription());
                    break;
                case "district":
                    list.add(i, queryItemCSes.get(i).getDistrict());
                    break;
                case "type":
                    list.add(i, queryItemCSes.get(i).getType());
                    break;
                case "socket":
                    list.add(i, queryItemCSes.get(i).getSocket());
                    break;
                case "quantity":
                    list.add(i, queryItemCSes.get(i).getQuantity() + "");
                    break;
            }
        }
        list.add(0,all);
    }

    public void addListenerOnButton() {

        spinnerDescription = (Spinner) findViewById(R.id.search_spinner_description);
        spinnerDistrict = (Spinner) findViewById(R.id.search_spinner_district);
        spinnerType = (Spinner) findViewById(R.id.search_spinner_type);
        spinnerSocket = (Spinner) findViewById(R.id.search_spinner_socket);
        spinnerQuantity = (Spinner) findViewById(R.id.search_spinner_quantity);

        checkBoxNearest = (CheckBox) findViewById(R.id.search_checkbox_nearest);
        checkBoxAvailability = (CheckBox) findViewById(R.id.search_checkbox_availability);

        btnSearch = (Button) findViewById(R.id.search_button_search);
        btnClear = (Button) findViewById(R.id.search_button_clear);

        final int defaultPosition = 0;

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.setClass(SearchActivity.this, SearchResultActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("description", spinnerDescription.getSelectedItem().toString());
                bundle.putString("district", spinnerDistrict.getSelectedItem().toString());
                bundle.putString("type", spinnerType.getSelectedItem().toString());
                bundle.putString("socket", spinnerSocket.getSelectedItem().toString());
                bundle.putString("quantity", spinnerQuantity.getSelectedItem().toString());
                bundle.putBoolean("nearest", checkBoxNearest.isChecked());
                bundle.putBoolean("availability", checkBoxAvailability.isChecked());

                resultIntent.putExtras(bundle);
                startActivity(resultIntent);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                spinnerDescription.setSelection(defaultPosition);
                spinnerDistrict.setSelection(defaultPosition);
                spinnerType.setSelection(defaultPosition);
                spinnerSocket.setSelection(defaultPosition);
                spinnerQuantity.setSelection(defaultPosition);

                if(checkBoxNearest.isChecked()){
                    checkBoxNearest.toggle();
                }

                if(checkBoxAvailability.isChecked()){
                    checkBoxAvailability.toggle();
                }
            }
        });
    }
}