package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.models.Product;
import com.example.tiktube.backend.services.SearchService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchController {
    private SearchService searchService;

    public SearchController() {
        this.searchService = new SearchService();
    }

    public CompletableFuture<List<Object>> search(String keyword) {
        return searchService.search(keyword);
    }

    public CompletableFuture<List<Product>> searchProductByName(String keyword) {
        return this.searchService.searchProductByName(keyword);
    }
}
