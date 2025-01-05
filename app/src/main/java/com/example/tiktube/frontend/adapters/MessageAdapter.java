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
import com.example.tiktube.backend.models.Message;

import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Context context;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        // Set profile image (use Glide or Picasso for loading images)
        holder.profileImage.setImageResource(R.drawable.ic_account);

        // Set sender name
        holder.senderName.setText(message.getSenderId()); // Display sender name

        // Retrieve and display the last message
        if (message.getMessageContent() != null && !message.getMessageContent().isEmpty()) {
            // Get the last map in the list of messageContent
            Map<String, String> lastMessageMap = message.getMessageContent().get(message.getMessageContent().size() - 1);

            // Iterate through the map to find the last message
            StringBuilder lastMessageBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : lastMessageMap.entrySet()) {
                String userId = entry.getKey();
                String content = entry.getValue();
                lastMessageBuilder.append(userId).append(": ").append(content).append("\n");
            }

            holder.lastMessage.setText(lastMessageBuilder.toString().trim()); // Set the last message text
        } else {
            holder.lastMessage.setText("No messages yet"); // Fallback if no messages exist
        }

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

