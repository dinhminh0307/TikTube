package com.example.tiktube.backend.services;

import android.util.Log;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.firebase.FirebaseHelper;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.utils.UidGenerator;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    FirebaseHelper firebaseHelper;

    LoginController loginController;

    private InteractionService interactionService;

    private VideoService videoService;

    private String video_collection = "videos";

    private String users_collection = "users";
    public UserService() {
        firebaseHelper = new FirebaseHelper();
        loginController = new LoginController();
        interactionService = new InteractionService();
        videoService = new VideoService();
    }


    public void uploadVideo(Video vid) {
        firebaseHelper.create(video_collection, vid.getUid(), vid, new DataFetchCallback<String>() {
            @Override
            public void onSuccess(List<String> data) {
                String vidUID = data.get(0);
                Log.d("User Service", "UID Video: " + vidUID);

                // Update the user's ownVideo field
                loginController.getCurrentUser(new GetUserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        List<String> updateOwnVideo = user.getOwnVideo();
                        if (updateOwnVideo == null) {
                            updateOwnVideo = new ArrayList<>();
                        }

                        // Add the new video UID to the user's ownVideo list
                        updateOwnVideo.add(vidUID);
                        Log.d("User Service", "user uid " + loginController.getUserUID());
                        // Update the user's document in the users_collection
                        firebaseHelper.updateField(
                                loginController.getUserUID(), // Get the current user's document ID
                                users_collection, // Collection is "users"
                                "ownVideo", // Field to update
                                updateOwnVideo // Updated list of video UIDs
                        );

                        for (String tmp : updateOwnVideo) {
                            Log.d("User Service", "Updated Video: " + tmp);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("User Service", "Error fetching current user: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("User Service", "Error creating video: " + e.getMessage());
            }
        });
    }



    public void getAllVideo(DataFetchCallback<Video> cb) {
        firebaseHelper.findAll(video_collection, Video.class, cb);
    }

    public void getUserById(String id, DataFetchCallback<User> cb) {
        firebaseHelper.findByID(id, users_collection, User.class, cb);
    }

    public void userInteraction(Interaction interaction, Video video, String customUID, DataFetchCallback<String> cb) {
        // Add interaction to Firestore
        interactionService.addInteraction(interaction, customUID, new DataFetchCallback<String>() {
            @Override
            public void onSuccess(List<String> interactionUID) {
                // Update the video's interaction list
                videoService.videoAddInteraction(video);

                // Add the video to the user's interactedVideo list
                loginController.getCurrentUser(new GetUserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        List<String> interactedVideos = user.getInteractedVideo();
                        if (interactedVideos == null) {
                            interactedVideos = new ArrayList<>();
                        }

                        // Ensure the video UID is added only once
                        if (!interactedVideos.contains(video.getUid())) {
                            interactedVideos.add(video.getUid());
                        }

                        // Update the user's interactedVideo field in Firestore
                        firebaseHelper.updateField(
                                loginController.getUserUID(),
                                users_collection,
                                "interactedVideo",
                                interactedVideos
                        );
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("User Service", "Error fetching current user: " + e.getMessage());
                    }
                });

                // Forward success callback
                cb.onSuccess(interactionUID);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("User Service", "Error adding interaction: " + e.getMessage());
                cb.onFailure(e);
            }
        });
    }



}
