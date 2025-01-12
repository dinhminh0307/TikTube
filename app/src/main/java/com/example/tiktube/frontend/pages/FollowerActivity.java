package com.example.tiktube.frontend.pages;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

    private UserController userController;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_follower);

        initComponent();

        fetchAndDisplayFollower();
    }

    private void initComponent() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        user = getIntent().getParcelableExtra("user");

        // Initialize user list
        userList = new ArrayList<>();
        userAdapter = new OtherUserAdapter(userList, FollowerActivity.this, user);
        recyclerView.setAdapter(userAdapter);

        userController = new UserController();
    }

    private void fetchAndDisplayFollower() {
        if (user == null) {
            return; // Ensure the user object is not null
        }

        // Fetch the list of users the current user is following
        userController.userGetFollowerList(user)
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