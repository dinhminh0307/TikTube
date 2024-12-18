package com.example.tiktube.backend.users;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;

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
}
