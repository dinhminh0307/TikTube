package com.example.tiktube.backend.register;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.tiktube.backend.callbacks.SignUpCallback;
import com.example.tiktube.frontend.register.RegisterActivity;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

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
