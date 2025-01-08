package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.models.Cart;
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
}
