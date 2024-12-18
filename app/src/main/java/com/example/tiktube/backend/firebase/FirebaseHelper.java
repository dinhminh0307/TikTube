package com.example.tiktube.backend.firebase;

import android.util.Log;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.models.Video;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FirebaseHelper {
    private FirebaseFirestore firestore;

    public FirebaseHelper() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void create(String collections, Object obj) {
        // Add the data to the "bloodDonationSites" collection
        firestore.collection(collections)
                .add(obj)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "object site added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding object site", e);
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



}
