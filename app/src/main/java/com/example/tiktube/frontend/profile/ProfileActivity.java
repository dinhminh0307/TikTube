package com.example.tiktube.frontend.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tiktube.R;
import com.example.tiktube.backend.models.User;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    TextView nameID;
    User userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        userData = getIntent().getParcelableExtra("user");
        nameID = findViewById(R.id.nameID);
        nameID.setText(userData.getName());
        FirebaseAuth.getInstance().signOut(); //call to signout temp
    }
}