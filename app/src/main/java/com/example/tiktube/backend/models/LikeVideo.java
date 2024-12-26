package com.example.tiktube.backend.models;

public class LikeVideo {

    private String uid;
    private String ownerUID;
    private String videoUID;
    private String timeStamp;

    // Constructor
    public LikeVideo(String ownerUID, String videoUID, String timeStamp) {
        this.ownerUID = ownerUID;
        this.videoUID = videoUID;
        this.timeStamp = timeStamp;
    }

    // Default Constructor (required for serialization)
    public LikeVideo() {
    }

    // Getters
    public String getOwnerUID() {
        return ownerUID;
    }

    public String getVideoUID() {
        return videoUID;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    // Setters
    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public void setVideoUID(String videoUID) {
        this.videoUID = videoUID;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
