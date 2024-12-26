package com.example.tiktube.frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.models.Interaction;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Interaction> commentsList;

    // Constructor
    public CommentsAdapter(List<Interaction> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate comment_item.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // Bind data to the views
        Interaction interaction = commentsList.get(position);
        holder.userName.setText(interaction.getOwnerUID()); // Assuming ownerUID is the user name or identifier
        holder.commentText.setText(interaction.getComment());
        holder.commentTime.setText(interaction.getTimeStamp());

        // Update like button state
        if (interaction.isLiked()) {
            holder.likeButton.setImageResource(R.drawable.ic_like_filled); // Replace with your filled like icon
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_like); // Replace with your default like icon
        }

        // Handle like button click
        holder.likeButton.setOnClickListener(v -> {
            boolean isLiked = !interaction.isLiked();
            interaction.setLiked(isLiked);
            notifyItemChanged(position); // Refresh the item
        });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    // ViewHolder class
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, commentText, commentTime;
        ImageView likeButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.comment_user_name);
            commentText = itemView.findViewById(R.id.comment_text);
            commentTime = itemView.findViewById(R.id.comment_time);
            likeButton = itemView.findViewById(R.id.like_button);
        }
    }
}