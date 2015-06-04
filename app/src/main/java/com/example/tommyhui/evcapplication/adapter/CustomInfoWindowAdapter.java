package com.example.tommyhui.evcapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    LayoutInflater inflater = null;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoWindow = inflater.inflate(R.layout.custom_info_window_layout, null);

        TextView tv=(TextView)infoWindow.findViewById(R.id.custom_info_window_item_chargingStation);
        tv.setText(marker.getTitle());
        tv=(TextView)infoWindow.findViewById(R.id.custom_info_window_item_address);
        tv.setText(marker.getSnippet());
        tv=(TextView)infoWindow.findViewById(R.id.custom_info_window_item_distance);
        tv.setText(marker.getSnippet());
        tv=(TextView)infoWindow.findViewById(R.id.custom_info_window_item_time);
        tv.setText(marker.getSnippet());

        return(infoWindow);
    }
}

