package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktube.R;
import com.example.tiktube.backend.models.Video;

import java.util.List;

public class VideoGridAdapter extends RecyclerView.Adapter<VideoGridAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videoList;
    private OnVideoClickListener onVideoClickListener;

    // Interface for handling video click
    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    public VideoGridAdapter(Context context, List<Video> videoList, OnVideoClickListener listener) {
        this.context = context;
        this.videoList = videoList;
        this.onVideoClickListener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_grid, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);

        // Use Glide to load the thumbnail
        Glide.with(context)
                .load(video.getVideoURL())
                .thumbnail(0.1f)
                .into(holder.thumbnail);

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            if (onVideoClickListener != null) {
                Log.d("Video grid adapter", "Video Clicked" );
                onVideoClickListener.onVideoClick(video);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
