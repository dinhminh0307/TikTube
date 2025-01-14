package com.example.tiktube.backend.services;

import android.app.Activity;
import android.content.Intent;
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
    private static final String TAG = "LoginService";

    private FirebaseHelper firebaseHelper;
    private GoogleOAuth2Service googleOAuth2Service;

    public LoginService() {
        firebaseHelper = new FirebaseHelper();
    }

    public void initializeGoogleOAuth(Activity activity) {
        googleOAuth2Service = new GoogleOAuth2Service(activity);
    }

    public void login(String email, String password, LoginResultCallback resultCallback) throws Exception {
        firebaseHelper.login(email, password, new LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d("LoginService", "User: " + user.getEmail());
                resultCallback.onLoginSuccess(user);
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof FirebaseAuthInvalidCredentialsException) {
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
        return firebaseHelper.getUserId();
    }

    public void userSignOut() {
        this.firebaseHelper.userSignOut();
    }

    /**
     * Starts Google Sign-In process.
     */
    public void startGoogleSignIn(Activity activity) {
        if (googleOAuth2Service == null) {
            initializeGoogleOAuth(activity);
        }
        googleOAuth2Service.startGoogleSignIn(activity);
    }

    /**
     * Handles the result of Google Sign-In.
     */
    public CompletableFuture<FirebaseUser> handleGoogleSignInResult(Intent data) {
        if (googleOAuth2Service == null) {
            throw new IllegalStateException("GoogleOAuth2Service is not initialized.");
        }
        return googleOAuth2Service.handleSignInResult(data);
    }
}
