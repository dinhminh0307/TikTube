package com.example.tiktube.backend.register;

import android.content.Context;

public class RegisterController {
    RegisterService registerService;

    public RegisterController(Context context)  {
        registerService = new RegisterService(context);
    }

    public void register(String email, String password) throws Exception{
        registerService.register(email, password);
    }
}
