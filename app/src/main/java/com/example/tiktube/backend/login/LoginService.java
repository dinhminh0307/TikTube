package com.example.tiktube.backend.login;

import android.util.Log;

import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.callbacks.LoginCallback;
import com.example.tiktube.backend.callbacks.LoginResultCallback;
import com.example.tiktube.backend.exceptions.InvalidCredentialException;
import com.example.tiktube.backend.models.User;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class LoginService {
    LoginRepository loginRepository;

    public LoginService() {
        loginRepository = new LoginRepository();
    }

    public void login(String email, String password, LoginResultCallback resultCallback) throws Exception{
        loginRepository.login(email, password, new LoginCallback() {
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
        return loginRepository.getCurrentUser(callback);
    }
}
