package com.example.tommyhui.evcapplication.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.overview.OverviewHistoryFragmentActivity;
import com.example.tommyhui.evcapplication.overview.OverviewListFragmentActivity;

import java.util.ArrayList;


public class OverviewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<ItemCS> itemCSes = new ArrayList<>();
    private String tabTitles[] = new String[]{"List", "History"};

    public OverviewPagerAdapter(FragmentManager fm) {
        super(fm);
//        this.itemCSes = items;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        // TODO Auto-generated method stub
        switch (position) {
            case 0:
                /*Pass the Overview List to OverviewListFragment*/
//                OverviewListFragmentActivity frag = new OverviewListFragmentActivity();
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("list", itemCSes);
//                frag.setArguments(bundle);
                return new OverviewListFragmentActivity();
            case 1:
                return new OverviewHistoryFragmentActivity();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return tabTitles.length;
    }

    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}


