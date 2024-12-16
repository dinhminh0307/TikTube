package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tiktube.R;

import java.util.List;

public class TrendingSearchesAdapter extends BaseAdapter {
    private Context context;
    private List<String> trendingEntries;

    public TrendingSearchesAdapter(Context context, List<String> trendingEntries) {
        this.context = context;
        this.trendingEntries = trendingEntries;
    }

    @Override
    public int getCount() {
        return trendingEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return trendingEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trending_search_item, parent, false);
        }

        TextView trendingEntry = convertView.findViewById(R.id.trendingEntry);

        String trendingQuery = trendingEntries.get(position);
        trendingEntry.setText(truncateText(trendingQuery));

        return convertView;
    }

    private String truncateText(String text) {
        if (text.length() > 34) {
            return text.substring(0, 33) + "...";
        }
        return text;
    }
}
