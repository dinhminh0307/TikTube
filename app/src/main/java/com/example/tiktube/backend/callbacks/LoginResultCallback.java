package com.example.tiktube.backend.callbacks;

import com.google.firebase.auth.FirebaseUser;

public interface LoginResultCallback {
    void onLoginSuccess(FirebaseUser user);
    void onLoginFailure(Exception exception);
}
