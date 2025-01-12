package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.OtherUserAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OtherUserAdapter userAdapter;
    private List<User> userList;
    private List<User> filteredUserList; // For filtered results

    private UserController userController;

    private User user;
    private ImageView btnBack;

    private SearchView search_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        initComponent();

        // Fetch and display following users
        fetchAndDisplayFollowing();

        // Set up search functionality
        setupSearch();
    }

    private void initComponent() {
        btnBack = findViewById(R.id.btnBack);
        search_users = findViewById(R.id.search_users);
        btnBack.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the passed user object
        user = getIntent().getParcelableExtra("user");

        // Initialize user list and adapter
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>(); // Initialize filtered list
        userAdapter = new OtherUserAdapter(filteredUserList, FollowingActivity.this, user); // Use filtered list for adapter
        recyclerView.setAdapter(userAdapter);

        // Initialize UserController
        userController = new UserController();
    }

    private void fetchAndDisplayFollowing() {
        if (user == null) {
            return; // Ensure the user object is not null
        }

        // Fetch the list of users the current user is following
        userController.userGetFollowingList(user)
                .thenAccept(following -> {
                    // Update the user list on the main thread
                    runOnUiThread(() -> {
                        userList.clear();
                        userList.addAll(following);
                        filteredUserList.clear();
                        filteredUserList.addAll(userList); // Initially display all users
                        userAdapter.notifyDataSetChanged(); // Notify adapter about data changes
                    });
                })
                .exceptionally(e -> {
                    // Handle errors
                    e.printStackTrace();
                    return null;
                });
    }

    private void setupSearch() {
        search_users.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUserList(query); // Filter list based on query
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUserList(newText); // Update filter as user types
                return true;
            }
        });
    }

    private void filterUserList(String query) {
        filteredUserList.clear();
        if (query == null || query.isEmpty()) {
            filteredUserList.addAll(userList); // Show all users if query is empty
        } else {
            for (User user : userList) {
                if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredUserList.add(user); // Add matching users to the filtered list
                }
            }
        }
        userAdapter.notifyDataSetChanged(); // Notify adapter about data changes
    }
}
