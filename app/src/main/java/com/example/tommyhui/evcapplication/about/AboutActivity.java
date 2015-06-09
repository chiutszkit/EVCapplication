package com.example.tommyhui.evcapplication.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.AboutListViewAdapter;

import java.lang.reflect.Field;

public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        /** Use customized action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /** Set up action bar's title **/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(R.string.about_title);

        /** Set up action bar's icon **/
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(R.drawable.about_icon);

        /** Display action bar's icon **/
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** Display the listview **/
        final ListView listview = (ListView) findViewById(R.id.about_list_view);

        int[] aboutTitle = new int[]{
                R.string.about_author, R.string.about_telephone, R.string.about_email, R.string.about_version, R.string.about_date
        };

        int[] aboutItem = new int[]{
                R.string.about_author_name, R.string.about_author_tel, R.string.about_author_email, R.string.about_app_version, R.string.about_app_date
        };

        int[] aboutDrawable = new int[]{
                R.drawable.avatar_icon, R.drawable.phone_icon, R.drawable.email_icon, R.drawable.android_icon, R.drawable.date_icon
        };

        AboutListViewAdapter adapter = new AboutListViewAdapter(this, aboutDrawable, aboutTitle, aboutItem);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 1:
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.about_author_tel))));
                        break;
                    case 2:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.about_author_email)});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.about_email_subject);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, R.string.about_email_text);

                        CharSequence cs = getString(R.string.about_email_intent);
                        startActivity(Intent.createChooser(emailIntent, cs));
                        break;
                    default:
                }
            }
        });
    }
}
