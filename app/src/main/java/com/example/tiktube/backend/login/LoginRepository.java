package com.example.tiktube.backend.login;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.callbacks.LoginCallback;
import com.example.tiktube.backend.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class LoginRepository {
    private static final String TAG = "LoginRepository";
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private static String userId;

    public LoginRepository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void login(String email, String password, LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.onSuccess(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public String getUserId() {
        return this.userId;
    }

    public User getCurrentUser(GetUserCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                Log.d(TAG, "User retrieved: " + user.getName());
                                callback.onSuccess(user);
                            } else {
                                callback.onFailure(new Exception("Failed to parse User object."));
                            }
                        } else {
                            callback.onFailure(new Exception("User document does not exist."));
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error retrieving user document", e);
                        callback.onFailure(e);
                    });
        } else {
            callback.onFailure(new Exception("No authenticated user found."));
        }
        return new User();
    }

}
