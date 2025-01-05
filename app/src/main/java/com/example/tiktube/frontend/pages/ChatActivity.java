package com.example.tiktube.frontend.pages;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.models.Message;
import com.example.tiktube.frontend.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        RecyclerView recyclerView = findViewById(R.id.messagesRecyclerView);

        // Initialize sample messages
        List<Message> messageList = createSampleMessages();

        // Set up RecyclerView
        MessageAdapter adapter = new MessageAdapter(this, messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private List<Message> createSampleMessages() {
        List<Message> messageList = new ArrayList<>();

        List<Map<String, String>> messageContent1 = new ArrayList<>();
        Map<String, String> message1 = new HashMap<>();
        message1.put("user1", "Hi, how are you?");
        messageContent1.add(message1);
        messageList.add(new Message("1", "user1", "user2", messageContent1));

        List<Map<String, String>> messageContent2 = new ArrayList<>();
        Map<String, String> message2 = new HashMap<>();
        message2.put("user2", "I am good, thanks! How about you?");
        messageContent2.add(message2);
        messageList.add(new Message("2", "user2", "user1", messageContent2));

        return messageList;
    }
}
