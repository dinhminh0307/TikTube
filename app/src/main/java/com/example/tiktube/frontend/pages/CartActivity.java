package com.example.tiktube.frontend.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.CartController;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.models.Cart;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.CartAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProductList = new ArrayList<>();
    private TextView totalPriceTextView, numberProduct;

    private CartController cartController;

    private LoginController loginController;

    private User currentUser = new User();

    private Cart userCart = new Cart();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        initComponent();

        // Set up RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load cart products dynamically and set up adapter once complete
        loadCartProducts().thenRun(() -> runOnUiThread(() -> {
            cartAdapter = new CartAdapter(this, cartProductList, this::updateTotalPrice);
            cartRecyclerView.setAdapter(cartAdapter);
            updateTotalPrice();
        }));
    }

    private void initComponent() {
        cartController = new CartController();
        loginController = new LoginController();
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        numberProduct = findViewById(R.id.numberProduct);

        setCurrentUser();
    }

    private CompletableFuture<Void> loadCartProducts() {
        return cartController.getCurrentUserCart(currentUser)
                .thenAccept(cart -> {
                    if (cart != null) {
                        cartProductList.addAll(cart.getCartProducts());
                        numberProduct.setText("Shopping cart " + Integer.toString(cartProductList.size()));
                        userCart.setCart(cart);
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    @SuppressLint("DefaultLocale")
    private void updateTotalPrice() {
        double totalPrice = 0.0;
        for (Product product : cartProductList) {
            totalPrice += product.getPrice() * product.getQuantity();
        }
        userCart.setTotalPrice(totalPrice);
        totalPriceTextView.setText(String.format("Total: %.3fđ", totalPrice));
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
}
