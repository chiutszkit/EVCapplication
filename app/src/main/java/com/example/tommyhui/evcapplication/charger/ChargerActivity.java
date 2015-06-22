package com.example.tommyhui.evcapplication.charger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.ChargerListViewAdapter;

public class ChargerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charger_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.charger_type_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.big_socket);

        /** Display the list of socket **/
        final ListView listview = (ListView) findViewById(R.id.charger_list_view);

        final int[] typeTitle = new int[]{
                R.string.charger_type_standard_title, R.string.charger_type_medium_title, R.string.charger_type_quick_title
        };

        int[] typeSubtitle = new int[]{
                R.string.charger_type_standard_subtitle, R.string.charger_type_medium_subtitle, R.string.charger_type_quick_subtitle
        };

        int[] typeIcon = new int[]{
                R.drawable.standard_icon, R.drawable.medium_icon, R.drawable.quick_icon
        };

        ChargerListViewAdapter adapter = new ChargerListViewAdapter(this, typeIcon, typeTitle, typeSubtitle);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent();
                intent.setClass(ChargerActivity.this, ChargerListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("type", getString(typeTitle[position]));

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}