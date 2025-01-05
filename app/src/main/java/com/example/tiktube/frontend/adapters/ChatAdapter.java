package com.example.tiktube.frontend.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;

import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Map<String, String>> messages;
    private String currentUserId; // To differentiate between sender and receiver

    public ChatAdapter(List<Map<String, String>> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Map<String, String> messageMap = messages.get(position);

        for (Map.Entry<String, String> entry : messageMap.entrySet()) {
            String userId = entry.getKey(); // User ID (sender or receiver)
            String content = entry.getValue(); // Message content

            // Set message content
            holder.messageContent.setText(content);

            // Align the message bubble based on whether the current user is the sender
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageContent.getLayoutParams();
            if (currentUserId.equals(userId)) {
                params.gravity = Gravity.END; // Align to the right for sender
                holder.messageContent.setBackgroundResource(R.drawable.bubble_sender);
            } else {
                params.gravity = Gravity.START; // Align to the left for receiver
                holder.messageContent.setBackgroundResource(R.drawable.bubble_receiver);
            }
            holder.messageContent.setLayoutParams(params);

            // Optional timestamp
            holder.messageTimestamp.setText("Just now"); // Replace with actual timestamp logic
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, messageTimestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.messageContent);
            messageTimestamp = itemView.findViewById(R.id.messageTimestamp);
        }
    }
}
