package com.example.tommyhui.evcapplication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.tommyhui.evcapplication.overview.OverviewHistoryFragmentActivity;
import com.example.tommyhui.evcapplication.overview.OverviewListFragmentActivity;


public class OverviewPagerAdapter extends FragmentPagerAdapter{

    public OverviewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        switch(position){
            case 0:
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
        return 2;
    }

}

