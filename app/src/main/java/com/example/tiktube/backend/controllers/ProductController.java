package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.models.Product;
import com.example.tiktube.backend.services.ProductService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProductController {
    private ProductService productService;

    public ProductController() {
        productService = new ProductService();
    }

    public CompletableFuture<List<Product>> getAllProducts() {
        return productService.getAllProducts();
    }
}
