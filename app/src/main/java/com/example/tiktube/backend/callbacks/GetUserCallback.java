package com.example.tiktube.backend.callbacks;

import com.example.tiktube.backend.models.User;

public interface GetUserCallback {
    void onSuccess(User user);
    void onFailure(Exception e);
}

