package com.example.tiktube.backend.firebase;

import android.util.Log;

import com.example.tiktube.backend.models.Video;
import com.google.firebase.firestore.FirebaseFirestore;

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
}
