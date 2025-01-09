package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.StripeKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StripeKeyService {
    private String key_collection = "keys";
    private FirebaseHelper firebaseHelper;

    public StripeKeyService() {
        this.firebaseHelper = new FirebaseHelper();
    }

    public CompletableFuture<StripeKey> getKey() {
        CompletableFuture<StripeKey> future = new CompletableFuture<>();
        firebaseHelper.findAll(key_collection, StripeKey.class, new DataFetchCallback<StripeKey>() {
            @Override
            public void onSuccess(List<StripeKey> data) {
                StripeKey key = new StripeKey(data.get(0).getUid(), data.get(0).getPrivateKeyStripe(), data.get(0).getPublicKeyStripe());
                future.complete(key);
            }

            @Override
            public void onFailure(Exception e) {
                future.completeExceptionally(e);
            }
        });
                return  future;
    }
}
