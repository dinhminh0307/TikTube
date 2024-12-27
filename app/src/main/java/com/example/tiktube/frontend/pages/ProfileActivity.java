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
        nameID.setText(user.getName());

        // set up edit button to check the current user
        checkCurrentUser();

        setUpUserStat();

        onMenuIconClicked();
    }

    @SuppressLint("SetTextI18n")
    private void setUpUserStat() {
        Log.d("Profile Activity", "Followin: " + Integer.toString(user.getFollowingList().size()));
        followingNumber.setText(Integer.toString(user.getFollowingList().size()));
        followerNumber.setText(Integer.toString(user.getFollowerList().size()));
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
                    if (video.getOwner().equals(user.getUid())) {
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
                setUpUserStat();
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
                        break;
                    case OTHER:
                        editProfileBtn.setText("Follow");
                        isCurrentUser = Enums.UserType.OTHER;
                        break;
                    case FOLLOWER:
                        editProfileBtn.setText("Unfollow");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void onEditButtonClicked() {
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCurrentUser == Enums.UserType.OTHER) {
                    userController.userFollowingAction(user, new DataFetchCallback<Void>() {
                        @Override
                        public void onSuccess(List<Void> data) {
                            checkCurrentUser();
                            reloadTheTargetUser();
                            Toast.makeText(ProfileActivity.this, "Follow Successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ProfileActivity.this, "Follow Fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
