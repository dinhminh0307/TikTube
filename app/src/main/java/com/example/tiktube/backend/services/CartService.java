package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.Cart;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CartService {
    private FirebaseHelper firebaseHelper;

    private String cart_collection = "carts";

    public CartService() {
        this.firebaseHelper = new FirebaseHelper();
    }

    public CompletableFuture<Void> createCart(Cart cart) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        firebaseHelper.create(
                cart_collection,
                cart.getUid(),
                cart,
                new DataFetchCallback<String>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        future.complete(null);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        future.completeExceptionally(e);
                    }
                }
        );
        return future;
    }
}
