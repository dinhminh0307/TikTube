package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.models.Message;
import com.example.tiktube.backend.services.MessageService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageController {
    private MessageService messageService;

    public MessageController() {
        this.messageService = new MessageService();
    }

    public CompletableFuture<Void> addMessage(Message message) {
        return messageService.addMessage(message);
    }

    public CompletableFuture<List<Message>> getAllMessages() {
        return  messageService.getAllMessages();
    }
}
