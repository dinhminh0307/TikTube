package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.services.InteractionService;

public class InteractionController {
    private InteractionService interactionService;

    public InteractionController() {
        this.interactionService = new InteractionService();
    }

    public void getAllInteractionsByVideoUID(String videoUID, DataFetchCallback<Interaction> cb) {
        interactionService.getAllInteractionsByVideoUID(videoUID, cb);
    }
}
