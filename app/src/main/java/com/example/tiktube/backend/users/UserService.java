package com.example.tiktube.backend.users;

import android.util.Log;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.firebase.FirebaseHelper;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserService {
    FirebaseHelper firebaseHelper;

    private String video_collection = "videos";

    private String users_collection = "users";
    public UserService() {
        firebaseHelper = new FirebaseHelper();
    }

    public void uploadVideo(Video vid) {
        firebaseHelper.create(video_collection, vid);
    }

    public void getAllVideo(DataFetchCallback<Video> cb) {
        firebaseHelper.findAll(video_collection, Video.class, cb);
    }

    public void getUserById(String id, DataFetchCallback<User> cb) {
        firebaseHelper.findByID(id, users_collection, User.class, cb);
    }
}
