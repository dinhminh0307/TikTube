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

    public CompletableFuture<Void> createProduct(Product product) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        firebaseHelper.create(
                product_collection,
                product.getUid(),
                product,
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

    public CompletableFuture<Void> deleteProduct(Product product) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        firebaseHelper.deleteDocument(
                product_collection,
                product.getUid(),
                new DataFetchCallback<Void>() {
                    @Override
                    public void onSuccess(List<Void> data) {
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

    public CompletableFuture<Void> updateProduct(Product product) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        // update name
        firebaseHelper.updateField(
                product.getUid(),
                product_collection,
                "name",
                product.getName()
        );
        //update price
        firebaseHelper.updateField(
                product.getUid(),
                product_collection,
                "price",
                product.getPrice()
        );
        // update quantity
        firebaseHelper.updateField(
                product.getUid(),
                product_collection,
                "quantity",
                product.getQuantity()
        );
        future.complete(null);
        return future;
    }
}
