
package com.example.tiktube.backend.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String uid;
    private double totalPrice;
    private List<Product> cartProducts = new ArrayList<>();
    private String date;

    private String ownerId;
    private boolean isPurchased = false;

    // Default constructor
    public Cart() {
    }

    // Constructor with fields
    public Cart(String uid, String ownerId, double totalPrice, List<Product> cartProducts, String date, boolean isPurchased) {
        this.uid = uid;
        this.totalPrice = totalPrice;
        this.cartProducts = cartProducts;
        this.date = date;
        this.isPurchased = isPurchased;
        this.ownerId = ownerId;
    }

    // Getter and Setter for uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    // Getter and Setter for totalPrice
    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Getter and Setter for cartProducts
    public List<Product> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(List<Product> cartProducts) {
        this.cartProducts = cartProducts;
    }

    // Getter and Setter for date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getter and Setter for isPurchased
    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public void setCart(Cart cart) {
        this.uid = cart.getUid();
        this.cartProducts = cart.getCartProducts();
        this.ownerId = cart.getOwnerId();
        this.date = cart.getDate();
        this.totalPrice = cart.getTotalPrice();
        this.isPurchased = cart.isPurchased();
    }
}
