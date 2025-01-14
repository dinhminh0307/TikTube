package com.example.tiktube.backend.services;

import android.util.Log;

import com.example.tiktube.backend.callbacks.CheckUserCallback;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.models.LikeVideo;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.utils.Enums;
import com.example.tiktube.backend.utils.UidGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserService {
    private final InteractionService interactionService;
    private final LikeService likeService;
    private final VideoService videoService;
    private final String video_collection = "videos";
    private final String users_collection = "users";
    private final String interaction_collections = "interactions";
    FirebaseHelper firebaseHelper;
    LoginController loginController;

    public UserService() {
        firebaseHelper = new FirebaseHelper();
        loginController = new LoginController();
        interactionService = new InteractionService();
        videoService = new VideoService();
        likeService = new LikeService();
    }


    public void uploadVideo(Video vid, DataFetchCallback<Void> cb) {
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
        List<String> followingUserFollowerList = new ArrayList<>(followingUser.getFollowerList());

        // Add the current user UID to the follower list only if not already present
        if (!followingUserFollowerList.contains(loginController.getUserUID())) {
            followingUserFollowerList.add(loginController.getUserUID());
        }

        List<String> currentUserFollowingList = new ArrayList<>();
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserFollowingList.addAll(user.getFollowingList());

                // Add the target user's UID to the following list only if not already present
                if (!currentUserFollowingList.contains(followingUser.getUid())) {
                    currentUserFollowingList.add(followingUser.getUid());
                }

                // Update in the target user's follower list
                firebaseHelper.updateField(followingUser.getUid(), users_collection,
                        "followerList", followingUserFollowerList);

                // Update in the current user's following list
                firebaseHelper.updateField(loginController.getUserUID(), users_collection,
                        "followingList", currentUserFollowingList);

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

    public void userEditProfile(User updatedUser, DataFetchCallback<User> callback) {
        if (updatedUser.getUid() == null || updatedUser.getUid().isEmpty()) {
            callback.onFailure(new IllegalArgumentException("User UID is null or empty"));
            return;
        }

        // Update only non-empty fields
        if (updatedUser.getName() != null && !updatedUser.getName().isEmpty()) {
            firebaseHelper.updateField(updatedUser.getUid(), "users", "name", updatedUser.getName());
        }
        if (updatedUser.getPhoneNumber() != null && !updatedUser.getPhoneNumber().isEmpty()) {
            firebaseHelper.updateField(updatedUser.getUid(), "users", "phoneNumber", updatedUser.getPhoneNumber());
        }
        if (updatedUser.getBio() != null && !updatedUser.getBio().isEmpty()) {
            firebaseHelper.updateField(updatedUser.getUid(), "users", "bio", updatedUser.getBio());
        }
        if (updatedUser.getInstagram() != null && !updatedUser.getInstagram().isEmpty()) {
            firebaseHelper.updateField(updatedUser.getUid(), "users", "instagram", updatedUser.getInstagram());
        }
        if (updatedUser.getFacebook() != null && !updatedUser.getFacebook().isEmpty()) {
            firebaseHelper.updateField(updatedUser.getUid(), "users", "facebook", updatedUser.getFacebook());
        }
        if (updatedUser.getImageUrl() != null && !updatedUser.getImageUrl().isEmpty()) {
            firebaseHelper.updateField(updatedUser.getUid(), "users", "imageUrl", updatedUser.getImageUrl());
        }

        callback.onSuccess(Collections.singletonList(updatedUser));
    }

    public void deleteUser(User user, DataFetchCallback<Void> callback) {
        if (user == null || user.getUid() == null || user.getUid().isEmpty()) {
            callback.onFailure(new IllegalArgumentException("User ID is null or empty"));
            return;
        }
        firebaseHelper.deleteUser(user.getUid(), callback);
    }

    public void getUserNamesByIds(List<String> uids, DataFetchCallback<String> callback) {
        if (uids == null || uids.isEmpty()) {
            callback.onSuccess(Collections.emptyList());
            return;
        }

        List<String> names = new ArrayList<>();
        List<String> failures = new ArrayList<>();

        for (String uid : uids) {
            firebaseHelper.findByID(uid, "users", User.class, new DataFetchCallback<User>() {
                @Override
                public void onSuccess(List<User> users) {
                    if (!users.isEmpty()) {
                        names.add(users.get(0).getName());
                    }
                    if (names.size() + failures.size() == uids.size()) {
                        callback.onSuccess(names);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    failures.add(uid);
                    if (names.size() + failures.size() == uids.size()) {
                        callback.onFailure(new Exception("Failed to fetch some names"));
                    }
                }
            });
        }
    }

    public CompletableFuture<List<Video>> getUserLikeVideo(User user) {
        return likeService.getUserLikeVideo(user);
    }

    public void getAllUsers(DataFetchCallback<User> cb) {
        firebaseHelper.findAll(users_collection, User.class, cb);
    }

    public CompletableFuture<List<User>> userGetFollowingList(User user) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        List<User> followingList = new ArrayList<>();

        // If there are no following IDs, complete the future immediately
        if (user.getFollowingList() == null || user.getFollowingList().isEmpty()) {
            future.complete(followingList);
            return future;
        }

        // Use a counter to track the number of pending asynchronous calls
        final int[] pendingRequests = {user.getFollowingList().size()};

        for (String followingId : user.getFollowingList()) {
            getUserById(
                    followingId,
                    new DataFetchCallback<User>() {
                        @Override
                        public void onSuccess(List<User> data) {
                            synchronized (followingList) {
                                followingList.add(data.get(0)); // Add user to the list
                            }

                            // Decrement pending requests
                            synchronized (pendingRequests) {
                                pendingRequests[0]--;
                                if (pendingRequests[0] == 0) {
                                    future.complete(followingList); // Complete the future when all requests are done
                                }
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            synchronized (pendingRequests) {
                                pendingRequests[0]--;
                                if (pendingRequests[0] == 0) {
                                    // If there's an error, still complete the future with the data fetched so far
                                    future.complete(followingList);
                                }
                            }
                        }
                    }
            );
        }

        return future;
    }

    public CompletableFuture<List<User>> userGetFollowerList(User user) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        List<User> followerList = new ArrayList<>();

        // If there are no follower IDs, complete the future immediately
        if (user.getFollowerList() == null || user.getFollowerList().isEmpty()) {
            future.complete(followerList);
            return future;
        }

        // Use a counter to track the number of pending asynchronous calls
        final int[] pendingRequests = {user.getFollowerList().size()};

        for (String followerId : user.getFollowerList()) {
            getUserById(
                    followerId,
                    new DataFetchCallback<User>() {
                        @Override
                        public void onSuccess(List<User> data) {
                            synchronized (followerList) {
                                followerList.add(data.get(0)); // Add user to the list
                            }

                            // Decrement pending requests
                            synchronized (pendingRequests) {
                                pendingRequests[0]--;
                                if (pendingRequests[0] == 0) {
                                    future.complete(followerList); // Complete the future when all requests are done
                                }
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            synchronized (pendingRequests) {
                                pendingRequests[0]--;
                                if (pendingRequests[0] == 0) {
                                    // If there's an error, still complete the future with the data fetched so far
                                    future.complete(followerList);
                                }
                            }
                        }
                    }
            );
        }

        return future;
    }


}
