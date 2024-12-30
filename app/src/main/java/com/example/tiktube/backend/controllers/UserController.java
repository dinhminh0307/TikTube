package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.callbacks.CheckUserCallback;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.models.LikeVideo;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.services.UserService;

public class UserController {
    UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public void uploadVideo(Video vid, DataFetchCallback<Void> cb) {
        userService.uploadVideo(vid, cb);
    }

    public void getAllVideo(DataFetchCallback<Video> cb) {
        userService.getAllVideo(cb);
    }

    public void getUserById(String id, DataFetchCallback<User> cb) {
        userService.getUserById(id,  cb);
    }

    public void userInteraction(Interaction interaction, Video video, String customUID, DataFetchCallback<String> cb) {
        userService.userInteraction(interaction, video, customUID, cb);
    }

    public void userLikeVideo(Video video, LikeVideo likeVideo, DataFetchCallback<String> cb) {
        userService.userLikeVideo(video, likeVideo, cb);
    }

    public void userFollowingAction(User followingUser, DataFetchCallback<Void> cb) {
        userService.userFollowingAction(followingUser, cb);
    }

    public void checkCurrentUser(User target, CheckUserCallback cb) {
        userService.checkCurrentUser(target, cb);
    }

    public void userUnfollowAction(User target, DataFetchCallback<Void> cb) {
        userService.userUnfollowAction(target, cb);
    }
}
