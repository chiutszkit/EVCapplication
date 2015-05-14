package com.example.tommyhui.evcapplication.socket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.SocketListPageListViewAdapter;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS_DBController;
import com.example.tommyhui.evcapplication.nearby.NearbyActivity;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SocketListActivity extends ActionBarActivity {

    private String type = "";
    private ArrayList<ItemCS> socketList = new ArrayList<>();
    private SocketListPageListViewAdapter socketListPageListViewAdapter;
    private ItemCS_DBController db;
    private StickyListHeadersListView  listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_listpage_activity);

        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");

        /*Use Customized Action Bar*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        /*Set Action Bar's Title*/
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText(type + " Charger");

        /*Set Action Bar's Icon*/
        int resId = 0;
        switch (type) {
            case "Standard":
                resId = R.drawable.standard_icon;
                break;
            case "Medium":
                resId = R.drawable.medium_icon;
                break;
            case "Quick":
                resId = R.drawable.quick_icon;
                break;
            default:
                break;
        }
        ImageView myImgView = (ImageView) findViewById(R.id.action_bar_icon);
        myImgView.setImageResource(resId);

        db = new ItemCS_DBController(getApplicationContext());
        socketList = db.inputQueryCSes(this, type);
//
//        mCurrentStickyHeaderSection = -1;
//
//        mStickyHeader = (TextView) findViewById(R.id.textview_sticky_header_section);
//        mStickyHeader2 = (TextView) findViewById(R.id.textview_sticky_header_section_2);
//
//        listView = (ListView) findViewById(R.id.socket_listpage_list_view);
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                updateStickyHeader(firstVisibleItem);
//            }
//
//            @Override
//            public void onScrollStateChanged(AbsListView arg0, int arg1) {
//                // TODO Auto-generated method stub
//            }
//        });
//
        listView = (StickyListHeadersListView) this.findViewById(R.id.socket_listpage_list_view);

        if (socketListPageListViewAdapter == null) {
            socketListPageListViewAdapter = new SocketListPageListViewAdapter(SocketListActivity.this, socketList);
            listView.setAdapter(socketListPageListViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    // TODO Auto-generated method stub

                    ItemCS cs = socketList.get(position);

                    Intent intent = new Intent();
                    intent.setClass(SocketListActivity.this, NearbyActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putString("type", type);
                    bundle.putString("address", cs.getAddress());
                    bundle.putString("description", cs.getDescription());

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }
//    private void updateStickyHeader(int firstVisibleItem) {
//        // here is the tricky part. You have to determine pos and section
//        // pos is the position within a section pos = -1 means it's the header of the section
//        // you have to determine if firstVisibleItem is a header or not
//        // you also have to determine the section to which belongs the first item
//        int pos =  1;
//        int section =  1;
//
//        if (section != mCurrentStickyHeaderSection) {
//            mStickyHeader.setText("Your_Previous_Section_Text");
//            mStickyHeader2.setText("Your_Next_Section_Text");
//            mCurrentStickyHeaderSection = section;
//        }
//
//        int stickyHeaderHeight = mStickyHeader.getHeight();
//        if (stickyHeaderHeight == 0) {
//            stickyHeaderHeight = mStickyHeader.getMeasuredHeight();
//        }
//
//        View SectionLastView = listView.getChildAt(0);
//        if (SectionLastView != null && pos == -1 && SectionLastView.getBottom() <= stickyHeaderHeight) {
//            int lastViewBottom = SectionLastView.getBottom();
//            mStickyHeader.setTranslationY(lastViewBottom - stickyHeaderHeight);
//            mStickyHeader2.setTranslationY(lastViewBottom - stickyHeaderHeight + mStickyHeader.getHeight());
//        } else if (stickyHeaderHeight != 0) {
//            mStickyHeader.setTranslationY(0);
//            mStickyHeader2.setTranslationY(mStickyHeader.getHeight());
//        }
//    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_options_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
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
