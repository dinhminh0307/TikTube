package com.example.tiktube.api;

import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.login.LoginController;
import com.example.tiktube.backend.models.User;
import com.google.firebase.auth.FirebaseUser;

public class AuthAPI {
    LoginController loginController;

    public AuthAPI() {
        this.loginController = new LoginController();
    }

    public User getCurrentUser(GetUserCallback callback) {
        return loginController.getCurrentUser(callback);
    }
}
