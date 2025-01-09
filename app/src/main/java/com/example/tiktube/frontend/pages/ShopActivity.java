package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    private LoginController loginController;

    private ImageView cartIcon;

    private ProductController productController =  new ProductController();

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
    }

    private void initComponent() {
        cartIcon = findViewById(R.id.cartIcon);
        loginController = new LoginController();

        setCurrentUser();
    }

    private void onCartIconClicked() {
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProducts() {
        productController.getAllProducts()
                .thenAccept(p -> {
                    productList.addAll(p);
                    productAdapter = new ProductAdapter(this, productList, currentUser);
                    productRecyclerView.setAdapter(productAdapter);
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

            }
        });
    }



}
