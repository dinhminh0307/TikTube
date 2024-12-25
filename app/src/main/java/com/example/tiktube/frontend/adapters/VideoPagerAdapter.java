package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.controllers.UserController;

import java.util.List;

public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder> {

    private List<Video> videos;
    private Context context;

    private UserController userController;
    public VideoPagerAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
        userController = new UserController();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videos.get(position);

        // Set Video URI
        Uri videoUri = Uri.parse(video.getVideoURL());
        holder.videoView.setVideoURI(videoUri);

        // Start Video Playback
        holder.videoView.setOnPreparedListener(mp -> mp.start());
        holder.videoView.setOnCompletionListener(mp -> mp.start()); // Loop video

        // Set Video Metadata
        userController.getUserById(video.getOwner(), new DataFetchCallback<User>() {
            @Override
            public void onSuccess(List<User> data) {
                holder.username.setText(data.get(0).getName());
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("VidepPagerAdapter", "fail to get username: " + e.getMessage());
            }
        });
        holder.description.setText(video.getTitle());

        // Set Touch Listener for Pause/Resume
        holder.videoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Toggle play/pause
                if (holder.videoView.isPlaying()) {
                    holder.videoView.pause();
                } else {
                    holder.videoView.start();
                }

                // Call performClick for accessibility
                v.performClick();
            }
            return true; // Indicate the touch event is consumed
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        TextView username, description;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            username = itemView.findViewById(R.id.username);
            description = itemView.findViewById(R.id.description);
        }
    }
}
