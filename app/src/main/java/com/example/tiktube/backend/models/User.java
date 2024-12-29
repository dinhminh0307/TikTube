package com.example.tiktube.backend.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private String uid; // UID field
    private String name;
    private String phoneNumber;
    private String email;
    private String imageUrl;
    private List<String> ownVideo;
    private List<String> interactedVideo;
    private List<String> likesVideo = new ArrayList<>(); // New likesVideo field
    private List<String> followingList = new ArrayList<>(); // New followingList field
    private List<String> followerList = new ArrayList<>(); // New followerList field

    // Default constructor (required for Firestore)
    public User() {}

    // Constructor
    public User(String uid, String name, String phoneNumber, String email, String imageUrl, List<String> ownVideo, List<String> interactedVideo) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.imageUrl = imageUrl;
        this.ownVideo = ownVideo;
        this.interactedVideo = interactedVideo;
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public List<String> getLikesVideo() {
        return likesVideo;
    }

    public void setLikesVideo(List<String> likesVideo) {
        this.likesVideo = likesVideo;
    }

    public List<String> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<String> followingList) {
        this.followingList = followingList;
    }

    public List<String> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(List<String> followerList) {
        this.followerList = followerList;
    }

    public void setUser(User currentUser) {
        this.uid = currentUser.getUid();
        this.name = currentUser.getName();
        this.phoneNumber = currentUser.getPhoneNumber();
        this.email = currentUser.getEmail();
        this.imageUrl = currentUser.getImageUrl();
        this.ownVideo = currentUser.getOwnVideo();
        this.interactedVideo = currentUser.getInteractedVideo();
        this.likesVideo = currentUser.getLikesVideo();
        this.followingList = currentUser.getFollowingList();
        this.followerList = currentUser.getFollowerList();
    }

    // Parcelable implementation
    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        imageUrl = in.readString();
        ownVideo = in.createStringArrayList();
        interactedVideo = in.createStringArrayList();
        likesVideo = in.createStringArrayList();
        followingList = in.createStringArrayList(); // Parcelable integration for followingList
        followerList = in.createStringArrayList(); // Parcelable integration for followerList
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(imageUrl);
        dest.writeStringList(ownVideo);
        dest.writeStringList(interactedVideo);
        dest.writeStringList(likesVideo);
        dest.writeStringList(followingList); // Write followingList to Parcel
        dest.writeStringList(followerList); // Write followerList to Parcel
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
