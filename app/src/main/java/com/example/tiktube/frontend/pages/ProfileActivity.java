package com.example.tiktube.frontend.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.MainActivity;
import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.CheckUserCallback;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.utils.Enums;
import com.example.tiktube.frontend.adapters.VideoGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements VideoGridAdapter.OnVideoClickListener {
    TextView nameID, followingNumber, followerNumber;
    ImageView menuIcon;

    Button editProfileBtn;
    private VideoGridAdapter videoGridAdapter;
    private RecyclerView videoRecyclerView;
    private List<Video> videoList;
    private UserController userController;
    private LoginController loginController;

    private User user;

    private Enums.UserType isCurrentUser = Enums.UserType.CURRENT_USER;

    private List<String> displayFollowingList = new ArrayList<>();

    private List<String> getDisplayFollowerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        setUpComponent();
        setUpRecyclerView();
        fetchUserVideo();
        onEditButtonClicked();
    }

    private void setUpComponent() {
        menuIcon = findViewById(R.id.menuIcon);
        nameID = findViewById(R.id.username);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        followingNumber = findViewById(R.id.followingNumber);
        followerNumber = findViewById(R.id.followerNumber);

        loginController = new LoginController();
        userController = new UserController();

        user = getIntent().getParcelableExtra("user");
        if (user == null) {
            Log.e("ProfileActivity", "User object is null");
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        nameID.setText(user.getName());

        // set up edit button to check the current user
        checkCurrentUser();

        onMenuIconClicked();
    }


    @SuppressLint("SetTextI18n")
    private void setUpUserStat() {
        followingNumber.setText(user.getFollowingList() != null ?
                Integer.toString(user.getFollowingList().size()) : "0");
        followerNumber.setText(user.getFollowerList() != null ?
                Integer.toString(user.getFollowerList().size()) : "0");
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
                    Log.d("Video Activity", "User UID: " +user.getUid());
                    if (video.getOwner().equals(user.getUid())) {
                        Log.d("Video Activity", "Video: " +video.getOwner());
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

    private void reloadTheTargetUser() {
        userController.getUserById(user.getUid(), new DataFetchCallback<User>() {
            @Override
            public void onSuccess(List<User> data) {
                user.setUser(data.get(0));
                checkCurrentUser(); // check the current user again to reload the button
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void checkCurrentUser() {
        userController.checkCurrentUser(user, new CheckUserCallback() {
            @Override
            public void onSuccess(Enums.UserType user) {
                switch (user) {
                    case CURRENT_USER:
                        Log.d("Profile Activity", "Current User right?");
                        break;
                    case OTHER:
                        editProfileBtn.setText("Follow");
                        isCurrentUser = Enums.UserType.OTHER;
                        break;
                    case FOLLOWER:
                        editProfileBtn.setText("Unfollow");
                        isCurrentUser = Enums.UserType.FOLLOWER;
                        break;
                    default:
                        break;
                }
                setUpUserStat(); // set up user status after checking profile
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void followUser() {
        userController.userFollowingAction(user, new DataFetchCallback<Void>() {
            @Override
            public void onSuccess(List<Void> data) {
                checkCurrentUser();
                reloadTheTargetUser();
                Toast.makeText(ProfileActivity.this, "Followed Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ProfileActivity.this, "Follow Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onEditButtonClicked() {
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCurrentUser == Enums.UserType.OTHER || isCurrentUser == Enums.UserType.FOLLOWER) {
                    // Toggle Follow/Unfollow action
                    if (editProfileBtn.getText().toString().equalsIgnoreCase("Follow")) {
                        followUser();
                    } else if (editProfileBtn.getText().toString().equalsIgnoreCase("Unfollow")) {
                        unfollowUser();
                    }
                } else if (isCurrentUser == Enums.UserType.CURRENT_USER) {
                    // Handle edit profile for the current user
                    Log.d("EditButton", "Edit button clicked by the current user.");
                    // You can navigate to an Edit Profile page here
                    Toast.makeText(ProfileActivity.this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void unfollowUser() {
        userController.userUnfollowAction(user, new DataFetchCallback<Void>() {
            @Override
            public void onSuccess(List<Void> data) {
                reloadTheTargetUser();
                Toast.makeText(ProfileActivity.this, "Unfollow Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ProfileActivity.this, "Unfollow Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupMenu(View anchor) {
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.menu_profile, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.option_settings) {
                // Navigate to Settings
//        Intent settingsIntent = new Intent(ProfileActivity.this, SettingActivity.class);
//        startActivity(settingsIntent);
                return true;
            } else if (itemId == R.id.option_edit) {
//        if (isCurrentUser == Enums.UserType.CURRENT_USER) {
//            // Navigate to Edit Profile page
//            Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
//            startActivity(editIntent);
//        } else {
//            Toast.makeText(this, "You can only edit your own profile", Toast.LENGTH_SHORT).show();
//        }
                return true;
            } else if (itemId == R.id.option_logout) {
                // Perform logout
                loginController.userSignOut();
                Intent logoutIntent = new Intent(ProfileActivity.this, MainActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void onMenuIconClicked() {
        menuIcon.setOnClickListener(v -> showPopupMenu(menuIcon));

    }
}
