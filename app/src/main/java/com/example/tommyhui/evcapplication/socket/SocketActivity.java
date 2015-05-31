package com.example.tommyhui.evcapplication.socket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.SocketListViewAdapter;

import java.util.ArrayList;

public class SocketActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_activity);

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*Set Action Bar's Title*/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Socket Types");

        /*Set Action Bar's Icon*/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.big_socket);

        /*Display the List View*/
        final ListView listview = (ListView) findViewById(R.id.socket_list_view);

        final int[] typeTitle = new int[]{
                R.string.socket_type_standard_title, R.string.socket_type_medium_title, R.string.socket_type_quick_title
        };

        int[] typeSubtitle = new int[]{
                R.string.socket_type_standard_subtitle, R.string.socket_type_medium_subtitle, R.string.socket_type_quick_subtitle
        };

        int[] typeIcon = new int[]{
                R.drawable.standard_icon, R.drawable.medium_icon, R.drawable.quick_icon
        };

        SocketListViewAdapter adapter = new SocketListViewAdapter(this, typeIcon, typeTitle, typeSubtitle);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent();
                intent.setClass(SocketActivity.this, SocketListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("type", getString(typeTitle[position]));

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}