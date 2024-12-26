package com.example.tiktube.backend.models;

public class Interaction {
    private String ownerUID;
    private String videoUID;
    private String comment;
    private boolean isLiked;
    private String timeStamp;

    // Constructor
    public Interaction(String ownerUID, String videoUID, String comment, boolean isLiked, String timeStamp) {
        this.ownerUID = ownerUID;
        this.videoUID = videoUID;
        this.comment = comment;
        this.isLiked = isLiked;
        this.timeStamp = timeStamp;
    }

    // Default constructor (required for serialization)
    public Interaction() {
    }

    // Getters
    public String getOwnerUID() {
        return ownerUID;
    }

    public String getVideoUID() {
        return videoUID;
    }

    public String getComment() {
        return comment;
    }

    public boolean isLiked() {
        return isLiked;
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

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
