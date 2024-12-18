package com.example.tiktube.backend.models;

import java.util.List;

public class Video {
    private String title;
    private String videoURL;
    private String owner;
    private String timeStamps;
    private List<String> viewers;
    private List<String> interactions;

    // Constructor
    public Video(String title, String videoURL, String owner, String timeStamps, List<String> viewers, List<String> interactions) {
        this.title = title;
        this.videoURL = videoURL;
        this.owner = owner;
        this.timeStamps = timeStamps;
        this.viewers = viewers;
        this.interactions = interactions;
    }

    // Default Constructor (optional, required for Firebase and serialization libraries)
    public Video() {
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(String timeStamps) {
        this.timeStamps = timeStamps;
    }

    public List<String> getViewers() {
        return viewers;
    }

    public void setViewers(List<String> viewers) {
        this.viewers = viewers;
    }

    public List<String> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<String> interactions) {
        this.interactions = interactions;
    }
}
