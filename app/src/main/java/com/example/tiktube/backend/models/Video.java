package com.example.tiktube.backend.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Video implements Parcelable {
    private String uid; // UID field
    private String title;
    private String videoURL;
    private String owner;
    private String timeStamps;
    private List<String> viewers;
    private List<String> interactions;
    private List<String> likes = new ArrayList<>(); // New likesVideo field

    // Constructor
    public Video(String uid, String title, String videoURL, String owner, String timeStamps, List<String> viewers, List<String> interactions) {
        this.uid = uid;
        this.title = title;
        this.videoURL = videoURL;
        this.owner = owner;
        this.timeStamps = timeStamps;
        this.viewers = viewers;
        this.interactions = interactions;

    }

    // Default Constructor (required for Firebase and serialization libraries)
    public Video() {
    }

    // Parcelable Implementation
    protected Video(Parcel in) {
        uid = in.readString();
        title = in.readString();
        videoURL = in.readString();
        owner = in.readString();
        timeStamps = in.readString();
        viewers = in.createStringArrayList();
        interactions = in.createStringArrayList();
        likes = in.createStringArrayList(); // Parcelable integration for likesVideo
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(title);
        dest.writeString(videoURL);
        dest.writeString(owner);
        dest.writeString(timeStamps);
        dest.writeStringList(viewers);
        dest.writeStringList(interactions);
        dest.writeStringList(likes); // Write likesVideo to Parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }
}
