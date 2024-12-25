package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.callbacks.LoginResultCallback;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.services.LoginService;

public class LoginController {
    LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    public void login(String email, String password, LoginResultCallback callback) throws  Exception {
        loginService.login(email, password, callback);
    }

    public User getCurrentUser(GetUserCallback callback) {
        return loginService.getCurrentUser(callback);
    }

    public String getUserUID() {
        return loginService.getUserUID();
    }


    public void userSignOut() {
        this.loginService.userSignOut();
    }
}
