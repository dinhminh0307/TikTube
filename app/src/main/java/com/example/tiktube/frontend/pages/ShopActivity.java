package com.example.tiktube.frontend.pages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.frontend.adapters.GridSpacingItemDecoration;
import com.example.tiktube.frontend.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

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
        productAdapter = new ProductAdapter(this, productList);
        productRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        productList = new ArrayList<>();

        // Example products (replace with dynamic data)
        productList.add(new Product("1", "Product 1", 100.0, "https://via.placeholder.com/150", 10, new ArrayList<>()));
        productList.add(new Product("2", "Product 2", 200.0, "https://via.placeholder.com/150", 5, new ArrayList<>()));
        productList.add(new Product("3", "Product 3", 300.0, "https://via.placeholder.com/150", 8, new ArrayList<>()));
        productList.add(new Product("4", "Product 4", 400.0, "https://via.placeholder.com/150", 12, new ArrayList<>()));
        productList.add(new Product("5", "Product 5", 500.0, "https://via.placeholder.com/150", 7, new ArrayList<>()));
    }
}
