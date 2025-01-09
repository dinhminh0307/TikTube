package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktube.R;
import com.example.tiktube.backend.models.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product> productList;
    private Runnable updateTotalPriceCallback;

    public CartAdapter(Context context, List<Product> productList, Runnable updateTotalPriceCallback) {
        this.context = context;
        this.productList = productList;
        this.updateTotalPriceCallback = updateTotalPriceCallback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("%.3fđ", product.getPrice()));
        holder.productQuantity.setText(String.valueOf(product.getQuantity()));

        // Load product image using Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_account_circle_foreground) // Placeholder image
                .into(holder.productImage);

        // Handle increase quantity
        holder.increaseQuantity.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            notifyItemChanged(position);
            updateTotalPriceCallback.run();
        });

        // Handle decrease quantity
        holder.decreaseQuantity.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1);
                notifyItemChanged(position);
                updateTotalPriceCallback.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity;
        ImageButton increaseQuantity, decreaseQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity);
            decreaseQuantity = itemView.findViewById(R.id.decreaseQuantity);
        }
    }
}
