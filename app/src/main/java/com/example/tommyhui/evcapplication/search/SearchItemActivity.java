package com.example.tommyhui.evcapplication.search;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;

public class SearchItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_item_activity);

//        /*Hide Action Bar*/
//        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        String address = bundle.getString("address");
        String description = bundle.getString("description");
        String type = bundle.getString("type");
        String socket = bundle.getString("socket");
        Integer quantity  = bundle.getInt("quantity");

        TextView addressText = (TextView)findViewById(R.id.search_item_text_chargingStationAddress);
        addressText.setText(address);
        TextView descriptionText = (TextView)findViewById(R.id.search_item_text_description);
        descriptionText.setText(description);
        TextView typeText = (TextView)findViewById(R.id.search_item_text_type);
        typeText.setText("Type: " + type);
        TextView socketText = (TextView)findViewById(R.id.search_item_text_socket);
        socketText.setText("Socket: " + socket);
        TextView quantityText = (TextView)findViewById(R.id.search_item_text_quantity);
        quantityText.setText("Quantity: " + quantity.toString());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
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