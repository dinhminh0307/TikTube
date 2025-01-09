package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.frontend.adapters.CartAdapter;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProductList = new ArrayList<>();
    private TextView totalPriceTextView;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        backButton = findViewById(R.id.backButton);
        onBackButtonClicked();

        // Set up RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load cart products dynamically
        loadCartProducts();

        // Set adapter
        cartAdapter = new CartAdapter(this, cartProductList, this::updateTotalPrice);
        cartRecyclerView.setAdapter(cartAdapter);

        // Update total price
        updateTotalPrice();
    }

    private void onBackButtonClicked() {
        backButton.setOnClickListener(v -> finish());
    }

    private void loadCartProducts() {
        // Example products (replace this with dynamic data)
        cartProductList.add(new Product("1", "Product 1", 158.699, "https://via.placeholder.com/150", 4, new ArrayList<>()));
        cartProductList.add(new Product("2", "Product 2", 189.100, "https://via.placeholder.com/150", 2, new ArrayList<>()));
        cartProductList.add(new Product("3", "Product 3", 230.000, "https://via.placeholder.com/150", 6, new ArrayList<>()));
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (Product product : cartProductList) {
            totalPrice += product.getPrice() * product.getQuantity();
        }
        totalPriceTextView.setText(String.format("Total: %.3fđ", totalPrice));
    }
}
