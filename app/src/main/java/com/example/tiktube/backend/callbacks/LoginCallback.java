package com.example.tiktube.backend.callbacks;

import com.google.firebase.auth.FirebaseUser;

public interface LoginCallback {
    void onSuccess(FirebaseUser user);
    void onFailure(Exception exception);
}
