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

    private List<Object> returnSearch;

    private final String video_collection = "videos";

    private final String users_collection = "users";

    public SearchService() {
        firebaseHelper = new FirebaseHelper();
        returnSearch = new ArrayList<>();
    }

    public CompletableFuture<List<Object>> search(String keyword) {
        CompletableFuture<List<Object>> future = new CompletableFuture<>();

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
                                // Preprocess the video title
                                String processedTitle = vid.getTitle().toLowerCase();

                                // Tokenize the title into individual words
                                String[] titleWords = processedTitle.split("\\s+");

                                // Concatenate all words into a single string (remove spaces)
                                String concatenatedTitle = String.join("", titleWords);

                                boolean isMatched = false;

                                // Check if the concatenated title contains the keyword
                                if (concatenatedTitle.contains(processedKeyword)) {
                                    isMatched = true;
                                } else {
                                    // Check if any word in the title starts with the keyword
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

                        // After searching for videos, search for users
                        searchUsers(processedKeyword, future);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // If video search fails, proceed to search users
                        searchUsers(processedKeyword, future);
                    }
                }
        );

        return future;
    }

    private void searchUsers(String processedKeyword, CompletableFuture<List<Object>> future) {
        firebaseHelper.findAll(
                users_collection,
                User.class,
                new DataFetchCallback<User>() {
                    @Override
                    public void onSuccess(List<User> data) {
                        for (User user : data) {
                            if (user.getName() != null) {
                                // Preprocess the user name
                                String processedName = user.getName().toLowerCase();

                                // Tokenize the name into individual words
                                String[] nameWords = processedName.split("\\s+");

                                // Concatenate all words into a single string (remove spaces)
                                String concatenatedName = String.join("", nameWords);

                                boolean isMatched = false;

                                // Check if the concatenated name contains the keyword
                                if (concatenatedName.contains(processedKeyword)) {
                                    isMatched = true;
                                } else {
                                    // Check if any word in the name starts with the keyword
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
                        // Complete the future after adding users
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
