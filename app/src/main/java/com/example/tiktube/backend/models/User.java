package com.example.tiktube.backend.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private String name;
    private String phoneNumber;
    private String email;
    private String imageUrl;
    private List<String> ownVideo;
    private List<String> interactedVideo;

    // Default constructor (required for Firestore)
    public User() {}

    // Constructor
    public User(String name, String phoneNumber, String email, String imageUrl, List<String> ownVideo, List<String> interactedVideo) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.imageUrl = imageUrl;
        this.ownVideo = ownVideo;
        this.interactedVideo = interactedVideo;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getOwnVideo() {
        return ownVideo;
    }

    public void setOwnVideo(List<String> ownVideo) {
        this.ownVideo = ownVideo;
    }

    public List<String> getInteractedVideo() {
        return interactedVideo;
    }

    public void setInteractedVideo(List<String> interactedVideo) {
        this.interactedVideo = interactedVideo;
    }

    public void setUser(User currentUser) {
        this.email = currentUser.getEmail();
        this.phoneNumber = currentUser.getPhoneNumber();
        this.name = currentUser.getName();
        this.imageUrl = currentUser.getImageUrl();
        this.ownVideo = currentUser.getOwnVideo();
        this.interactedVideo = currentUser.getInteractedVideo();
    }

    // Parcelable implementation
    protected User(Parcel in) {
        name = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        imageUrl = in.readString();
        ownVideo = in.createStringArrayList();
        interactedVideo = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(imageUrl);
        dest.writeStringList(ownVideo);
        dest.writeStringList(interactedVideo);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
