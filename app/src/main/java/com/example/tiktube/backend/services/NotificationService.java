package com.example.tiktube.backend.services;

import android.util.Log;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.firebase.FirebaseHelper;
import com.example.tiktube.backend.models.Notification;
import com.example.tiktube.backend.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NotificationService {
    FirebaseHelper firebaseHelper;

    private String notification_collection = "notifications";

    private LoginController loginController;

    public NotificationService() {
        firebaseHelper = new FirebaseHelper();
        loginController = new LoginController();
    }

    public CompletableFuture<List<Notification>> getNotifications() {
        CompletableFuture<List<Notification>> future = new CompletableFuture<>();

        firebaseHelper.findAll(notification_collection, Notification.class, new DataFetchCallback<Notification>() {
            @Override
            public void onSuccess(List<Notification> data) {
                // find the notification match with current user uid
                List<Notification> returnNotification = new ArrayList<>();
                loginController.getCurrentUser(new GetUserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        for (Notification notification : data) {
                            if(notification.getOwnerID().equals(user.getUid())) {
                                returnNotification.add(notification);
                                Log.d("Notification Activity", "Notificaiton: " + notification.getContent());
                            }
                        }
                        future.complete(returnNotification);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        future.completeExceptionally(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public CompletableFuture<Void> addNotification(Notification notification) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        firebaseHelper.create(
                notification_collection,
                notification.getUid(),
                notification,
                new DataFetchCallback<String>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        future.complete(null); // Signal success
                    }

                    @Override
                    public void onFailure(Exception e) {
                        future.completeExceptionally(e); // Signal failure
                    }
                }
        );

        return future; // Return the CompletableFuture
    }


}
