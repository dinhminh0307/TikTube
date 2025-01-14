package com.example.tiktube.frontend.adapters;

import android.annotation.SuppressLint;
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
import com.example.tiktube.backend.callbacks.CacheCallback;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.NotificationController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.example.tiktube.backend.models.LikeVideo;
import com.example.tiktube.backend.models.Notification;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.utils.UidGenerator;
import com.example.tiktube.frontend.dialogs.CommentsDialogFragment;
import com.example.tiktube.frontend.pages.ProfileActivity;
import com.example.tiktube.frontend.pages.VideoPageActivity;

import java.util.List;

public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder> {

    private List<Video> videos;
    private Context context;

    private UserController userController;

    private LoginController loginController;

    private NotificationController notificationController;

    private final Map<Integer, VideoView> preloadedVideoViews = new HashMap<>();
    public VideoPagerAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
        userController = new UserController();
        notificationController = new NotificationController();
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
        cacheVideoAsync(video, new CacheCallback() {
            @Override
            public void onCacheComplete(File cachedFile) {
                holder.videoView.setVideoPath(cachedFile.getAbsolutePath());
            }

            @Override
            public void onCacheError(Exception e) {
                holder.videoView.setVideoURI(Uri.parse(video.getVideoURL()));
            }
        });


        // Start Video Playback
        holder.videoView.setOnPreparedListener(mp -> mp.start());
        holder.videoView.setOnCompletionListener(mp -> mp.start()); // Loop video

        preloadAdjacentVideos(position);

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

        onVideoStatDisplay(holder, video);
    }

    private void preloadAdjacentVideos(int currentPosition) {
        int nextPosition = currentPosition + 1;
        int prevPosition = currentPosition - 1;

        if (nextPosition < videos.size()) {
            preloadVideo(videos.get(nextPosition));
        }
        if (prevPosition >= 0) {
            preloadVideo(videos.get(prevPosition));
        }
    }

    private void preloadVideo(Video video) {
        new Thread(() -> {
            try {
                URL url = new URL(video.getVideoURL());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000); // Timeout for quick retries
                connection.connect();
                connection.getInputStream().close(); // Preload video data
            } catch (Exception e) {
                Log.e("VideoPagerAdapter", "Error preloading video", e);
            }
        }).start();
    }




    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        for (VideoView videoView : preloadedVideoViews.values()) {
            videoView.stopPlayback();
        }
        preloadedVideoViews.clear();
    }



    // cache videos temporarily and reduce redundant network usage
    private void cacheVideoAsync(Video video, CacheCallback callback) {
        new Thread(() -> {
            try {
                File cacheDir = context.getCacheDir();
                File cacheFile = new File(cacheDir, "video_" + video.getUid() + ".mp4");
                if (cacheFile.exists()) {
                    callback.onCacheComplete(cacheFile); // Use existing cached file
                    return;
                }

                URL url = new URL(video.getVideoURL());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(cacheFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                callback.onCacheComplete(cacheFile); // Notify completion
            } catch (Exception e) {
                Log.e("VideoPagerAdapter", "Error caching video", e);
                callback.onCacheError(e);
            }
        }).start();
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
        holder.likeVideo.setOnClickListener(v -> {
            LikeVideo likeVideo = new LikeVideo(
                    loginController.getUserUID(),
                    video.getUid(),
                    "Just Now"
            );

            userController.userLikeVideo(video, likeVideo, new DataFetchCallback<String>() {
                @Override
                public void onSuccess(List<String> data) {
                    video.getLikes().add(data.get(0)); // Update likes in the video object
                    // add to notification colelction
                    userController.getUserById(loginController.getUserUID(), new DataFetchCallback<User>() {
                        @Override
                        public void onSuccess(List<User> data) {
                            Notification notification = new Notification(video.getOwner(), data.get(0).getName() + " Like your video", "Just Now");
                            notification.setUid(UidGenerator.generateUID());

                            notificationController.addNotification(notification)
                                    .thenRun(() -> {
                                        // This will execute when the CompletableFuture is successfully completed
                                        Log.d("Video Adapter", "Notification successfully added.");
                                    })
                                    .exceptionally(e -> {
                                        // This will handle any exceptions
                                        Log.e("Video Adapter", "Error adding notification", e);
                                        return null;
                                    });

                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });

                    notifyItemChanged(holder.getAdapterPosition()); // Refresh UI for this item
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("VideoPagerAdapter", "Failed to like video: " + e.getMessage());
                }
            });
        });
    }


    @SuppressLint("SetTextI18n")
    private void onVideoStatDisplay(VideoViewHolder holder, Video video) {
        holder.displayLikeVideo.setText(Integer.toString(video.getLikes().size()));
        holder.displayComment.setText(Integer.toString(video.getInteractions().size()));
    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        public VideoView videoView;
        TextView username, description, displayLikeVideo, displayComment;

        ImageView comment, likeVideo;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            username = itemView.findViewById(R.id.username);
            description = itemView.findViewById(R.id.description);
            comment = itemView.findViewById(R.id.comment);
            likeVideo = itemView.findViewById(R.id.likeVideo);
            displayComment = itemView.findViewById(R.id.displayComment);
            displayLikeVideo = itemView.findViewById(R.id.displayLikeVideo);
        }
    }
}
