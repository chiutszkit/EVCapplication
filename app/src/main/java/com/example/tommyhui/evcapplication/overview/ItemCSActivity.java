package com.example.tommyhui.evcapplication.overview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS;
import com.example.tommyhui.evcapplication.database.FavoriteItemCS_DBController;

public class ItemCSActivity extends Activity {

    private String address;
    private String district;
    private String description;
    private String type;
    private String socket;
    private Integer quantity;

    private FavoriteItemCS_DBController db;
    private FavoriteItemCS favouriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chargingstation_item_activity);

//        /*Hide Action Bar*/
//        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        address = bundle.getString("address");
        district = bundle.getString("district");
        description = bundle.getString("description");
        type = bundle.getString("type");
        socket = bundle.getString("socket");
        quantity  = bundle.getInt("quantity");

        TextView addressText = (TextView)findViewById(R.id.item_text_chargingStationAddress);
        addressText.setText(address);
        TextView descriptionText = (TextView)findViewById(R.id.item_text_description);
        descriptionText.setText(description);
        TextView typeText = (TextView)findViewById(R.id.item_text_type);
        typeText.setText("Type: " + type);
        TextView socketText = (TextView)findViewById(R.id.item_text_socket);
        socketText.setText("Socket: " + socket);
        TextView quantityText = (TextView)findViewById(R.id.item_text_quantity);
        quantityText.setText("Quantity: " + quantity.toString());

        Button favoriteButton = (Button) findViewById(R.id.item_button_favorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                db = new FavoriteItemCS_DBController(getApplicationContext());
                favouriteItem = new FavoriteItemCS(address, district, description, type, socket, quantity);
                if(db.addFavoriteCS(favouriteItem) == null)
                    Toast.makeText(getApplicationContext(), R.string.item_toast_addToFavorites_before, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), R.string.item_toast_addToFavorites, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}