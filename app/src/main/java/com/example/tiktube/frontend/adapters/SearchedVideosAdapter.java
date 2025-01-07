package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktube.R;
import com.example.tiktube.backend.models.Video;

import java.util.List;

public class SearchedVideosAdapter extends BaseAdapter {
    private Context context;
    private List<Video> videos;

    public SearchedVideosAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_search, parent, false);
        }

        ImageView imgThumbnail = convertView.findViewById(R.id.imgThumbnail);
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        ImageView imgProfile = convertView.findViewById(R.id.imgProfile);
        TextView txtUsername = convertView.findViewById(R.id.txtUsername);
        TextView txtLikes = convertView.findViewById(R.id.txtLikes);

        Video video = videos.get(position);

        Glide.with(context).load(video.getVideoURL()).thumbnail(0.1F).into(imgThumbnail);
        txtTitle.setText(video.getTitle());
        txtLikes.setText("♡ " + video.getLikes().size());
        return convertView;
    }
}
