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

    public CompletableFuture<Product> productUpdateQuantity(Product product) {
        return productService.productUpdateQuantity(product);
    }

    public CompletableFuture<Void> addProduct(Product product) {
        return productService.createProduct(product);
    }

    public CompletableFuture<Void> deleteProduct(Product product) {
        return productService.deleteProduct(product);
    }

    public CompletableFuture<Void> updateProduct(Product product) {
        return productService.updateProduct(product);
    }
}
