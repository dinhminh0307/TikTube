package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktube.R;
import com.example.tiktube.backend.helpers.ImageBuilder;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.pages.FollowerActivity;

import java.util.List;

public class OtherUserAdapter  extends RecyclerView.Adapter<OtherUserAdapter.UserViewHolder> {
    private List<User> userList;

    private Context context;

    private ImageBuilder imageBuilder;



    // Constructor to accept a list of users
    public OtherUserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        imageBuilder = new ImageBuilder(context);
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
        setButtonState(holder);

        // Handle follow button click
        holder.followButton.setOnClickListener(v -> {
            // Update following/follower list logic here
            user.getFollowerList().add(user.getUid()); // Example logic
            notifyDataSetChanged(); // Refresh the adapter
        });
    }

    private void setButtonState(UserViewHolder holder) {
        if (context instanceof FollowerActivity) {
            holder.followButton.setText("Follow back");
            holder.followButton.setEnabled(true);
            holder.followButton.setBackgroundColor(context.getResources().getColor(R.color.red)); // Assuming pink for "Follow back"
        } else {
            holder.followButton.setText("Following");
            holder.followButton.setEnabled(false); // Disable button to indicate it's already "Following"
            holder.followButton.setBackgroundColor(context.getResources().getColor(R.color.gray)); // Set background to gray
        }
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
