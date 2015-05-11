package com.example.tommyhui.evcapplication.overview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.OverviewListViewAdapter;
import com.example.tommyhui.evcapplication.database.HistoryDBController;
import com.example.tommyhui.evcapplication.database.HistoryItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.search.SearchItemActivity;

import java.util.ArrayList;

public class OverviewListFragmentActivity extends Fragment {

    private HistoryDBController db;
    private HistoryItemCS history;
    private ArrayList<ItemCS> ListOfCSes = new ArrayList<>();

    public OverviewListFragmentActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        ListOfCSes = args.getParcelableArrayList("list");

//        Log.i("test-logging", "ListOfCSes.getCount() : " + ListOfCSes.size());
        return inflater.inflate(R.layout.overview_list_fragment, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onActivityCreated(savedInstanceState);
        db = new HistoryDBController(getActivity());

        /*To Display the List of CS with the Original List of CS*/
        ListView listview = (ListView) getActivity().findViewById(R.id.overview_list_view);
        listview.setAdapter(new OverviewListViewAdapter(getActivity(), ListOfCSes));
        listview.setTextFilterEnabled(true);

        /*To Handle the Click of item of a CS*/
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(((OverviewActivity)getActivity()).searchViewQuery == "")
                    ListOfCSes = ((OverviewActivity)getActivity()).ItemCSes;
                else
                    ListOfCSes = ((OverviewActivity)getActivity()).QueryItemCSes;
                ItemCS cs = ListOfCSes.get(position);

                history = new HistoryItemCS((cs.getAddress()), cs.getDistrict(), cs.getDescription(), cs.getType(), cs.getSocket(), cs.getQuantity());
                db.addHistoryCS(history);

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