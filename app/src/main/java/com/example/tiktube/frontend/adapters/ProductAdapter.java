package com.example.tiktube.frontend.adapters;

import android.content.Context;
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
import com.example.tiktube.backend.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
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

        // Set product details
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("%.2fđ", product.getPrice()));
        holder.productQuantity.setText(String.format("Quantity: %d", product.getQuantity()));

        // Load product image using Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_account_circle_foreground) // Replace with your placeholder image
                .into(holder.productImage);

        // Handle product item click
        holder.itemView.setOnClickListener(v -> showProductDetailsDialog(product));
    }

    private void showProductDetailsDialog(Product product) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_product_details, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);

        // Initialize dialog views
        ImageView dialogProductImage = dialogView.findViewById(R.id.dialogProductImage);
        TextView dialogProductName = dialogView.findViewById(R.id.dialogProductName);
        TextView dialogProductPrice = dialogView.findViewById(R.id.dialogProductPrice);
        TextView dialogProductQuantity = dialogView.findViewById(R.id.dialogProductQuantity);
        TextView dialogProductReviews = dialogView.findViewById(R.id.dialogProductReviews);
        TextView dialogTotalPrice = dialogView.findViewById(R.id.dialogTotalPrice);
        android.widget.EditText dialogUserQuantity = dialogView.findViewById(R.id.dialogUserQuantity);

        // Set product details
        Glide.with(context).load(product.getImageUrl()).into(dialogProductImage);
        dialogProductName.setText(product.getName());
        dialogProductPrice.setText(String.format("Price: %.2fđ", product.getPrice()));
        dialogProductQuantity.setText(String.format("Available: %d", product.getQuantity()));
        dialogProductReviews.setText(String.format("Reviews: %d", product.getReviews().size()));

        // Calculate total price based on user input
        dialogUserQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int userQuantity = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                double totalPrice = userQuantity * product.getPrice();
                dialogTotalPrice.setText(String.format("Total: %.2fđ", totalPrice));
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        builder.setView(dialogView)
                .setPositiveButton("Add to Cart", (dialog, which) -> {
                    int userQuantity = Integer.parseInt(dialogUserQuantity.getText().toString());
                    if (userQuantity <= product.getQuantity() && userQuantity > 0) {
                        Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Invalid quantity!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
