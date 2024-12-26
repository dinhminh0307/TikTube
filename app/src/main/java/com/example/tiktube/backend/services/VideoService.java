package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.firebase.FirebaseHelper;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.models.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoService {
    private String video_collection = "videos";

    private FirebaseHelper firebaseHelper;

    private InteractionService interactionService;

    public VideoService() {
        this.interactionService = new InteractionService();
        this.firebaseHelper = new FirebaseHelper();
    }

    public void videoAddInteraction(Video video) {
        interactionService.getAllInteractionsByVideoUID(video.getUid(), new DataFetchCallback<Interaction>() {
            @Override
            public void onSuccess(List<Interaction> data) {
                List<String> videoUID = new ArrayList<>();
                for(Interaction tmp: data) {
                    videoUID.add(tmp.getUid());
                }
                firebaseHelper.updateField(video.getUid(), video_collection, "interactions", videoUID);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
