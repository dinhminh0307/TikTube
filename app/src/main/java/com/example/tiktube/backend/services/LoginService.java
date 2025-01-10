package com.example.tiktube.backend.services;

import android.util.Log;

import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.callbacks.LoginCallback;
import com.example.tiktube.backend.callbacks.LoginResultCallback;
import com.example.tiktube.backend.exceptions.InvalidCredentialException;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.User;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public class LoginService {
    FirebaseHelper firebaseHelper;

    public LoginService() {
        firebaseHelper = new FirebaseHelper();
    }

    public void login(String email, String password, LoginResultCallback resultCallback) throws Exception{
        firebaseHelper.login(email, password, new LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d("RegisterService", "User: " + user.getEmail());
                resultCallback.onLoginSuccess(user);
            }

            @Override
            public void onFailure(Exception exception) {
                if(exception instanceof FirebaseAuthInvalidCredentialsException) {
                    resultCallback.onLoginFailure(new InvalidCredentialException("Wrong email or password"));
                } else {
                    resultCallback.onLoginFailure(new Exception("An unknown error occurred."));
                }
            }
        });
    }

    public User getCurrentUser(GetUserCallback callback) {
        return firebaseHelper.getCurrentUser(callback);
    }

    public CompletableFuture<Boolean> adminLogin(String username, String password) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        firebaseHelper.adminLogin(username, password, new LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                future.complete(true); // Admin login successful
            }

            @Override
            public void onFailure(Exception exception) {
                future.complete(false); // Admin login failed
            }
        });
        return future;
    }



    public String getUserUID() {
        return  firebaseHelper.getUserId();
    }

    public void userSignOut() {
        this.firebaseHelper.userSignOut();
    }
}
