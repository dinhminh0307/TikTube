package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.frontend.adapters.VideoGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements VideoGridAdapter.OnVideoClickListener {
    TextView nameID;
    ImageView menuIcon;
    private VideoGridAdapter videoGridAdapter;
    private RecyclerView videoRecyclerView;
    private List<Video> videoList;
    private UserController userController;
    private LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        setUpComponent();
        setUpRecyclerView();
        fetchUserVideo();
    }

    private void setUpComponent() {
        menuIcon = findViewById(R.id.menuIcon);
        nameID = findViewById(R.id.username);
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        loginController = new LoginController();
        userController = new UserController();
    }

    private void setUpRecyclerView() {
        videoList = new ArrayList<>();
        videoGridAdapter = new VideoGridAdapter(this, videoList, this); // Pass the listener
        videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns in the grid
        videoRecyclerView.setAdapter(videoGridAdapter);
    }

    private void fetchUserVideo() {
        userController.getAllVideo(new DataFetchCallback<Video>() {
            @Override
            public void onSuccess(List<Video> data) {
                videoList.clear();
                for (Video video : data) {
                    if (video.getOwner().equals(loginController.getUserUID())) {
                        videoList.add(video);
                    }
                }
                Log.d("ProfileActivity", "Number of videos: " + videoList.size());
                videoGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onVideoClick(Video video) {
        // Handle video click here
//        Intent intent = new Intent(ProfileActivity.this, VideoDetailsActivity.class);
//        intent.putExtra("video", video); // Pass the video object to the new activity
//        startActivity(intent);
    }
}
