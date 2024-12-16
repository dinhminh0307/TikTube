package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tiktube.R;

import java.util.List;

public class PastSearchesAdapter extends BaseAdapter {
    private Context context;
    private List<String> searchEntries;

    public PastSearchesAdapter(Context context, List<String> searchEntries) {
        this.context = context;
        this.searchEntries = searchEntries;
    }

    @Override
    public int getCount() {
        return searchEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return searchEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.past_search_item, parent, false);
        }

        TextView searchEntry = convertView.findViewById(R.id.searchEntry);
        TextView searchBtnRemove = convertView.findViewById(R.id.searchBtnRemove);

        String searchQuery = searchEntries.get(position);
        searchEntry.setText(truncateText(searchQuery));

        return convertView;
    }

    private String truncateText(String text) {
        if (text.length() > 34) {
            return text.substring(0, 33) + "...";
        }
        return text;
    }
}
