package com.example.tiktube.api;

import com.example.tiktube.backend.login.LoginController;
import com.google.firebase.auth.FirebaseUser;

public class AuthAPI {
    LoginController loginController;

    public AuthAPI() {
        this.loginController = new LoginController();
    }

    public FirebaseUser getCurrentUser() {
        return loginController.getCurrentUser();
    }
}
