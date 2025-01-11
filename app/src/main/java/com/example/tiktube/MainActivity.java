package com.example.tiktube;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.pages.LoginActivity;
import com.example.tiktube.frontend.pages.RegisterActivity;
import com.example.tiktube.frontend.pages.VideoPageActivity;
import com.example.tiktube.frontend.pages.AdminActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    Button signUpBtn;
    Button viewUsersBtn; // New Button for viewing users

    LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        loginController = new LoginController();

//        Save display mode status
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
//        Apply display mode on launch
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        onSignUpButtonClicked();
        onLoginButtonClicked();
//        onViewUsersButtonClicked(); // Initialize the new button
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in and update UI accordingly.
        loginController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d("Login", "User retrieved: " + user.getName());
                if (user != null) {
                    // Navigate to ProfileActivity
                    Intent intent = new Intent(MainActivity.this, VideoPageActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish(); // Optional: Finish MainActivity to prevent going back
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Login", "Error: " + e.getMessage());
                // Remain in the current activity (MainActivity)
            }
        });
    }

    private void onSignUpButtonClicked() {
        signUpBtn = findViewById(R.id.signUpButton);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onLoginButtonClicked() {
        loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // New Method for "View Users" Button
//    private void onViewUsersButtonClicked() {
//        viewUsersBtn = findViewById(R.id.viewAdminPage);
//        viewUsersBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
}
