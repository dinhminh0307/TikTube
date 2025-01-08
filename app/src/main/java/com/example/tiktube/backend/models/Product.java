package com.example.tiktube.backend.models;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private String uid;
    private double price;
    private String imageUrl;
    private int quantity;
    private String name;
    private List<String> reviews = new ArrayList<>();

    // Constructor
    public Product() {
    }

    public Product(String uid, String name, double price, String imageUrl, int quantity, List<String> reviews) {
        this.uid = uid;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.reviews = reviews;
        this.name = name;
    }

    // Getter and Setter for uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter and Setter for price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter and Setter for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter and Setter for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for reviews
    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
