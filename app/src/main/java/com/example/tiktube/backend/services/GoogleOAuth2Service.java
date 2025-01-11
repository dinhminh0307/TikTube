package com.example.tiktube.backend.services;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GoogleOAuth2Service {
    private static final String TAG = "GoogleOAuth2Service";

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseHelper firebaseHelper;

    public GoogleOAuth2Service(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id)) // Replace with your client ID from Firebase
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper();
    }

    public void startGoogleSignIn(Activity activity) {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activity.startActivityForResult(signInIntent, 100); // Request code 100
        });
    }


    public CompletableFuture<FirebaseUser> handleSignInResult(Intent data) {
        CompletableFuture<FirebaseUser> future = new CompletableFuture<>();

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        task.addOnSuccessListener(googleAccount -> {
            AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser != null) {
                            checkAndCreateUser(firebaseUser).thenAccept(user -> future.complete(firebaseUser))
                                    .exceptionally(e -> {
                                        future.completeExceptionally(e);
                                        return null;
                                    });
                        } else {
                            future.completeExceptionally(new Exception("Firebase user is null after Google sign-in."));
                        }
                    })
                    .addOnFailureListener(future::completeExceptionally);
        }).addOnFailureListener(future::completeExceptionally);

        return future;
    }

    private CompletableFuture<User> checkAndCreateUser(FirebaseUser firebaseUser) {
        CompletableFuture<User> future = new CompletableFuture<>();
        String email = firebaseUser.getEmail();

        if (email == null) {
            future.completeExceptionally(new Exception("Google account email is null."));
            return future;
        }

        firebaseHelper.findByEmail("users", email, User.class, new DataFetchCallback<User>() {
            @Override
            public void onSuccess(List<User> users) {
                if (!users.isEmpty()) {
                    // User exists in Firestore
                    Log.d(TAG, "User already exists in Firestore: " + email);
                    future.complete(users.get(0));
                } else {
                    // Create new user in Firestore
                    User newUser = new User();
                    newUser.setUid(firebaseUser.getUid());
                    newUser.setEmail(email);
                    newUser.setName(firebaseUser.getDisplayName());
                    newUser.setImageUrl(firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "");
                    firebaseHelper.create("users", firebaseUser.getUid(), newUser, new DataFetchCallback<String>() {
                        @Override
                        public void onSuccess(List<String> data) {
                            Log.d(TAG, "New user created in Firestore: " + email);
                            future.complete(newUser);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "Failed to create new user in Firestore", e);
                            future.completeExceptionally(e);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error fetching user from Firestore", e);
                future.completeExceptionally(e);
            }
        });

        return future;
    }
}
