package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.Interaction;

import java.util.List;

public class InteractionService {
    private FirebaseHelper firebaseHelper;

    private String interaction_collection = "interactions";

    public InteractionService() {
        this.firebaseHelper = new FirebaseHelper();
    }

    public void addInteraction(Interaction interaction,String customUID,  DataFetchCallback<String >cb) {
        firebaseHelper.create(interaction_collection, customUID, interaction, cb);
    }

    public void getAllInteractionsByVideoUID(String videoUID, DataFetchCallback<Interaction> cb) {
        firebaseHelper.getFirestore().collection(interaction_collection)
                .whereEqualTo("videoUID", videoUID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Convert query results directly to Interaction objects
                    List<Interaction> interactions = querySnapshot.toObjects(Interaction.class);
                    cb.onSuccess(interactions);
                })
                .addOnFailureListener(cb::onFailure);
    }



}
