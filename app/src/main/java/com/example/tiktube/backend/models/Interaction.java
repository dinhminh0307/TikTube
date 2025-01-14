// Interaction class with UID field
package com.example.tiktube.backend.models;

public class Interaction {
    private String uid; // UID field
    private String ownerUID;
    private String videoUID;
    private String comment;
    private String timeStamp;

    // Constructor
    public Interaction(String uid, String ownerUID, String videoUID, String comment, String timeStamp) {
        this.uid = uid;
        this.ownerUID = ownerUID;
        this.videoUID = videoUID;
        this.comment = comment;
        this.timeStamp = timeStamp;
    }

    // Default constructor (required for serialization)
    public Interaction() {
    }

    // Getters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public String getVideoUID() {
        return videoUID;
    }

    public String getComment() {
        return comment;
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


    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}