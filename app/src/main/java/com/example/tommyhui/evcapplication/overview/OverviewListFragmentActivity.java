package com.example.tommyhui.evcapplication.overview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.OverviewListViewAdapter;
import com.example.tommyhui.evcapplication.database.DatabaseCS;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.search.SearchItemActivity;
import com.example.tommyhui.evcapplication.adapter.OverviewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class OverviewListFragmentActivity extends Fragment {

    private DatabaseCS db;
    private ArrayList<ItemCS> ListOfCSes = new ArrayList<>();

    public OverviewListFragmentActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // Get the Bundle Object
        Bundle args = getArguments();

        // Get ArrayList Bundle
//        Log.d("e", "To" + args.size());
//        ListOfCSes = args.getParcelableArrayList("list");
        if (args == null) {
            Toast.makeText(getActivity(), "arguments is null ", Toast.LENGTH_LONG).show();
        } else {
            //ListOfCSes = args.getParcelableArrayList("list");
            ListOfCSes = args.getParcelableArrayList("items");
            Toast.makeText(getActivity(), "text " + args , Toast.LENGTH_LONG).show();
        }

        Log.i("test-logging", "ListOfCSes.getCount() : " + ListOfCSes.size());

        return inflater.inflate(R.layout.overview_list_fragment, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onActivityCreated(savedInstanceState);




        /*To Display the List of Overview*/
        ListView listview = (ListView) getActivity().findViewById(R.id.overview_list_view);
        listview.setAdapter(new OverviewListViewAdapter(getActivity(), ListOfCSes));
        listview.setTextFilterEnabled(true);

        /*To Handle the Click of item of a CS*/
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ItemCS cs = ListOfCSes.get(position);

                Intent searchItemIntent = new Intent();
                searchItemIntent.setClass(getActivity(), SearchItemActivity.class);

                Bundle bundle = new Bundle();

                bundle.putString("address", cs.getAddress());
                bundle.putString("description", cs.getDescription());
                bundle.putString("type", cs.getType());
                bundle.putString("socket", cs.getSocket());
                bundle.putInt("quantity", cs.getQuantity());

                searchItemIntent.putExtras(bundle);
                startActivity(searchItemIntent);
            }
        });
    }
}