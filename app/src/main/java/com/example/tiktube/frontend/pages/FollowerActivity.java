package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.OtherUserAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OtherUserAdapter userAdapter;
    private List<User> userList;
    private List<User> filteredUserList; // Filtered list for search results

    private UserController userController;

    private User user;
    private ImageView btnBack;
    private SearchView search_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_follow);

        initComponent();

        fetchAndDisplayFollower();

        setupSearch(); // Set up the search functionality
    }

    private void initComponent() {
        btnBack = findViewById(R.id.btnBack);
        search_users = findViewById(R.id.search_users);
        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        user = getIntent().getParcelableExtra("user");

        // Initialize user lists
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>(); // Separate list for filtered results

        userAdapter = new OtherUserAdapter(filteredUserList, FollowerActivity.this, user); // Use filtered list for the adapter
        recyclerView.setAdapter(userAdapter);

        userController = new UserController();
    }

    private void fetchAndDisplayFollower() {
        if (user == null) {
            return; // Ensure the user object is not null
        }

        userController.userGetFollowerList(user)
                .thenAccept(followers -> {
                    runOnUiThread(() -> {
                        userList.clear();
                        userList.addAll(followers);
                        filteredUserList.clear();
                        filteredUserList.addAll(userList); // Initially display all users
                        userAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void setupSearch() {
        search_users.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUserList(query); // Filter based on query
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUserList(newText); // Update results dynamically as user types
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
                    filteredUserList.add(user); // Add matching users
                }
            }
        }
        userAdapter.notifyDataSetChanged(); // Notify adapter of data changes
    }
}
