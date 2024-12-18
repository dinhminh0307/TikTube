package com.example.tiktube.backend.register;

import android.content.Context;

import com.example.tiktube.backend.callbacks.SignUpCallback;

public class RegisterService {
    RegisterRepository registerRepository;
    Context context;

    public RegisterService(Context context) {
        this.registerRepository = new RegisterRepository();
        this.context = context;
    }

    public void register(String email, String password, String name, String phoneNumber, SignUpCallback cb) {
        registerRepository.SignUp(email, password, name, phoneNumber, cb);
    }

}
