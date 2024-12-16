package com.example.tiktube;

import android.content.Intent;
import android.os.Bundle;

import com.example.tiktube.api.AuthAPI;
import com.example.tiktube.backend.callbacks.GetUserCallback;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.frontend.login.LoginActivity;
import com.example.tiktube.frontend.profile.ProfileActivity;
import com.example.tiktube.frontend.register.RegisterActivity;
import com.example.tiktube.frontend.search.SearchActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tiktube.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    Button signUpBtn;

    AuthAPI authAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        authAPI = new AuthAPI();

        onSignUpButtonClicked();
        onLoginButtonClicked();

        Button btnTestSearch = findViewById(R.id.btnTestSearch);

        btnTestSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in and update UI accordingly.
        authAPI.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d("Login", "User retrieved: " + user.getName());
                if (user != null) {
                    // Navigate to ProfileActivity
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
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

}