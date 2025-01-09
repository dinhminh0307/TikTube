package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.models.Cart;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.services.CartService;

import java.util.concurrent.CompletableFuture;

public class CartController {
    private CartService cartService;

    public CartController() {
        this.cartService = new CartService();
    }

    public CompletableFuture<Void> createCart(Cart cart) {
        return cartService.createCart(cart);
    }

    public CompletableFuture<Cart> getCurrentUserCart(User user) {
        return this.cartService.getCurrentUserCart(user);
    }

    public CompletableFuture<Void> updateCart(Cart cart) {
        return this.cartService.updateCart(cart);
    }

    public  CompletableFuture<Void> updatePurchasedCart(Cart cart) {
        return this.cartService.updatePurchasedCart(cart);
    }
}
