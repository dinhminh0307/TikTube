package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.models.StripeKey;
import com.example.tiktube.backend.services.StripeKeyService;

import java.util.concurrent.CompletableFuture;

public class KeyController {
    private StripeKeyService stripeKeyService;

    public KeyController() {
        stripeKeyService = new StripeKeyService();
    }

    public CompletableFuture<StripeKey> getKey() {
        return this.stripeKeyService.getKey();
    }
}
