package com.example.tiktube.frontend.pages;

import android.os.Bundle;

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

    private UserController userController;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        initComponent();

        // Fetch and display following users
        fetchAndDisplayFollowing();
    }

    private void initComponent() {
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize user list and adapter
        userList = new ArrayList<>();
        userAdapter = new OtherUserAdapter(userList, FollowingActivity.this);
        recyclerView.setAdapter(userAdapter);

        // Initialize UserController
        userController = new UserController();

        // Get the passed user object
        user = getIntent().getParcelableExtra("user");
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
                        userAdapter.notifyDataSetChanged(); // Notify adapter about data changes
                    });
                })
                .exceptionally(e -> {
                    // Handle errors
                    e.printStackTrace();
                    return null;
                });
    }
}
