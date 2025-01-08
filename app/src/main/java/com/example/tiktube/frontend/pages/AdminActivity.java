package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.helpers.ImageBuilder;
import com.example.tiktube.backend.helpers.UserListHelper;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.UserAdapter;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private UserListHelper userListHelper;
    private RecyclerView rvContentList;
    private Button btnUserList, btnProductList;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        initializeViews();
        initializeControllers();

        // Initialize UserListHelper
        userListHelper = new UserListHelper(this, userController);

        setButtonListeners();
        loadDefaultView();
    }

    private void initializeViews() {
        rvContentList = findViewById(R.id.recyclerViewContent);
        rvContentList.setLayoutManager(new LinearLayoutManager(this));
        btnUserList = findViewById(R.id.btnUserList);
        btnProductList = findViewById(R.id.btnProductList);
    }

    private void initializeControllers() {
        userController = new UserController();
    }

    private void setButtonListeners() {
        btnUserList.setOnClickListener(v -> fetchAndDisplayUsers());
        btnProductList.setOnClickListener(v -> displayEmptyProductList());
    }

    private void loadDefaultView() {
        fetchAndDisplayUsers();
    }

    private void fetchAndDisplayUsers() {
        userController.getAllUsers(new DataFetchCallback<User>() {
            @Override
            public void onSuccess(List<User> users) {
                UserAdapter adapter = new UserAdapter(users, AdminActivity.this, user -> {
                    showUserOptionsDialog(user); // Restored info button functionality
                });
                rvContentList.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEmptyProductList() {
        // Temporary empty state for products
        Toast.makeText(this, "Product list is under development", Toast.LENGTH_SHORT).show();
        rvContentList.setAdapter(null); // Set RecyclerView to show nothing
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
        userListHelper.addListSummary(llUserLists, "Own Videos", user.getOwnVideo().size());
        userListHelper.addListSummary(llUserLists, "Interacted Videos", user.getInteractedVideo().size());
        userListHelper.addListSummary(llUserLists, "Liked Videos", user.getLikesVideo().size());

        // Fetch and display names in table format for Following and Followers
        userListHelper.fetchAndDisplayNamesAsTable(llUserLists, "Following", user.getFollowingList());
        userListHelper.fetchAndDisplayNamesAsTable(llUserLists, "Followers", user.getFollowerList());

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
