package com.example.tommyhui.evcapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tommyhui.evcapplication.menu.MenuActivity;

public class HomeActivity extends Activity {

    LinearLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        background = (LinearLayout) findViewById(R.id.homepage);
        background.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}