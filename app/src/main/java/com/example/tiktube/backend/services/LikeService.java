package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.firebase.FirebaseHelper;
import com.example.tiktube.backend.models.LikeVideo;
import com.example.tiktube.backend.utils.UidGenerator;

public class LikeService {
    private FirebaseHelper firebaseHelper;

    private String like_collection = "likes_video";

    public LikeService() {
        firebaseHelper = new FirebaseHelper();
    }

    public void createLike(LikeVideo likeVideo, DataFetchCallback<String> cb) {

        firebaseHelper.create(like_collection, likeVideo.getUid(), likeVideo, cb);
    }
}
