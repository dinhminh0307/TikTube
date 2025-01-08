package com.example.tiktube.frontend.pages;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.frontend.adapters.VideoPagerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;

import java.util.ArrayList;
import java.util.List;

public class SingleVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int REQUEST_CODE_SIGN_IN = 2;

    private Uri videoUri;
    private Drive googleDriveService;
    private GoogleSignInClient googleSignInClient;

    private UserController userController;
    private LoginController loginController;

    private ViewPager2 viewPager2;
    private VideoPagerAdapter videoPagerAdapter;

    private List<Video> videoDataList = new ArrayList<>();

    private ImageView btnBack;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_video_page);
        userController = new UserController();
        loginController = new LoginController();

        currentUser = getIntent().getParcelableExtra("user");

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Set up ViewPager2
        viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL); // Enable vertical scrolling
        // add playback control
        handlePlaybackOnScroll();

        fetchAllVideo();
    }

    private RecyclerView getRecyclerViewFromViewPager2(ViewPager2 viewPager2) {
        return (RecyclerView) viewPager2.getChildAt(0);
    }

    private void handlePlaybackOnScroll() {
        RecyclerView recyclerView = getRecyclerViewFromViewPager2(viewPager2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Get the current ViewHolder
                RecyclerView.ViewHolder currentHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (currentHolder instanceof VideoPagerAdapter.VideoViewHolder) {
                    VideoPagerAdapter.VideoViewHolder videoHolder = (VideoPagerAdapter.VideoViewHolder) currentHolder;
                    if (videoHolder.videoView != null) {
                        videoHolder.videoView.start(); // Play the current video
                    }
                }

                // Pause all other videos
                for (int i = 0; i < videoPagerAdapter.getItemCount(); i++) {
                    if (i != position) {
                        RecyclerView.ViewHolder otherHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (otherHolder instanceof VideoPagerAdapter.VideoViewHolder) {
                            VideoPagerAdapter.VideoViewHolder videoHolder = (VideoPagerAdapter.VideoViewHolder) otherHolder;
                            if (videoHolder.videoView != null && videoHolder.videoView.isPlaying()) {
                                videoHolder.videoView.pause();
                            }
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                // Optional: Additional behavior during scroll state changes
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCurrentUser();
    }

    // Fetch the current user from the database or server
    private void fetchCurrentUser() {
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.setUser(user); // Update the current user object
                Log.d("SingleVideoActivity", "Current user updated: " + currentUser.getName());
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SingleVideoActivity", "Failed to fetch current user: " + e.getMessage());
            }
        });
    }

    public void updateVideo(Video updatedVideo) {
        int position = videoDataList.indexOf(updatedVideo);
        if (position != -1) {
            videoDataList.set(position, updatedVideo);
            videoPagerAdapter.notifyItemChanged(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager2 = null;
        videoPagerAdapter = null;
        googleDriveService = null;
        Log.d("SingleVideoActivity", "Activity destroyed");
    }

    // Fetch all videos and set up adapter
    private void fetchAllVideo() {
        userController.getAllVideo(new DataFetchCallback<Video>() {
            @Override
            public void onSuccess(List<Video> data) {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        if (data != null && !data.isEmpty()) {
                            videoDataList.clear();
                            videoDataList.add(getIntent().getParcelableExtra("video"));

                            // Set up adapter
                            if (videoPagerAdapter == null) {
                                videoPagerAdapter = new VideoPagerAdapter(SingleVideoActivity.this, videoDataList);
                                viewPager2.setAdapter(videoPagerAdapter);
                            } else {
                                videoPagerAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("SingleVideoActivity", "No videos found");
                            Toast.makeText(SingleVideoActivity.this, "No videos available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        Log.d("SingleVideoActivity", "Failed to fetch videos: " + e.getMessage());
                        Toast.makeText(SingleVideoActivity.this, "Failed to load videos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
