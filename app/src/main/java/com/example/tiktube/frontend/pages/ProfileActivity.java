package com.example.tiktube.frontend.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.MainActivity;
import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.CheckUserCallback;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.NotificationController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.helpers.ImageBuilder;
import com.example.tiktube.backend.models.Notification;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.utils.Enums;
import com.example.tiktube.backend.utils.UidGenerator;
import com.example.tiktube.frontend.adapters.VideoGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements VideoGridAdapter.OnVideoClickListener {
    TextView nameID, followingNumber, followerNumber, totalLike, bioText;
    ImageView menuIcon, likeVideos, userVideos, privateVideos, bookmarkVideos, profilePicture, btnBack, homeIcon, searchIcon, uploadIcon, messagingIcon, profileIcon;

    private ImageBuilder imageBuilder;

    Button editProfileBtn, messageId;
    private VideoGridAdapter videoGridAdapter;
    private RecyclerView videoRecyclerView;
    private List<Video> videoList;
    private UserController userController;
    private LoginController loginController;

    private User user;

    private Enums.UserType isCurrentUser = Enums.UserType.CURRENT_USER;

    private NotificationController notificationController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        setUpComponent();
        setUpRecyclerView();
        fetchUserVideo();
        onEditButtonClicked();
        onMessageButtonClicked();
        onFollowerNumberClicked();
        onFollowingNumberClicked();

        onTabToggle();
    }


    private void setUpComponent() {
//        Save display mode status
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
//        Apply display mode on launch
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        menuIcon = findViewById(R.id.menuIcon);
        nameID = findViewById(R.id.username);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        followingNumber = findViewById(R.id.followingNumber);
        followerNumber = findViewById(R.id.followerNumber);
        totalLike = findViewById(R.id.totalLike);
        bioText = findViewById(R.id.bioText);
        messageId = findViewById(R.id.messageId);
        likeVideos = findViewById(R.id.likeVideos);
        userVideos = findViewById(R.id.userVideos);
        privateVideos = findViewById(R.id.privateVideos);
        bookmarkVideos = findViewById(R.id.bookmarkVideos);
        profilePicture = findViewById(R.id.profilePicture);
        btnBack = findViewById(R.id.btnBack);
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
//        uploadIcon = findViewById(R.id.uploadIcon);
        messagingIcon = findViewById(R.id.messagingIcon);
        profileIcon = findViewById(R.id.profileIcon);

        loginController = new LoginController();
        userController = new UserController();
        notificationController = new NotificationController();
        imageBuilder = new ImageBuilder(ProfileActivity.this);

        user = getIntent().getParcelableExtra("user");
        if (user == null) {
            Log.e("ProfileActivity", "User object is null");
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up edit button to check the current user
        checkCurrentUser();

        nameID.setText(user.getName());
        bioText.setText(user.getBio());

        // Load the profile picture from imageUrl
        imageProfileLoaded();


        onMenuIconClicked();
        onBackBtnClicked();
        onHomeBtnClicked();
        onSearchBtnClicked();
        onMessageBtnClicked();
    }

    private void imageProfileLoaded() {
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            imageBuilder.loadImage(profilePicture, user);
        } else {
            // Set a default image if no URL is available
            profilePicture.setImageResource(R.drawable.ic_account_circle_foreground);
        }
    }


    private void onMessageButtonClicked() {
        messageId.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MessagePageActivity.class);
            intent.putExtra("userID", user.getUid());
            startActivity(intent);
        });
    }

    private void displayTotalLikes() {
        int likeCount = 0;
        for (Video v : videoList) {
            likeCount += v.getLikes().size();
        }
        totalLike.setText(Integer.toString(likeCount));
    }

    private void onFollowerNumberClicked() {
        followerNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FollowerActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private void onFollowingNumberClicked() {
        followingNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FollowingActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private void onTabToggle() {
        userVideos.setOnClickListener(v -> {
            highlightTab(userVideos);
            fetchUserVideo(); // Fetch and display user videos
        });

        likeVideos.setOnClickListener(v -> {
            highlightTab(likeVideos);
            fetchLikedVideos(); // Fetch and display liked videos
        });

        // Default: Highlight "User Videos" and fetch them
        highlightTab(userVideos);
        fetchUserVideo();
    }



    private void highlightTab(ImageView activeTab) {
        // Deactivate all tabs
        userVideos.setAlpha(0.5F);
        likeVideos.setAlpha(0.5F);
        privateVideos.setAlpha(0.5F);
        bookmarkVideos.setAlpha(0.5F);

        // Activate the selected tab
        activeTab.setAlpha(1F);
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
                runOnUiThread(() -> {
                    videoList.clear(); // Clear the current video list
                    for (Video video : data) {
                        if (video.getOwner().equals(user.getUid())) {
                            videoList.add(video); // Add only the user's videos
                        }
                    }
                    displayTotalLikes();
                    findViewById(R.id.empty_state).setVisibility(videoList.isEmpty() ? View.VISIBLE : View.GONE);
                    videoGridAdapter.notifyDataSetChanged(); // Notify adapter of changes
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    videoList.clear(); // Clear the list on error
                    videoGridAdapter.notifyDataSetChanged(); // Notify adapter of changes
                    findViewById(R.id.empty_state).setVisibility(View.VISIBLE); // Show empty state
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user videos", Toast.LENGTH_SHORT).show();
                });
                Log.e("ProfileActivity", "Failed to fetch user videos", e);
            }
        });
    }



    private void fetchLikedVideos() {
        userController.getUserLikeVideo(user)
                .thenAccept(vids -> {
                    runOnUiThread(() -> {
                        // Clear the video list
                        videoList.clear();

                        if (vids != null && !vids.isEmpty()) {
                            videoList.addAll(vids); // Add liked videos
                            findViewById(R.id.empty_state).setVisibility(View.GONE); // Hide empty state
                            videoRecyclerView.setVisibility(View.VISIBLE); // Ensure RecyclerView is visible
                        } else {
                            // If no liked videos, show empty state and hide the RecyclerView
                            findViewById(R.id.empty_state).setVisibility(View.VISIBLE); // Show empty state
                            videoRecyclerView.setVisibility(View.GONE); // Hide RecyclerView
                        }

                        // Notify the adapter about the changes
                        videoGridAdapter.notifyDataSetChanged();
                    });
                })
                .exceptionally(e -> {
                    runOnUiThread(() -> {
                        // Handle error by clearing the list and showing the empty state
                        videoList.clear();
                        videoGridAdapter.notifyDataSetChanged();

                        findViewById(R.id.empty_state).setVisibility(View.VISIBLE); // Show empty state
                        videoRecyclerView.setVisibility(View.GONE); // Hide RecyclerView

                        Toast.makeText(ProfileActivity.this, "Failed to fetch liked videos", Toast.LENGTH_SHORT).show();
                    });
                    Log.e("ProfileActivity", "Failed to fetch liked videos", e);
                    return null;
                });
    }





    @Override
    public void onVideoClick(Video video) {
        // Handle video click here
        Intent intent = new Intent(ProfileActivity.this, SingleVideoActivity.class);
        intent.putExtra("video", video); // Pass the video object to the new activity
        intent.putExtra("user", user);
        startActivity(intent);
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

    private void fetchCurrentUserData() {
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User currentUser) {
                user.setUser(currentUser);
                nameID.setText(user.getName());
                bioText.setText(user.getBio());
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
                        btnBack.setVisibility(View.GONE);
                        fetchCurrentUserData();
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
            String targetUid = user.getUid();

            @Override
            public void onSuccess(List<Void> data) {
                loginController.getCurrentUser(new GetUserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Notification notification = new Notification(targetUid, user.getName() + " has followed you", "just now");
                        notification.setUid(UidGenerator.generateUID());
                        notificationController.addNotification(notification);
                        checkCurrentUser();
                        reloadTheTargetUser();
                        Toast.makeText(ProfileActivity.this, "Followed Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ProfileActivity.this, "Follow Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onEditButtonClicked() {
        editProfileBtn.setOnClickListener(v -> {
            if (isCurrentUser == Enums.UserType.OTHER || isCurrentUser == Enums.UserType.FOLLOWER) {
                // Toggle Follow/Unfollow action
                if (editProfileBtn.getText().toString().equalsIgnoreCase("Follow")) {
                    followUser();
                } else if (editProfileBtn.getText().toString().equalsIgnoreCase("Unfollow")) {
                    unfollowUser();
                }
            } else if (isCurrentUser == Enums.UserType.CURRENT_USER) {
                editProfilePage();
                // Handle edit profile for the current user
                Log.d("EditButton", "Edit button clicked by the current user.");
                // You can navigate to an Edit Profile page here
                Toast.makeText(ProfileActivity.this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
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
                Intent settingsIntent = new Intent(ProfileActivity.this, SettingActivity.class);
                startActivity(settingsIntent);
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

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Get updated user data
                    user = result.getData().getParcelableExtra("updatedUser");

                    // Update the UI with the new user data
                    nameID.setText(user.getName());
                    bioText.setText(user.getBio());
                    setUpUserStat();
                }
            }
    );

    private void editProfilePage() {
        Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        editIntent.putExtra("user", user); // Pass the current user to EditProfileActivity
        editProfileLauncher.launch(editIntent);
    }

    private void onBackBtnClicked() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void onHomeBtnClicked() {
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, VideoPageActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }

    private void onSearchBtnClicked() {
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    private void onMessageBtnClicked() {
        messagingIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }
//    private void onProfileBtnClicked() {
//        profileIcon.setOnClickListener(v -> {
//            btnBack.setVisibility(View.GONE);
//            fetchCurrentUserData();
//            editProfileBtn.setText("Edit profile");
//            isCurrentUser = Enums.UserType.CURRENT_USER;
//            Log.d("Profile Activity", "Current User right?");
//            setUpUserStat();
//        });
//    }
}
