package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.ProductController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.helpers.ImageBuilder;
import com.example.tiktube.backend.models.Product;
import com.example.tiktube.backend.utils.UidGenerator;
import com.example.tiktube.frontend.adapters.AdminProductAdapter;
import com.example.tiktube.frontend.adapters.UserListAdapter;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private UserListAdapter userListAdapter;
    private RecyclerView rvContentList, productListContent;
    private Button btnUserList, btnProductList, btnAddProduct;
    private UserController userController;

    private ProductController productController;

    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        initializeControllers();

        initializeViews();


        setButtonListeners();
    }

    private void initializeViews() {
        rvContentList = findViewById(R.id.recyclerViewContent);
        rvContentList.setLayoutManager(new LinearLayoutManager(this));

        productListContent = findViewById(R.id.productListContent);
        productListContent.setLayoutManager(new LinearLayoutManager(this));
        btnUserList = findViewById(R.id.btnUserList);
        btnProductList = findViewById(R.id.btnProductList);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        // Initialize UserListHelper
        userListAdapter = new UserListAdapter(this, userController);

        loadDefaultView();
    }

    private void initializeControllers() {
        userController = new UserController();
        productController = new ProductController();
    }


    private void setButtonListeners() {
        btnUserList.setOnClickListener(v -> {
            fetchAndDisplayUsers();
            btnAddProduct.setVisibility(View.GONE); // Hide Add Product button
            rvContentList.setVisibility(View.VISIBLE); // Show User List content
            productListContent.setVisibility(View.GONE); // Hide Product List content
        });

        btnProductList.setOnClickListener(v -> {
            fetchAndDisplayProducts();
            btnAddProduct.setVisibility(View.VISIBLE); // Show Add Product button
            productListContent.setVisibility(View.VISIBLE); // Show Product List content
            rvContentList.setVisibility(View.GONE); // Hide User List content
        });

        btnAddProduct.setOnClickListener(v -> showAddProductDialog());
    }


    private void showAddProductDialog() {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);

        // Bind views
        EditText productName = dialogView.findViewById(R.id.editProductName);
        EditText productImageUrl = dialogView.findViewById(R.id.editProductImageLink);
        EditText productPrice = dialogView.findViewById(R.id.editProductPrice);
        EditText productQuantity = dialogView.findViewById(R.id.editProductQuantity);
        Button addButton = dialogView.findViewById(R.id.btnAddProduct);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);

        // Create and show the dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        addButton.setOnClickListener(v -> {
            // Create a new product
            Product newProduct = new Product();
            newProduct.setName(productName.getText().toString().trim());
            newProduct.setPrice(Double.parseDouble(productPrice.getText().toString().trim()));
            newProduct.setQuantity(Integer.parseInt(productQuantity.getText().toString().trim()));
            newProduct.setImageUrl(productImageUrl.getText().toString());
            newProduct.setUid(UidGenerator.generateUID());
            // Add the product to Firebase
            productController.addProduct(newProduct)
                    .thenRun(() -> runOnUiThread(() -> {
                        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        fetchAndDisplayProducts(); // Refresh the product list
                        dialog.dismiss();
                    }))
                    .exceptionally(e -> {
                        runOnUiThread(() -> Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show());
                        e.printStackTrace();
                        return null;
                    });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void fetchAndDisplayProducts() {
        productController.getAllProducts()
                .thenAccept(products -> runOnUiThread(() -> {
                    if(products.isEmpty()) {
                        displayEmptyProductList();
                        return;
                    }
                    AdminProductAdapter adapter = new AdminProductAdapter(products, this, this::onProductUpdate, this::onProductDelete);
                    productListContent.setAdapter(adapter);
                    productListContent.setVisibility(View.VISIBLE); // Ensure Product List is visible
                    rvContentList.setVisibility(View.GONE); // Hide User List
                }))
                .exceptionally(e -> {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(AdminActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show());
                    return null;
                });
    }




    private void onProductUpdate(Product product) {
        showUpdateProductDialog(product); // Implement a method to handle product updates
    }

    private void onProductDelete(Product product) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    productController.deleteProduct(product)
                            .thenRun(() -> runOnUiThread(() -> {
                                Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                                fetchAndDisplayProducts(); // Refresh the product list
                            }))
                            .exceptionally(e -> {
                                runOnUiThread(() -> Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show());
                                e.printStackTrace();
                                return null;
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }



    private void showUpdateProductDialog(Product product) {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_product, null);

        // Bind views
        EditText productName = dialogView.findViewById(R.id.editProductName);
        EditText productPrice = dialogView.findViewById(R.id.editProductPrice);
        EditText productQuantity = dialogView.findViewById(R.id.editProductQuantity);
        Button updateButton = dialogView.findViewById(R.id.btnUpdateProduct);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);

        // Pre-fill fields with the current product details
        productName.setText(product.getName());
        productPrice.setText(String.valueOf(product.getPrice()));
        productQuantity.setText(String.valueOf(product.getQuantity()));

        // Create and show the dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        updateButton.setOnClickListener(v -> {
            // Update the product with new values
            product.setName(productName.getText().toString().trim());
            product.setPrice(Double.parseDouble(productPrice.getText().toString().trim()));
            product.setQuantity(Integer.parseInt(productQuantity.getText().toString().trim()));

            // Update the product in Firebase
            productController.updateProduct(product)
                    .thenRun(() -> runOnUiThread(() -> {
                        Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        fetchAndDisplayProducts(); // Refresh the product list
                        dialog.dismiss();
                    }))
                    .exceptionally(e -> {
                        runOnUiThread(() -> Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show());
                        e.printStackTrace();
                        return null;
                    });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }



    private void loadDefaultView() {
        fetchAndDisplayUsers(); // Load the user list by default
        btnAddProduct.setVisibility(View.GONE); // Hide Add Product button by default
        productListContent.setVisibility(View.GONE); // Hide Product List content by default
        rvContentList.setVisibility(View.VISIBLE); // Show User List content by default
    }

    private void fetchAndDisplayUsers() {
        userController.getAllUsers(new DataFetchCallback<User>() {
            @Override
            public void onSuccess(List<User> users) {
                if (users.isEmpty()) {
                    Toast.makeText(AdminActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                    rvContentList.setAdapter(null);
                    return;
                }

                UserAdapter adapter = new UserAdapter(users, AdminActivity.this, user -> {
                    showUserOptionsDialog(user);
                });
                rvContentList.setAdapter(adapter);
                rvContentList.setVisibility(View.VISIBLE); // Ensure User List is visible
                productListContent.setVisibility(View.GONE); // Hide Product List
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }



    private void displayEmptyProductList() {
        // Temporary empty state for products
        Toast.makeText(this, "Product list is under development", Toast.LENGTH_SHORT).show();
        productListContent.setAdapter(null); // Set RecyclerView to show nothing
    }

    private void showUserOptionsDialog(User user) {
        // Dialog to handle options for a user
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Options for " + user.getName())
                .setItems(new String[]{"View", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // View user
                            viewUser(user);
                            break;
                        case 1: // Delete user
                            deleteUser(user);
                            break;
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void viewUser(User user) {
        // Inflate the custom layout
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        android.view.View view = inflater.inflate(R.layout.dialog_user_details, null);

        // Bind views
        ImageView imgUser = view.findViewById(R.id.imgUser);
        TextView tvUserInfo = view.findViewById(R.id.tvUserInfo);
        LinearLayout llUserLists = view.findViewById(R.id.llUserLists);
        Button btnClose = view.findViewById(R.id.btnClose);

        // Load the user's image
        ImageBuilder imageBuilder = new ImageBuilder(this);
        imageBuilder.loadImage(imgUser, user);

        // Build user details
        String userInfo = "UID: " + user.getUid() + "\n" +
                "Name: " + user.getName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Phone: " + user.getPhoneNumber() + "\n" +
                "Bio: " + user.getBio() + "\n" +
                "Instagram: " + user.getInstagram() + "\n" +
                "Facebook: " + user.getFacebook() + "\n";

        tvUserInfo.setText(userInfo);

        // Add list summaries
        userListAdapter.addListSummary(llUserLists, "Own Videos", user.getOwnVideo().size());
        userListAdapter.addListSummary(llUserLists, "Interacted Videos", user.getInteractedVideo().size());
        userListAdapter.addListSummary(llUserLists, "Liked Videos", user.getLikesVideo().size());

        // Fetch and display names in table format for Following and Followers
        userListAdapter.fetchAndDisplayNamesAsTable(llUserLists, "Following", user.getFollowingList());
        userListAdapter.fetchAndDisplayNamesAsTable(llUserLists, "Followers", user.getFollowerList());

        // Show the dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void deleteUser(User user) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getName() + "? This action cannot be undone.")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    userController.deleteUser(user, new DataFetchCallback<Void>() {
                        @Override
                        public void onSuccess(List<Void> data) {
                            Toast.makeText(AdminActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchAndDisplayUsers(); // Refresh the list
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(AdminActivity.this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
