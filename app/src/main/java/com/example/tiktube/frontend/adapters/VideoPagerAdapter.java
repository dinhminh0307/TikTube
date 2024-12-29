package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.MainActivity;
import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.models.LikeVideo;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.frontend.dialogs.CommentsDialogFragment;
import com.example.tiktube.frontend.pages.ProfileActivity;
import com.example.tiktube.frontend.pages.VideoPageActivity;

import java.util.List;

public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder> {

    private List<Video> videos;
    private Context context;

    private UserController userController;

    private LoginController loginController;
    public VideoPagerAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
        userController = new UserController();
        loginController = new LoginController();
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
                Log.d("VideoPagerAdapter", "Failed to get username: " + e.getMessage());
            }
        });
        holder.description.setText(video.getTitle());

        // Set Touch Listener for Pause/Resume
        holder.videoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (holder.videoView.isPlaying()) {
                    holder.videoView.pause();
                } else {
                    holder.videoView.start();
                }
                v.performClick(); // For accessibility
            }
            return true;
        });

        // Open CommentsDialogFragment on comment icon click
        holder.comment.setOnClickListener(v -> {
            CommentsDialogFragment dialogFragment = CommentsDialogFragment.newInstance(video);
            dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "CommentsDialog");
        });

        onVideoLikeButtonClicked(holder, video);
        onUserNameClicked(holder, video);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.videoView.pause();
    }


    private void onUserNameClicked(VideoViewHolder holder, Video video) {
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userController.getUserById(video.getOwner(), new DataFetchCallback<User>() {
                    @Override
                    public void onSuccess(List<User> data) {
                        User vidOnwer = data.get(0);
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra("user", vidOnwer);
                        // Ensure the context is an activity context
                        if (context instanceof AppCompatActivity) {
                            context.startActivity(intent);
                        } else {
                            Log.e("VideoPagerAdapter", "Context is not an instance of AppCompatActivity");
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        });
    }

    private void onVideoLikeButtonClicked(VideoViewHolder holder, Video video) {
        holder.likeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeVideo likeVideo = new LikeVideo(loginController.getUserUID(), video.getUid(), "Just Now");
                userController.userLikeVideo(video, likeVideo);
            }
        });
    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        TextView username, description;

        ImageView comment, likeVideo;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            username = itemView.findViewById(R.id.username);
            description = itemView.findViewById(R.id.description);
            comment = itemView.findViewById(R.id.comment);
            likeVideo = itemView.findViewById(R.id.likeVideo);
        }
    }
}
