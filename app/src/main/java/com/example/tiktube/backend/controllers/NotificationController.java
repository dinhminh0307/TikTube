package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.models.Notification;
import com.example.tiktube.backend.services.NotificationService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NotificationController {
    private NotificationService notificationService;

    public NotificationController() {
        this.notificationService = new NotificationService();
    }

    public CompletableFuture<Void> addNotification(Notification notification) {
        return notificationService.addNotification(notification);
    }

    public CompletableFuture<List<Notification>> getNotifications() {
        return notificationService.getNotifications();
    }
}
