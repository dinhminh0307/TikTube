package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.example.tiktube.backend.utils.UidGenerator;
import com.example.tiktube.frontend.adapters.ChatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagePageActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageView sendMessageButton;
    private ChatAdapter chatAdapter;

    private List<Map<String, String>> messageContent = new ArrayList<>();

    private LoginController loginController;

    private MessageController messageController;
    private String currentUserId = "";
    private String receiverUserId = "";

    private Message currentMessageContents = new Message();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message_page);

        initComponent();

        fetchCurrentUserUid(); // perform async actions

    }



    private void fetchCurrentUserUid() {
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserId = user.getUid();
                fetchCurrentMessageContents();
                onSendButtonClicked();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void fetchCurrentMessageContents() {
        Log.d("Message Activity", "Current User Id: " + currentUserId);
        Log.d("Message Activity", "Current receiver Id: " + receiverUserId);
        messageController.getAllMessages()
                .thenAccept(messages -> {
                    // Filter messages where both the current user and receiver are involved
                    for (Message message : messages) {
                        if ((message.getSenderId().equals(currentUserId) && message.getReceiverId().equals(receiverUserId)) ||
                                (message.getSenderId().equals(receiverUserId) && message.getReceiverId().equals(currentUserId))) {
                            currentMessageContents.setMessage(message);
                        }
                    }

                    // Populate the UI with messages
                    messageContent.addAll(currentMessageContents.getMessageContent());

                    runOnUiThread(() -> {
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageContent.size() - 1);
                    });
                })
                .exceptionally(e -> {
                    Log.e("Error", "Failed to fetch messages", e);
                    return null;
                });
    }



    private void onSendButtonClicked() {
        sendMessageButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // Add the new message to the messageContent list
                Map<String, String> newMessage = new HashMap<>();
                if(messageContent.isEmpty()) {
                    newMessage.put(currentUserId, messageText);
                    messageContent.add(newMessage);
                    Message currentMessage = new Message(
                            UidGenerator.generateUID(),
                            currentUserId,
                            receiverUserId,
                            messageContent
                    );
                    currentMessageContents.setMessage(currentMessage);

                    messageController.addMessage(currentMessageContents)
                            .thenRun(() -> {
                                Log.d("MessageProile", "Run Successfully");
                            })
                            .exceptionally(e -> {
                                Log.e("Error", "Error: " + e);
                                return null;
                            });
                } else {
                    newMessage.put(currentUserId, messageText);
                    messageContent.add(newMessage);

                    // update to the firebase
                    currentMessageContents.setMessageContent(messageContent);
                    messageController.updateMessageContent(currentMessageContents);
                }


                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messageContent.size() - 1);

                // Clear the input field
                messageInput.setText("");
            }
        });
    }

    private void initComponent() {
        loginController = new LoginController();
        messageController = new MessageController();

        // Initialize UI components
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        messageContent = new ArrayList<>();
        // get intent
        Intent intent = getIntent();
        receiverUserId = intent.getStringExtra("userID");

        populateSampleMessages();

        // Initialize adapter
        chatAdapter = new ChatAdapter(messageContent, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void populateSampleMessages() {
        // Add individual messages to the conversation
//        Map<String, String> message1 = new HashMap<>();
//        message1.put(receiverUserId, "Hi! Thanks for reaching out.");
//        messageContent.add(message1);
//
//        Map<String, String> message2 = new HashMap<>();
//        message2.put(currentUserId, "I'm thinking about getting a large pepperoni pizza.");
//        messageContent.add(message2);
//
//        Map<String, String> message3 = new HashMap<>();
//        message3.put(currentUserId, "How long will it take for delivery?");
//        messageContent.add(message3);
//
//        Map<String, String> message4 = new HashMap<>();
//        message4.put(receiverUserId, "We can have that ready for you in about 30-40 minutes.");
//        messageContent.add(message4);

    }
}
