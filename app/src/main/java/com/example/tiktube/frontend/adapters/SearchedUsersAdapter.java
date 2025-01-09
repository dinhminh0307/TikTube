package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiktube.R;
import com.example.tiktube.backend.helpers.ImageBuilder;
import com.example.tiktube.backend.models.User;

import java.util.List;

public class SearchedUsersAdapter extends BaseAdapter {
    private Context context;
    private List<User> users;
    private ImageBuilder imageBuilder;

    public SearchedUsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user_search, parent, false);
        }

        ImageView imgProfile = convertView.findViewById(R.id.imgProfile);
        TextView txtUsername = convertView.findViewById(R.id.txtUsername);
        TextView txtFollowers = convertView.findViewById(R.id.txtFollowers);

        User user = users.get(position);

        imageBuilder = new ImageBuilder(context);
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            imageBuilder.loadImage(imgProfile, user);
        }
        txtUsername.setText(user.getName());
        txtFollowers.setText((user.getFollowerList() != null ? Integer.toString(user.getFollowerList().size()) : "0") + " followers");
        return convertView;
    }
}
