package com.example.tiktube.backend.services;

import android.content.Context;

import com.example.tiktube.backend.callbacks.SignUpCallback;
import com.example.tiktube.backend.helpers.FirebaseHelper;

public class RegisterService {
    FirebaseHelper firebaseHelper;
    Context context;

    public RegisterService(Context context) {
        this.firebaseHelper = new FirebaseHelper();
        this.context = context;
    }

    public void register(String email, String password, String name, String phoneNumber, SignUpCallback cb) {
        firebaseHelper.SignUp(email, password, name, phoneNumber, cb);
    }

}
