package com.example.tiktube.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktube.R;
import com.example.tiktube.backend.models.Product;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {

    private final List<Product> productList;
    private final Context context;
    private final OnProductActionListener updateListener;
    private final OnProductActionListener deleteListener;

    public interface OnProductActionListener {
        void onAction(Product product);
    }

    public AdminProductAdapter(List<Product> productList, Context context,
                               OnProductActionListener updateListener,
                               OnProductActionListener deleteListener) {
        this.productList = productList;
        this.context = context;
        this.updateListener = updateListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_product, parent, false);
        return new AdminProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.adminProductName.setText(product.getName());
        holder.adminProductPrice.setText(String.format("Price: %.2f", product.getPrice()));
        holder.adminProductQuantity.setText(String.format("Quantity: %d", product.getQuantity()));
        Glide.with(context)
                .load(product.getImageUrl()) // Replace with your product's image URL
                .placeholder(R.drawable.ic_account_circle_foreground) // Placeholder image
                .into(holder.productImageView);


        holder.adminUpdateProduct.setOnClickListener(v -> updateListener.onAction(product));
        holder.adminDeleteProduct.setOnClickListener(v -> deleteListener.onAction(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class AdminProductViewHolder extends RecyclerView.ViewHolder {
        TextView adminProductName, adminProductPrice, adminProductQuantity;
        Button adminUpdateProduct, adminDeleteProduct;

        ImageView productImageView;

        public AdminProductViewHolder(@NonNull View itemView) {
            super(itemView);
            adminProductName = itemView.findViewById(R.id.adminProductName);
            adminProductPrice = itemView.findViewById(R.id.adminProductPrice);
            adminProductQuantity = itemView.findViewById(R.id.adminProductQuantity);
            adminUpdateProduct = itemView.findViewById(R.id.adminUpdateProduct);
            adminDeleteProduct = itemView.findViewById(R.id.adminDeleteProduct);
            productImageView = itemView.findViewById(R.id.adminProductImage);
        }
    }
}
