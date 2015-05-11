package com.example.tommyhui.evcapplication.overview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.HistoryListViewAdapter;
import com.example.tommyhui.evcapplication.database.HistoryDBController;
import com.example.tommyhui.evcapplication.database.HistoryItemCS;
import com.example.tommyhui.evcapplication.database.ItemCS;
import com.example.tommyhui.evcapplication.search.SearchItemActivity;

import java.util.ArrayList;

public class OverviewHistoryFragmentActivity extends Fragment {

    private ArrayList<HistoryItemCS> historyList = new ArrayList<>();
    private HistoryListViewAdapter historyListViewAdapter;
    private HistoryDBController db;
    private ListView listView;


    public OverviewHistoryFragmentActivity() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_history_fragment, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        db = new HistoryDBController(getActivity());
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        historyList = db.getAllHistoryCSes();
        listView = (ListView) getView().findViewById(R.id.overview_history_list_view);

        if(historyListViewAdapter == null){
            historyListViewAdapter = new HistoryListViewAdapter(getActivity(), historyList);
            listView.setAdapter(historyListViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    // TODO Auto-generated method stub

                    HistoryItemCS cs = historyList.get(position);

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
        else{
            historyListViewAdapter.setList(historyList);

            historyListViewAdapter.notifyDataSetChanged();
        }
        Button clearButton = (Button) getActivity().findViewById(R.id.overview_history_button_clear);
        clearButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                db = new HistoryDBController(v.getContext());
                for(int i = 0; i < historyList.size(); i++) {
                    HistoryItemCS cs = historyList.get(i);
                    db.deleteHistoryCS(cs);
                }
                historyList.clear();
                historyListViewAdapter.setList(historyList);
                historyListViewAdapter.notifyDataSetChanged();
            }
        });
    }
}