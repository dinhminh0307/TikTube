package com.example.tiktube.backend.models;

public class Notification {
    private String ownerID;
    private String content;
    private String timeStamp;

    private String uid;

    // Default Constructor
    public Notification() {
    }

    // Parameterized Constructor
    public Notification(String ownerID, String content, String timeStamp) {
        this.ownerID = ownerID;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    // Getter for ownerID
    public String getOwnerID() {
        return ownerID;
    }

    // Setter for ownerID
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Setter for content
    public void setContent(String content) {
        this.content = content;
    }

    // Getter for timeStamp
    public String getTimeStamp() {
        return timeStamp;
    }

    // Setter for timeStamp
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


}
