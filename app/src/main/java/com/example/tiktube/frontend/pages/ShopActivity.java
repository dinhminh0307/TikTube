package com.example.tiktube.frontend.pages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.ProductController;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.frontend.adapters.GridSpacingItemDecoration;
import com.example.tiktube.frontend.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    private ProductController productController =  new ProductController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Initialize RecyclerView
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        // Add gap between items using ItemDecoration
        int gap = getResources().getDimensionPixelSize(R.dimen.product_item_gap);
        productRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, gap, true));

        // Load products dynamically
        loadProducts();

        // Set adapter

    }

    private void loadProducts() {
        productController.getAllProducts()
                .thenAccept(p -> {
                    productList.addAll(p);
                    productAdapter = new ProductAdapter(this, productList);
                    productRecyclerView.setAdapter(productAdapter);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

}
