package com.example.tiktube.backend.services;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.helpers.FirebaseHelper;
import com.example.tiktube.backend.models.Cart;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.backend.models.User;

import java.util.ArrayList;
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

    public CompletableFuture<List<Cart>> getAllCarts() {
        CompletableFuture<List<Cart>> future = new CompletableFuture<>();
        firebaseHelper.findAll(
                cart_collection,
                Cart.class,
                new DataFetchCallback<Cart>() {
                    @Override
                    public void onSuccess(List<Cart> data) {
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

    public CompletableFuture<Cart> getCurrentUserCart(User user) {
        CompletableFuture<Cart> future = new CompletableFuture<>();

        getAllCarts()
                .thenAccept(carts -> {
                    for (Cart c : carts) {
                        if (c.getOwnerId() != null && c.getOwnerId().equals(user.getUid()) && !c.isPurchased()) {
                            future.complete(c);
                            return; // Exit loop once the matching cart is found
                        }
                    }
                    // If no matching cart is found, complete the future with null or a specific exception
                    future.complete(null); // Or throw an exception if a cart must exist
                })
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });

        return future;
    }

    public CompletableFuture<Void> updatePurchasedCart(Cart cart) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        cart.setPurchased(true);
        firebaseHelper.updateField(
                cart.getUid(),
                cart_collection,
                "purchased",
                cart.isPurchased()
        );
        future.complete(null);
        return future;
    }

    public CompletableFuture<Void> updateCart(Cart cart) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        // update the cart we update the product and total price
        double newTotalPrice = 0;
        List<Product> cartProduct = new ArrayList<>();
        cartProduct.addAll(cart.getCartProducts());

        for(Product p : cartProduct) {
            newTotalPrice += p.getPrice() * p.getQuantity();
        }
        cart.setTotalPrice(newTotalPrice);
        firebaseHelper.updateField(
                cart.getUid(),
                cart_collection,
                "totalPrice",
                cart.getTotalPrice()
        );

        //update to product list in cart
        firebaseHelper.updateField(
                cart.getUid(),
                cart_collection,
                "cartProducts",
                cartProduct
        );
        future.complete(null);
        return future;
    }

}
