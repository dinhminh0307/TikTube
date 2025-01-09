package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.CartController;
import com.example.tiktube.backend.controllers.ProductController;
import com.example.tiktube.backend.models.Cart;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.utils.UidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private CartController cartController;

    private User currentUser;

    private ProductController productController;

    public ProductAdapter(Context context, List<Product> productList, User user) {
        this.context = context;
        this.productList = productList;
        this.cartController = new CartController();
        this.currentUser = user;
        productController = new ProductController();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        setProductDetails(holder, product);

        holder.itemView.setOnClickListener(v -> showProductDetailsDialog(product));
    }

    private void setProductDetails(ProductViewHolder holder, Product product) {
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("%.2fđ", product.getPrice()));
        holder.productQuantity.setText(String.format("Quantity: %d", product.getQuantity()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_account_circle_foreground)
                .into(holder.productImage);
    }

    private void showProductDetailsDialog(Product product) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_product_details, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);

        initializeDialogView(dialogView, product, builder);
    }

    private void initializeDialogView(View dialogView, Product product, androidx.appcompat.app.AlertDialog.Builder builder) {
        ImageView dialogProductImage = dialogView.findViewById(R.id.dialogProductImage);
        TextView dialogProductName = dialogView.findViewById(R.id.dialogProductName);
        TextView dialogProductPrice = dialogView.findViewById(R.id.dialogProductPrice);
        TextView dialogProductQuantity = dialogView.findViewById(R.id.dialogProductQuantity);
        TextView dialogProductReviews = dialogView.findViewById(R.id.dialogProductReviews);
        TextView dialogTotalPrice = dialogView.findViewById(R.id.dialogTotalPrice);
        android.widget.EditText dialogUserQuantity = dialogView.findViewById(R.id.dialogUserQuantity);

        populateDialogView(dialogProductImage, dialogProductName, dialogProductPrice, dialogProductQuantity, dialogProductReviews, product);

        setupQuantityListener(dialogUserQuantity, product, dialogTotalPrice);

        setupDialogActions(builder, dialogView, dialogUserQuantity, product);
    }

    private void populateDialogView(ImageView dialogProductImage, TextView dialogProductName, TextView dialogProductPrice,
                                    TextView dialogProductQuantity, TextView dialogProductReviews, Product product) {
        Glide.with(context).load(product.getImageUrl()).into(dialogProductImage);
        dialogProductName.setText(product.getName());
        dialogProductPrice.setText(String.format("Price: %.2fđ", product.getPrice()));
        dialogProductQuantity.setText(String.format("Available: %d", product.getQuantity()));
        dialogProductReviews.setText(String.format("Reviews: %d", product.getReviews().size()));
    }

    private void setupQuantityListener(android.widget.EditText dialogUserQuantity, Product product, TextView dialogTotalPrice) {
        dialogUserQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalPrice(dialogUserQuantity, product, dialogTotalPrice);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void calculateTotalPrice(android.widget.EditText dialogUserQuantity, Product product, TextView dialogTotalPrice) {
        int userQuantity = dialogUserQuantity.getText().toString().isEmpty() ? 0 : Integer.parseInt(dialogUserQuantity.getText().toString());
        double totalPrice = userQuantity * product.getPrice();
        dialogTotalPrice.setText(String.format("Total: %.2fđ", totalPrice));
    }

    private void setupDialogActions(androidx.appcompat.app.AlertDialog.Builder builder, View dialogView,
                                    android.widget.EditText dialogUserQuantity, Product product) {
        builder.setView(dialogView)
                .setPositiveButton("Add to Cart", (dialog, which) -> handleAddToCart(dialogUserQuantity, product))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleAddToCart(android.widget.EditText dialogUserQuantity, Product product) {
        String quantityText = dialogUserQuantity.getText().toString().trim();

        if (quantityText.isEmpty()) {
            Toast.makeText(context, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int userQuantity = Integer.parseInt(quantityText);

        if (!validateQuantity(userQuantity, product)) {
            Toast.makeText(context, "Invalid quantity!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Decrease the product quantity in the products collection
        int newQuantity = product.getQuantity() - userQuantity;
        product.setQuantity(newQuantity);

        // Update the quantity on Firebase for the product
        productController.productUpdateQuantity(product)
                .thenAccept(updatedProduct -> {
                    // Update the local product list and notify adapter
                    int position = productList.indexOf(product);
                    if (position >= 0) {
                        productList.set(position, updatedProduct);
                        notifyProductChange(position);
                    }
                })
                .exceptionally(e -> {
                    showToast("Failed to update product quantity on server");
                    e.printStackTrace();
                    return null;
                });

        // Create a new Cart object and add a copy of the product with the user's quantity
        Product cartProduct = createCartProductCopy(product, userQuantity);
        Cart newCart = createCart(userQuantity, cartProduct);

        cartController.getCurrentUserCart(currentUser)
                .thenAccept(existingCart -> {
                    if (existingCart != null) {
                        // Update the existing cart
                        mergeCartProducts(existingCart, newCart);
                        cartController.updateCart(existingCart)
                                .thenAccept(aVoid -> showToast("Cart updated successfully"))
                                .exceptionally(e -> {
                                    showToast("Failed to update cart");
                                    e.printStackTrace();
                                    return null;
                                });
                    } else {
                        // Save the new cart to Firebase
                        saveCartToFirebase(newCart);
                    }
                })
                .exceptionally(e -> {
                    showToast("Error fetching cart");
                    e.printStackTrace();
                    return null;
                });
    }

    private Product createCartProductCopy(Product product, int userQuantity) {
        Product cartProduct = new Product();
        cartProduct.setUid(product.getUid());
        cartProduct.setName(product.getName());
        cartProduct.setPrice(product.getPrice());
        cartProduct.setImageUrl(product.getImageUrl());
        cartProduct.setQuantity(userQuantity); // Set the quantity for the cart
        cartProduct.setReviews(new ArrayList<>(product.getReviews())); // Copy reviews if needed
        return cartProduct;
    }



    private void notifyProductChange(int position) {
        // Notify the adapter about the item change to refresh the RecyclerView
        notifyItemChanged(position);
    }


    private void mergeCartProducts(Cart existingCart, Cart newCart) {
        List<Product> updatedProducts = new ArrayList<>(existingCart.getCartProducts());
        updatedProducts.addAll(newCart.getCartProducts());
        existingCart.setCartProducts(updatedProducts);
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    private boolean validateQuantity(int userQuantity, Product product) {
        return userQuantity <= product.getQuantity() && userQuantity > 0;
    }

    private Cart createCart(int userQuantity, Product product) {
        Cart cart = new Cart();
        cart.setUid(UidGenerator.generateUID());
        product.setQuantity(userQuantity);
        cart.setTotalPrice(userQuantity * product.getPrice());
        cart.setCartProducts(List.of(product));
        cart.setOwnerId(currentUser.getUid());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cart.setDate(LocalDate.now().toString());
        }
        cart.setPurchased(false);
        return cart;
    }

    private void saveCartToFirebase(Cart cart) {
        cartController.createCart(cart)
                .thenAccept(aVoid -> Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show())
                .exceptionally(e -> {
                    Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return null;
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }
}

