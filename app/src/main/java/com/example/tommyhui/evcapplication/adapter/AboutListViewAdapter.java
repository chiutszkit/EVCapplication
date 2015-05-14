package com.example.tommyhui.evcapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tommyhui.evcapplication.R;

public class AboutListViewAdapter extends BaseAdapter {
    private final Context context;
    private final int[] icons;
    private final int[] titles;
    private final int[] texts;

    public AboutListViewAdapter(Context context, int[] icons, int[] titles, int[] texts) {
        this.context = context;
        this.icons = icons;
        this.titles = titles;
        this.texts = texts;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.about_list_item_layout, parent, false);

        android.view.ViewGroup.LayoutParams layoutParams = rowView.getLayoutParams();
        int totalHeight = parent.getMeasuredHeight();
        int rowHeight = totalHeight / getCount();
        layoutParams.height = rowHeight;
        rowView.setLayoutParams(layoutParams);

        TextView textTitleView = (TextView) rowView.findViewById(R.id.about_list_view_item_title);
        TextView textView = (TextView) rowView.findViewById(R.id.about_list_view_item_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.about_list_view_item_icon);
        textTitleView.setText(context.getString(titles[position]));
        textView.setText(context.getString(texts[position]));
        imageView.setImageResource(icons[position]);

        return rowView;
    }

}

