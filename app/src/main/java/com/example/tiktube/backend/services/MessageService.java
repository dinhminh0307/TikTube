package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.firebase.FirebaseHelper;
import com.example.tiktube.backend.models.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageService {
    private FirebaseHelper firebaseHelper;

    private String message_collection = "messages";

    public MessageService() {
        this.firebaseHelper = new FirebaseHelper();
    }

    public CompletableFuture<Void> addMessage(Message message) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        firebaseHelper.create(
                message_collection,
                message.getUid(),
                message,
                new DataFetchCallback<String>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        future.complete(null);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        future.completeExceptionally(e);
                    }
                }
        );
        return future;
    }

    public CompletableFuture<List<Message>> getAllMessages() {
        CompletableFuture<List<Message>> future = new CompletableFuture<>();
        firebaseHelper.findAll(
                message_collection,
                Message.class,
                new DataFetchCallback<Message>() {
                    @Override
                    public void onSuccess(List<Message> data) {
                        future.complete(data);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        future.completeExceptionally(e);
                    }
                }
        );
        return future;
    }
}
