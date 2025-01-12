package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.NotificationController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.helpers.ImageBuilder;
import com.example.tiktube.backend.models.Notification;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.utils.UidGenerator;
import com.example.tiktube.frontend.pages.FollowerActivity;
import com.example.tiktube.frontend.pages.ProfileActivity;
import com.example.tiktube.frontend.pages.VideoPageActivity;

import java.util.List;

public class OtherUserAdapter  extends RecyclerView.Adapter<OtherUserAdapter.UserViewHolder> {
    private List<User> userList;

    private Context context;

    private ImageBuilder imageBuilder;

    private UserController userController;

    private LoginController loginController;

    private NotificationController notificationController;

    private User currentUser = new User();

    // Constructor to accept a list of users
    public OtherUserAdapter(List<User> userList, Context context, User currentUser) {
        this.userList = userList;
        this.context = context;
        imageBuilder = new ImageBuilder(context);
        userController = new UserController();
        this.loginController = new LoginController();
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User owner) {
                currentUser.setUser(owner);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        this.notificationController = new NotificationController();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follower, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Bind user data to the views
        holder.userName.setText(user.getName());


        imageBuilder.loadImage(holder.profileImage, user);

        // Set button state based on following list
        setButtonState(holder, user);

        onFollowButtonClicked(holder, user);

        // Handle user name click to navigate to ProfileActivity
        holder.userName.setOnClickListener(v -> {
            // Clear the current stack and start VideoPageActivity
            Intent videoPageIntent = new Intent(context, VideoPageActivity.class);
            videoPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            videoPageIntent.putExtra("user", user); // Pass the clicked user as an extra
            context.startActivity(videoPageIntent);

            // Start ProfileActivity after VideoPageActivity is added to the stack
            Intent profileIntent = new Intent(context, ProfileActivity.class);
            profileIntent.putExtra("user", user); // Pass the clicked user as an extra
            context.startActivity(profileIntent);
        });
    }

    private void setButtonState(UserViewHolder holder, User user) {
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User owner) {
                // If the current user is viewing their own profile, hide the follow button
                if (owner.getUid().equals(user.getUid())) {
                    holder.followButton.setVisibility(View.INVISIBLE);
                }
                // If the user is not being followed, show "Follow"
                else if (!owner.getFollowingList().contains(user.getUid())) {
                    holder.followButton.setText("Follow");
                    holder.followButton.setEnabled(true);
                    holder.followButton.setBackgroundColor(context.getResources().getColor(R.color.red)); // Highlight follow button
                }
                // If the user is already being followed, show "Following"
                else {
                    holder.followButton.setText("Following");
                    holder.followButton.setEnabled(true);
                    holder.followButton.setBackgroundColor(context.getResources().getColor(R.color.gray)); // Neutral color for following
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Error updating button state", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onFollowButtonClicked(UserViewHolder holder, User user) {
        // Handle follow/unfollow button clicks
        holder.followButton.setOnClickListener(v -> {
            int position = holder.getBindingAdapterPosition(); // Get position
            if (position == RecyclerView.NO_POSITION) return; // Skip invalid positions

            loginController.getCurrentUser(new GetUserCallback() {
                @Override
                public void onSuccess(User owner) {
                    if (owner.getUid().equals(user.getUid())) {
                        return; // Prevent self-following
                    }

                    // Perform follow or unfollow based on the current state
                    if (!owner.getFollowingList().contains(user.getUid())) {
                        followUser(user, holder, position); // Pass holder and position for updates
                    } else {
                        unfollowUser(user, holder, position); // Pass holder and position for updates
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Action failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void followUser(User user, UserViewHolder holder, int position) {
        // Refresh current user state
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User currentUser) {
                userController.userFollowingAction(user, new DataFetchCallback<Void>() {
                    @Override
                    public void onSuccess(List<Void> data) {
                        Toast.makeText(context, "Followed Successfully", Toast.LENGTH_SHORT).show();

                        // Update button state after following
                        holder.followButton.setText("Following");
                        holder.followButton.setBackgroundColor(context.getResources().getColor(R.color.gray));
                        holder.followButton.setEnabled(true); // Keep button enabled
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Follow Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Failed to refresh user state.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void unfollowUser(User user, UserViewHolder holder, int position) {
        // Refresh current user state
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User currentUser) {
                userController.userUnfollowAction(user, new DataFetchCallback<Void>() {
                    @Override
                    public void onSuccess(List<Void> data) {
                        Toast.makeText(context, "Unfollowed Successfully", Toast.LENGTH_SHORT).show();

                        // Update button state after unfollowing
                        holder.followButton.setText("Follow");
                        holder.followButton.setBackgroundColor(context.getResources().getColor(R.color.red));
                        holder.followButton.setEnabled(true); // Keep button enabled
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Unfollow Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Failed to refresh user state.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder class
    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView userName, userId;
        Button followButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            followButton = itemView.findViewById(R.id.follow_button);
        }
    }
}
