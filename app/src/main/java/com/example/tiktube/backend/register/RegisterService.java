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

    public void register(String email, String password) {
        registerRepository.signUp(email, password, new SignUpCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(context, "User created successfully: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                Log.d("RegisterService", "User: " + user.getEmail());
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception != null) {
                    if (exception instanceof FirebaseAuthWeakPasswordException) {
                        Toast.makeText(context, "Password too weak. Must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "Invalid email format.", Toast.LENGTH_SHORT).show();
                    } else if (exception instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(context, "This email is already registered.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Unknown error occurred.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
