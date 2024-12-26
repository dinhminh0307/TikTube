package com.example.tiktube.backend.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.callbacks.LoginCallback;
import com.example.tiktube.backend.callbacks.SignUpCallback;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.utils.UidGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseHelper {
    private FirebaseFirestore firestore;
    private static final String TAG = "LoginRepository";

    private static String userId;

    private FirebaseAuth mAuth;

    public FirebaseHelper() {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void create(String collections, String customUID, Object obj, DataFetchCallback<String> cb) {
        // Add the data to the specified Firestore collection with a custom UID
        firestore.collection(collections)
                .document(customUID) // Use the custom UID
                .set(obj) // Set the object in the document
                .addOnSuccessListener(aVoid -> {
                    // Pass the custom UID to the callback
                    cb.onSuccess(Collections.singletonList(customUID));
                    Log.d("Firestore", "Object added with custom UID: " + customUID);
                })
                .addOnFailureListener(e -> {
                    // Notify failure with the exception
                    Log.e("Firestore", "Error adding object with custom UID", e);
                    cb.onFailure(e);
                });
    }




    public void updateField(String Id, String collections,  String fieldName, Object value) {
        firestore.collection(collections)
                .document(Id)
                .update(fieldName, value)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Field " + fieldName + " updated successfully for site: " + Id);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating field " + fieldName + " for site: " + Id, e);
                });
    }

    public <T> void findAll(String collection, Class<T> type, DataFetchCallback<T> callback) {
        firestore.collection(collection)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<T> results = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        T obj = doc.toObject(type);
                        if (obj != null) {
                            results.add(obj);
                        }
                    }
                    // Notify success with the fetched data
                    callback.onSuccess(results);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public <T> void findByID(String uid, String collection, Class<T> type, DataFetchCallback<T> callback) {
        firestore.collection(collection)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        T result = documentSnapshot.toObject(type); // Parse the document to the specified type
                        if (result != null) {
                            callback.onSuccess(Collections.singletonList(result)); // Wrap the result in a list for the callback
                        } else {
                            Log.d("Firestore", "Failed to parse document with ID: " + uid);
                            callback.onFailure(new NullPointerException("Parsed document is null"));
                        }
                    } else {
                        Log.d("Firestore", "No objecte found with ID: " + uid);
                        callback.onFailure(new IllegalArgumentException("No document found with ID: " + uid));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding donation site", e);
                    callback.onFailure(e);
                });
    }


    public <T> void findByObject(String collection, T object, DataFetchCallback<T> callback) {
        firestore.collection(collection)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<T> matchingResults = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        T docObject = doc.toObject((Class<T>) object.getClass());
                        if (docObject != null && Objects.equals(docObject, object)) {
                            matchingResults.add(docObject);
                        }
                    }

                    if (!matchingResults.isEmpty()) {
                        callback.onSuccess(matchingResults);
                    } else {
                        callback.onFailure(new IllegalArgumentException("No matching document found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public <T> void findDocumentUIDByObject(String collection, T object, DataFetchCallback<String> callback) {
        firestore.collection(collection)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        T docObject = doc.toObject((Class<T>) object.getClass());
                        if (docObject != null && Objects.equals(docObject, object)) {
                            // Return the UID (document ID) of the matching document
                            callback.onSuccess(Collections.singletonList(doc.getId()));
                            return;
                        }
                    }
                    // No matching document found
                    callback.onFailure(new IllegalArgumentException("No matching document found"));
                })
                .addOnFailureListener(callback::onFailure);
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
            firestore.collection("users").document(userId).get()
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

    public void userSignOut() {
        mAuth.signOut();
    }

    public void SignUp(String email, String password, String name, String phoneNumber, SignUpCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Create a User object
                                String userUid = UidGenerator.generateUID();
                                User user = new User(userUid, name, phoneNumber, email, "", new ArrayList<>(), new ArrayList<>());

                                // Save the User object to Firestore
                                firestore.collection("users").document(firebaseUser.getUid())
                                        .set(user)
                                        .addOnCompleteListener(saveTask -> {
                                            if (saveTask.isSuccessful()) {
                                                Log.d(TAG, "User profile saved to Firestore");
                                                callback.onSuccess(firebaseUser);
                                            } else {
                                                Log.w(TAG, "Failed to save user profile", saveTask.getException());
                                                callback.onFailure(saveTask.getException());
                                            }
                                        });
                            } else {
                                callback.onFailure(new Exception("User is null after successful registration"));
                            }
                        } else {
                            // Sign up failed
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

   public FirebaseFirestore getFirestore() {
        return firestore;
   }
}
