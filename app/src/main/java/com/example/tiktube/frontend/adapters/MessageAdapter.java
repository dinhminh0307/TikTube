package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.models.Message;
import com.example.tiktube.backend.models.User;

import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Context context;

    private UserController userController;

    private LoginController loginController;

    private String currentUserId = "";

    public MessageAdapter(Context context, List<Message> messages, String currentUserId) {
        this.context = context;
        this.messages = messages;
        this.userController = new UserController();
        this.loginController = new LoginController();
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    private void getSenderName(Message message, MessageViewHolder holder) {
        if(message.getSenderId().equals(currentUserId)) {
            userController.getUserById(message.getReceiverId(), new DataFetchCallback<User>() {
                @Override
                public void onSuccess(List<User> data) {
                    holder.senderName.post(() -> holder.senderName.setText(data.get(0).getName()));
                }

                @Override
                public void onFailure(Exception e) {

                }
            });

        } else {
            userController.getUserById(message.getSenderId(), new DataFetchCallback<User>() {
                @Override
                public void onSuccess(List<User> data) {
                    holder.senderName.post(() -> holder.senderName.setText(data.get(0).getName()));
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }

    }

    private void displayTheLastMessage(Message message, MessageViewHolder holder) {
        if (message.getMessageContent() != null && !message.getMessageContent().isEmpty()) {
            // Get the last map in the list of messageContent
            Map<String, String> lastMessageMap = message.getMessageContent().get(message.getMessageContent().size() - 1);

            // Iterate through the map to find the last message
            StringBuilder lastMessageBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : lastMessageMap.entrySet()) {
                String userId = entry.getKey();
                String content = entry.getValue();

                // Fetch the user's name asynchronously
                userController.getUserById(userId, new DataFetchCallback<User>() {
                    @Override
                    public void onSuccess(List<User> data) {
                        if (data != null && !data.isEmpty()) {
                            String userName = data.get(0).getName();
                            // Append user name and message content
                            holder.lastMessage.post(() -> holder.lastMessage.setText(userName + ": " + content));
                        } else {
                            // Handle case where user data is missing
                            holder.lastMessage.post(() -> holder.lastMessage.setText("Unknown User: " + content));
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure by showing a fallback
                        holder.lastMessage.post(() -> holder.lastMessage.setText("Unknown User: " + content));
                    }
                });
            }
        } else {
            // Fallback if no messages exist
            holder.lastMessage.setText("No messages yet");
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        // Set profile image (use Glide or Picasso for loading images)
        holder.profileImage.setImageResource(R.drawable.ic_account);

        // Set sender name
        getSenderName(message, holder);

        // Retrieve and display the last message
        displayTheLastMessage(message, holder);

        // Set a static timestamp for now (replace with actual logic if timestamps are stored)
        holder.timestamp.setText("1:01 PM");

        // Handle click events
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Clicked on " + message.getSenderId(), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView senderName, lastMessage, timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            senderName = itemView.findViewById(R.id.senderName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}

