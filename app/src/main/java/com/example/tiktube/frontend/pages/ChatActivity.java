package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.MessageController;
import com.example.tiktube.backend.models.Message;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private LoginController loginController;
    private MessageController messageController;

    private List<Message> userMessageList = new ArrayList<>();
    private String userId = "";

    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        loginController = new LoginController();
        messageController = new MessageController();

        recyclerView = findViewById(R.id.messagesRecyclerView);


        fetchCurrentUser();
    }

    private void fetchCurrentUser() {
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                userId = user.getUid();
                recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                adapter = new MessageAdapter(ChatActivity.this, userMessageList, userId);
                recyclerView.setAdapter(adapter);
                fetchUserMessage();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Chat Activity", "Error fetching user data: " + e);
            }
        });
    }

    private void fetchUserMessage() {
        messageController.getAllMessages()
                .thenAccept(msgs -> {
                    for (Message mes : msgs) {
                        if (mes.getReceiverId().equals(userId) || mes.getSenderId().equals(userId)) {
                            userMessageList.add(mes);
                        }
                    }
                    // Update the RecyclerView after messages are fetched
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(userMessageList.size() - 1);
                    });
                })
                .exceptionally(e -> {
                    Log.e("Chat Activity", "Error fetching messages", e);
                    return null;
                });
    }
}
