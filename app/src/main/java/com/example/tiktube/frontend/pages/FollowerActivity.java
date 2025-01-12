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
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.adapters.OtherUserAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OtherUserAdapter userAdapter;
    private List<User> userList;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_follower);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        user = getIntent().getParcelableExtra("user");

        // Initialize user list
        userList = new ArrayList<>();

        // Add sample users
        userList.add(new User("1", "Hanoi Nèee", "1234567890", "email@example.com",
                "https://example.com/image1.jpg", new ArrayList<>(), new ArrayList<>()));
        userList.add(new User("2", "Vic~thor ⚽", "9876543210", "email2@example.com",
                "https://example.com/image2.jpg", new ArrayList<>(), new ArrayList<>()));
        userList.add(new User("3", "Tiệm Đồ Chơi 1996", "5556667777", "email3@example.com",
                "https://example.com/image3.jpg", new ArrayList<>(), new ArrayList<>()));

        // Pass user list to the adapter
        userAdapter = new OtherUserAdapter(userList, FollowerActivity.this);
        recyclerView.setAdapter(userAdapter);
    }
}