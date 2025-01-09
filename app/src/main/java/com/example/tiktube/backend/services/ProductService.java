package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.Product;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProductService {
    FirebaseHelper firebaseHelper;
    private String product_collection = "products";

    public ProductService() {
        this.firebaseHelper = new FirebaseHelper();
    }

    public CompletableFuture<List<Product>> getAllProducts() {
        CompletableFuture<List<Product>> future = new CompletableFuture<>();

        firebaseHelper.findAll(
                product_collection,
                Product.class,
                new DataFetchCallback<Product>() {
                    @Override
                    public void onSuccess(List<Product> data) {
                        future.complete(data);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        future.completeExceptionally(e);
                    }
                }
        );
        return future;
    }

    public CompletableFuture<Product> productUpdateQuantity(Product product) {
        CompletableFuture<Product> future = new CompletableFuture<>();
        firebaseHelper.updateField(
                product.getUid(),
                product_collection,
                "quantity",
                product.getQuantity()
        );
        future.complete(product);
        return future;
    }
}
