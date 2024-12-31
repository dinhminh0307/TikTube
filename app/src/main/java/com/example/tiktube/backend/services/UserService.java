package com.example.tiktube.backend.services;

import android.util.Log;

import com.example.tiktube.backend.callbacks.CheckUserCallback;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.firebase.FirebaseHelper;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.models.LikeVideo;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.utils.Enums;
import com.example.tiktube.backend.utils.UidGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    FirebaseHelper firebaseHelper;

    LoginController loginController;

    private InteractionService interactionService;

    private LikeService likeService;

    private VideoService videoService;

    private String video_collection = "videos";

    private String users_collection = "users";

    private String interaction_collections = "interactions";
    public UserService() {
        firebaseHelper = new FirebaseHelper();
        loginController = new LoginController();
        interactionService = new InteractionService();
        videoService = new VideoService();
        likeService = new LikeService();
    }


    public void uploadVideo(Video vid ,DataFetchCallback<Void> cb) {
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
                        cb.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        cb.onFailure(e);
                        Log.e("User Service", "Error fetching current user: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                cb.onFailure(e);
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




    public void userLikeVideo(Video video, LikeVideo likeVideo, DataFetchCallback<String> cb) {
        likeVideo.setUid(UidGenerator.generateUID());
        likeService.createLike(likeVideo, new DataFetchCallback<String>() {
            @Override
            public void onSuccess(List<String> data) {
                // add like video in the users data
                getUserById(loginController.getUserUID(), new DataFetchCallback<User>() {
                    @Override
                    public void onSuccess(List<User> data) {
                        List<String> currentUserLikes = new ArrayList<>(data.get(0).getLikesVideo());

                        currentUserLikes.add(likeVideo.getUid());

                        firebaseHelper.updateField(loginController.getUserUID(),
                                users_collection,
                                "likesVideo",
                                currentUserLikes
                                );

                        // add likes in video
                        List<String> currentVideoLike = new ArrayList<>(video.getLikes());
                        currentVideoLike.add(likeVideo.getUid());
                        videoService.updateVideoLikesFields(video.getUid(), currentVideoLike);
                        cb.onSuccess(Collections.singletonList(likeVideo.getUid()));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        cb.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void checkCurrentUser(User target, CheckUserCallback cb) {
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {

                // Ensure `user`, `target`, and their lists are not null
                if (user == null || target == null || user.getFollowingList() == null || target.getUid() == null) {
                    cb.onFailure(new NullPointerException("User, target, or their lists are null"));
                    return;
                }

                if (user.getFollowingList().contains(target.getUid())) {
                    cb.onSuccess(Enums.UserType.FOLLOWER);
                } else if (!user.getFollowingList().contains(target.getUid()) && !user.getUid().equals(target.getUid())) {
                    Log.d("User Service", "current user uid: " + user.getUid() + " target uid: " + target.getUid());
                    cb.onSuccess(Enums.UserType.OTHER);
                } else if (!user.getFollowingList().contains(target.getUid()) && user.getUid().equals(target.getUid())) {
                    cb.onSuccess(Enums.UserType.CURRENT_USER);
                } else {
                    cb.onFailure(new IllegalStateException("Unhandled user type"));
                }


            }

            @Override
            public void onFailure(Exception e) {
                cb.onFailure(e);
            }
        });
    }

    public void userFollowingAction(User followingUser, DataFetchCallback<Void> cb) {
        List<String> followingUserfollower = new ArrayList<>();
        followingUserfollower.addAll(followingUser.getFollowerList());
        followingUserfollower.add(loginController.getUserUID());

        List<String> currentUserFollowing = new ArrayList<>();
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserFollowing.addAll(user.getFollowingList());
                currentUserFollowing.add(followingUser.getUid());
                // update in the target user follower list
                firebaseHelper.updateField(followingUser.getUid(), users_collection,
                        "followerList", followingUserfollower);

                //update in the current user
                firebaseHelper.updateField(loginController.getUserUID(), users_collection,
                        "followingList", currentUserFollowing);
                cb.onSuccess(null);
            }

            @Override
            public void onFailure(Exception e) {
                cb.onFailure(e);
            }
        });

    }

    public void userUnfollowAction(User target, DataFetchCallback<Void> cb) {
        List<String> targetFollower = new ArrayList<>();
        targetFollower.addAll(target.getFollowerList());
        // remove the current user in the target follower list
        targetFollower.remove(loginController.getUserUID());

        // current user following list
        List<String> currentUserFollowing = new ArrayList<>();
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserFollowing.addAll(user.getFollowingList());
                currentUserFollowing.remove(target.getUid());

                // update the current user collection firebase
                firebaseHelper.updateField(loginController.getUserUID(), users_collection,
                        "followingList", currentUserFollowing);

                // update in the target user follower list
                firebaseHelper.updateField(target.getUid(), users_collection,
                        "followerList", targetFollower);
                cb.onSuccess(null);
            }

            @Override
            public void onFailure(Exception e) {
                cb.onFailure(e);
            }
        });

    }

    public void userEditProfile(User targetUser, DataFetchCallback<Void> cb) {
        List<String> socialMedia = new ArrayList<>();
                loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                firebaseHelper.updateField(targetUser.getUid(), users_collection, "bio" , socialMedia);
                cb.onSuccess(null);
            }

            @Override
            public void onFailure(Exception e) {
                cb.onFailure(e);
            }
        });
    }
}
