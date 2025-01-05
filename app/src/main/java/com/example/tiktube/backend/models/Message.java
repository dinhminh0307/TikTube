package com.example.tiktube.backend.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Message {
    private String uid;
    private String senderId;
    private String receiverId;
    private List<Map<String, String>> messageContent = new ArrayList<>();

    // Constructor
    public Message(String uid, String senderId, String receiverId, List<Map<String, String>> messageContent) {
        this.uid = uid;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
    }

    // Default Constructor
    public Message() {
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public List<Map<String, String>> getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(List<Map<String, String>> messageContent) {
        this.messageContent = messageContent;
    }
}
