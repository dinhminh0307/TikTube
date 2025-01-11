package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.ProductController;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.GridSpacingItemDecoration;
import com.example.tiktube.frontend.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> filteredProductList = new ArrayList<>();

    private LoginController loginController;

    private ImageView btnBack, cartIcon;
    private SearchView searchInput;

    private ProductController productController = new ProductController();

    private User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        initComponent();

        // Initialize RecyclerView
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        // Add gap between items using ItemDecoration
        int gap = getResources().getDimensionPixelSize(R.dimen.product_item_gap);
        productRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, gap, true));

        // Load products dynamically
        loadProducts();

        // Set adapter
        onCartIconClicked();

        // Set up search functionality
        setupSearch();
    }

    private void initComponent() {
        btnBack = findViewById(R.id.btnBack);
        searchInput = findViewById(R.id.searchInput);
        cartIcon = findViewById(R.id.cartIcon);
        loginController = new LoginController();

        btnBack.setOnClickListener(v -> finish());

        setCurrentUser();
    }

    private void onCartIconClicked() {
        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void loadProducts() {
        productController.getAllProducts()
                .thenAccept(p -> {
                    productList.clear();
                    for (Product product : p) {
                        if (product.getQuantity() > 0) {
                            productList.add(product);
                        }
                    }
                    filteredProductList.addAll(productList); // Copy all products for filtering
                    runOnUiThread(() -> {
                        productAdapter = new ProductAdapter(this, filteredProductList, currentUser);
                        productRecyclerView.setAdapter(productAdapter);
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void setCurrentUser() {
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.setUser(user);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupSearch() {
        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });
    }

    private void filterProducts(String query) {
        String lowerCaseQuery = query.toLowerCase();
        filteredProductList.clear();

        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                filteredProductList.add(product);
            }
        }

        // Update the adapter with the filtered list
        productAdapter.notifyDataSetChanged();
    }
}
