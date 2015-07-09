package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.overview.OverviewHistoryFragmentActivity;
import com.example.tommyhui.evcapplication.overview.OverviewListFragmentActivity;


public class OverviewPagerAdapter extends FragmentPagerAdapter {

    Context mContext;
    private String tabTitles[];

    public OverviewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        tabTitles = new String[]{mContext.getResources().getString(R.string.overview_tab_list), mContext.getResources().getString(R.string.overview_tab_history)};
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
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
        return tabTitles.length;
    }

    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}


