package com.example.tiktube.api;

import android.content.Context;

import com.example.tiktube.backend.register.RegisterController;

public class SignUpAPI {
    RegisterController registerController;

    public SignUpAPI(Context context) {
        this.registerController = new RegisterController(context);
    }

    public void register(String email, String password) throws Exception{
        registerController.register(email, password);
    }
}
