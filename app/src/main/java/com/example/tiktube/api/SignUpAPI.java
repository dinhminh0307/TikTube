package com.example.tiktube.api;

import android.content.Context;

import com.example.tiktube.backend.callbacks.LoginResultCallback;
import com.example.tiktube.backend.exceptions.InvalidCredentialException;
import com.example.tiktube.backend.login.LoginController;
import com.example.tiktube.backend.register.RegisterController;

public class SignUpAPI {
    RegisterController registerController;

    LoginController loginController;

    public SignUpAPI(Context context) {
        this.loginController = new LoginController();
        this.registerController = new RegisterController(context);
    }

    public void register(String email, String password) throws Exception{
        registerController.register(email, password);
    }

    public void login(String email, String password, LoginResultCallback callback) throws  Exception {
        loginController.login(email, password, callback);
    }
}
