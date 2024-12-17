package com.example.tiktube.backend.register;

import android.content.Context;

import com.example.tiktube.backend.callbacks.SignUpCallback;

public class RegisterController {
    RegisterService registerService;

    public RegisterController(Context context)  {
        registerService = new RegisterService(context);
    }

    public void register(String email, String password, String name, String phoneNumber, SignUpCallback cb) throws Exception{
        registerService.register(email, password, name, phoneNumber, cb);
    }
}
