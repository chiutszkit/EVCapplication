package com.example.tommyhui.evcapplication.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.overview.OverviewHistoryFragmentActivity;
import com.example.tommyhui.evcapplication.overview.OverviewListFragmentActivity;

import java.util.ArrayList;


public class OverviewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<ItemCS> itemCSes = new ArrayList<>();

    public OverviewPagerAdapter(FragmentManager fm, ArrayList<ItemCS> items) {
        super(fm);
        this.itemCSes = items;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        // TODO Auto-generated method stub
        switch(position){
            case 0:
                OverviewListFragmentActivity frag = new OverviewListFragmentActivity();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("items", itemCSes);
                frag.setArguments(bundle);
                return frag;
            case 1:

                //do the similar thing as OverviewListFragment

                return new OverviewHistoryFragmentActivity();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 2;
    }

}


