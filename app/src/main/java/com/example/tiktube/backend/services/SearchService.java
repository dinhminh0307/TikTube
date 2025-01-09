package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchService {
    private FirebaseHelper firebaseHelper;

    private final String video_collection = "videos";
    private final String users_collection = "users";

    public SearchService() {
        firebaseHelper = new FirebaseHelper();
    }

    public CompletableFuture<List<Object>> search(String keyword) {
        CompletableFuture<List<Object>> future = new CompletableFuture<>();

        // Clear previous search results
        List<Object> returnSearch = new ArrayList<>();

        // Preprocess the keyword
        String processedKeyword = keyword.toLowerCase();

        // Search for videos
        firebaseHelper.findAll(
                video_collection,
                Video.class,
                new DataFetchCallback<Video>() {
                    @Override
                    public void onSuccess(List<Video> data) {
                        for (Video vid : data) {
                            if (vid.getTitle() != null) {
                                String processedTitle = vid.getTitle().toLowerCase();
                                String[] titleWords = processedTitle.split("\\s+");
                                String concatenatedTitle = String.join("", titleWords);

                                boolean isMatched = false;
                                if (concatenatedTitle.contains(processedKeyword)) {
                                    isMatched = true;
                                } else {
                                    for (String word : titleWords) {
                                        if (word.startsWith(processedKeyword)) {
                                            isMatched = true;
                                            break;
                                        }
                                    }
                                }

                                if (isMatched) {
                                    returnSearch.add(vid);
                                }
                            }
                        }

                        // Proceed to search users
                        searchUsers(processedKeyword, returnSearch, future);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // If video search fails, proceed to search users
                        searchUsers(processedKeyword, returnSearch, future);
                    }
                }
        );

        return future;
    }

    private void searchUsers(String processedKeyword, List<Object> returnSearch, CompletableFuture<List<Object>> future) {
        firebaseHelper.findAll(
                users_collection,
                User.class,
                new DataFetchCallback<User>() {
                    @Override
                    public void onSuccess(List<User> data) {
                        for (User user : data) {
                            if (user.getName() != null) {
                                String processedName = user.getName().toLowerCase();
                                String[] nameWords = processedName.split("\\s+");
                                String concatenatedName = String.join("", nameWords);

                                boolean isMatched = false;
                                if (concatenatedName.contains(processedKeyword)) {
                                    isMatched = true;
                                } else {
                                    for (String word : nameWords) {
                                        if (word.startsWith(processedKeyword)) {
                                            isMatched = true;
                                            break;
                                        }
                                    }
                                }

                                if (isMatched) {
                                    returnSearch.add(user);
                                }
                            }
                        }
                        // Complete the future with updated results
                        future.complete(returnSearch);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // If user search fails, complete the future with current results
                        future.complete(returnSearch);
                    }
                }
        );
    }
}
