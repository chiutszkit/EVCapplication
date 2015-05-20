package com.example.tommyhui.evcapplication.overview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.OverviewListViewAdapter;
import com.example.tommyhui.evcapplication.database.HistoryItemCS;
import com.example.tommyhui.evcapplication.database.HistoryItemCS_DBController;
import com.example.tommyhui.evcapplication.database.ItemCS;

import java.util.ArrayList;

public class OverviewListFragmentActivity extends Fragment {

    private HistoryItemCS_DBController db;
    private HistoryItemCS historyItem;
    private ArrayList<ItemCS> ListOfCSes = new ArrayList<>();
    private ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        /*Get the Overview List from OverviewActivity*/
//        Bundle args = getArguments();
//        ListOfCSes = args.getParcelableArrayList("list");
        ListOfCSes = ((OverviewActivity) getActivity()).ItemCSes;
        return inflater.inflate(R.layout.overview_list_fragment, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onActivityCreated(savedInstanceState);
        db = new HistoryItemCS_DBController(getActivity());

        /*To Display the List of CS with the Original List of CS*/
        listview = (ListView) getActivity().findViewById(R.id.overview_list_view);
        listview.setAdapter(new OverviewListViewAdapter(getActivity(), ListOfCSes));
        listview.setTextFilterEnabled(true);

        /*To Handle the Click of item of a CS*/
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (((OverviewActivity) getActivity()).searchViewQuery != "")
                    ListOfCSes = ((OverviewActivity) getActivity()).QueryItemCSes;
                else
                    ListOfCSes = ((OverviewActivity) getActivity()).ItemCSes;

                ItemCS cs = ListOfCSes.get(position);

                historyItem = new HistoryItemCS((cs.getAddress()), cs.getDistrict(), cs.getDescription(), cs.getType(), cs.getSocket(), cs.getQuantity(), cs.getLatitude(), cs.getLongitude());
                db.addHistoryCS(historyItem);

                Intent intent = new Intent();
                intent.setClass(getActivity(), ItemCSActivity.class);

                Bundle bundle = new Bundle();

                bundle.putString("address", cs.getAddress());
                bundle.putString("district", cs.getDistrict());
                bundle.putString("description", cs.getDescription());
                bundle.putString("type", cs.getType());
                bundle.putString("socket", cs.getSocket());
                bundle.putInt("quantity", cs.getQuantity());
                bundle.putString("latitude", cs.getLatitude());
                bundle.putString("longitude", cs.getLongitude());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}