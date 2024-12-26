package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.services.UserService;

public class UserController {
    UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public void uploadVideo(Video vid) {
        userService.uploadVideo(vid);
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
}
