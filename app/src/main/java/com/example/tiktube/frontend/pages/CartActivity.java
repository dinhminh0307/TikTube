package com.example.tiktube.frontend.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.CartController;
import com.example.tiktube.backend.controllers.KeyController;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.helpers.StripeHelper;
import com.example.tiktube.backend.models.Cart;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.backend.models.StripeKey;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.CartAdapter;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProductList = new ArrayList<>();
    private TextView totalPriceTextView, numberProduct;
    private ImageView backButton;
    private Button checkoutButton;

    private CartController cartController;
    private LoginController loginController;

    private User currentUser = new User();
    private Cart userCart = new Cart();

    private StripeHelper stripeHelper;
    private PaymentSheet paymentSheet;

    private StripeKey stripeKey = new StripeKey();

    private KeyController keyController;

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

        // Initialize PaymentSheet
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Initialize StripeHelper
        stripeHelper = new StripeHelper(paymentSheet);

        // Set up checkout button
        checkoutButton.setOnClickListener(v -> handleCheckout());
    }

    private void initComponent() {
        cartController = new CartController();
        loginController = new LoginController();
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        backButton = findViewById(R.id.backButton);
        onBackButtonClicked();
        numberProduct = findViewById(R.id.numberProduct);
        checkoutButton = findViewById(R.id.checkoutButton);

        keyController = new KeyController();
        keyController.getKey()
                .thenAccept(k -> {
                    stripeKey.setKey(k);
                    // Initialize PaymentConfiguration
                    PaymentConfiguration.init(this, k.getPublicKeyStripe());
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });

        setCurrentUser();
    }

    private void onBackButtonClicked() {
        backButton.setOnClickListener(v -> finish());
    }

    private CompletableFuture<Void> loadCartProducts() {
        return cartController.getCurrentUserCart(currentUser)
                .thenAccept(cart -> {
                    if (cart != null) {
                        cartProductList.addAll(cart.getCartProducts());
                        numberProduct.setText("Shopping cart " + cartProductList.size());
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

    private void handleCheckout() {
        if (cartProductList.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        stripeHelper.createPaymentIntent(userCart.getTotalPrice(), new StripeHelper.PaymentIntentCallback() {
            @Override
            public void onSuccess(String clientSecret) {
                PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Your Business Name")
                        .allowsDelayedPaymentMethods(true)
                        .build();

                paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(CartActivity.this, "Failed to create PaymentIntent: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onPaymentSheetResult(@NonNull PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
            completeOrder();
        } else if (result instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
        } else if (result instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed failedResult = (PaymentSheetResult.Failed) result;
            Toast.makeText(this, "Payment Failed: " + failedResult.getError().getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void completeOrder() {
        userCart.setPurchased(true);
        cartController.updatePurchasedCart(userCart)
                .thenRun(() -> runOnUiThread(() -> {
                    Toast.makeText(this, "Order completed successfully!", Toast.LENGTH_SHORT).show();
                    // Navigate back to ShopActivity
                    finish();
                }))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

}
