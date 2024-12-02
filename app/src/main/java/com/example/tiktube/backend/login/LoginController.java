package com.example.tiktube.backend.login;

import com.example.tiktube.backend.callbacks.LoginResultCallback;
import com.example.tiktube.backend.exceptions.InvalidCredentialException;
import com.google.firebase.auth.FirebaseUser;

public class LoginController {
    LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    public void login(String email, String password, LoginResultCallback callback) throws  Exception {
        loginService.login(email, password, callback);
    }

    public FirebaseUser getCurrentUser() {
        return loginService.getCurrentUser();
    }
}
