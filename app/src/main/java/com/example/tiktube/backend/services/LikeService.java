package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.LikeVideo;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LikeService {
    private FirebaseHelper firebaseHelper;

    private String like_collection = "likes_video";

    public LikeService() {
        firebaseHelper = new FirebaseHelper();
    }

    public void createLike(LikeVideo likeVideo, DataFetchCallback<String> cb) {

        firebaseHelper.create(like_collection, likeVideo.getUid(), likeVideo, cb);
    }

    public CompletableFuture<List<Video>> getUserLikeVideo(User user) {
        CompletableFuture<List<Video>> future = new CompletableFuture<>();
        List<Video> res = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        firebaseHelper.findAll(
                like_collection,
                LikeVideo.class,
                new DataFetchCallback<LikeVideo>() {
                    @Override
                    public void onSuccess(List<LikeVideo> data) {
                        for(LikeVideo vid: data) {
                            if(user.getLikesVideo().contains(vid.getUid())) {
                                tmp.add(vid.getVideoUID());
                            }
                        }

                        if(!tmp.isEmpty()) {
                            firebaseHelper.findAll(
                                    "videos",
                                    Video.class,
                                    new DataFetchCallback<Video>() {
                                        @Override
                                        public void onSuccess(List<Video> data) {
                                            for(Video vid: data) {
                                                if(tmp.contains(vid.getUid())) {
                                                    res.add(vid);
                                                }
                                            }
                                            future.complete(res);
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            future.completeExceptionally(e);
                                        }
                                    }
                            );
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        future.completeExceptionally(e);
                    }
                }
        );
        return future;
    }
}
